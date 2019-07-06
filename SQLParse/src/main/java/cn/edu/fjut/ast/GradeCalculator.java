package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;
import tree.GenTreeGraph;

/**
 * 这个类完成打分操作
 * 需要注意是否无视大小写，当大小写有区分时，　from Table 与 from table两者是不一样的。
 * @author admin-u1064462
 *
 */
public class GradeCalculator
{
	DBHelper dbHelper = DBHelper.getInstance();
	public static final boolean IGNORE_ORDERBY = true; //Ignore the order by clause
	public static final boolean IGNORE_CASE = true;  // Ignore the case of sql statmement
	MySQLParse parser;

	public GradeCalculator()
	{
		parser = new MySQLParse();
	}

	static int count = 0;

	public static void main(String[] args)
	{
		GradeCalculator calScore = new GradeCalculator();
		
		int i = 1;
		for (; i <= GlobalSetting.MAX_EXERCISE_ID; i++)
			calScore.calExerciseScore(i);
		System.out.println(count);

	}

	// with year_born as (select * from person natural join writer where year_born =
	// 1935), id as (select distinct id from year_born) select count(*) from id

	private void calScoreTest()
	{
		String sql = "SELECT first_name, last_name \n" + 
				"FROM person p INNER JOIN writer w\n" + 
				"ON w.id = p.id\n" + 
				"WHERE p.year_born = 1935;";
		String ref = "select count(distinct p.first_name) from writer w inner join person p on w.id = p.id where p.year_born = 1935;";
		MySQLParse parse = new MySQLParse();
		SQLTree tree = parse.parse(ref);
		List<SQLTree> trees = new ArrayList<>();
		trees.add(tree);
		float result = calBestScore(sql, trees);
		System.out.println(result);

	}

	public void calExerciseScore(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where is_correct = 0 and exercisesubmission_ptr_id not in "
						+ "( select id from exercise_remark where remark = 'noninterpretable' )"
						+ " and  exercise_id =  " + exercise_id); 																														
		String sql ;
		List<SQLTree> refTrees = getRefTrees(exercise_id);
		
		for (ExerciseSubmission submission : submissions)
		{
			sql = submission.getSubmitted_answer(); 
			if(IGNORE_CASE)
				sql = sql.toLowerCase();
			float score = calBestScore(sql, refTrees);			
			submission.setScore(score);
			System.out.printf("%d: %d/%d, id: %s, score: %f\n", exercise_id, submissions.indexOf(submission), submissions.size(), submission.getId(), score);	
		}
		
		//dbHelper.updateScore(submissions);
		System.out.println("Mission Completed!");

	}

	/**
	 * 将sql与所有正确解的解析树进行对比，取出其中的最高分做为最终得分
	 * 
	 * @param sql
	 * @param refTrees
	 * @return
	 */
	private float calBestScore(String sql, List<SQLTree> refTrees)
	{
		float result = 0;
		SQLTree tree = parser.parse(sql);
		
		for (SQLTree refTree : refTrees)
		{
			SQLTree tempTree = tree.deepCopy();
			float score = calScore(tempTree, refTree);
			if (score > result)
				result = score;
		}
		return result;
	}

	/**
	 * 
	 * 用来计算相似度，最终得分score计算公式如下： score = |tree 交 refTree| ) / |refTree的长度 | －　｜tree - refTree｜/ |tree|
	 * |tree - refTree| 由于在计算时，已经删除了所有的中间节点，因此不需要再进行-1操作
	 * tree比refTree多出来的节点． 有个问题需要确认下，即非叶子节点还有不少是类如subquery之类的中间结点，此类结点其实是不应该算进去的．
	 * 因此，后面需要统计一下，到底有哪些中间结点．
	 * 
	 * @param tree
	 * @param refTree
	 * @return
	 */
	public float calScore(SQLTree tree, SQLTree refTree)
	{
		Set<SQLTreeNode> intersct = new HashSet(); // 相同节点
		SQLTree tempTree = tree.deepCopy();
		lookupIntersect(tempTree.getRoot().getChildrenWithoutTempNode(), refTree.getRoot().getChildrenWithoutTempNode(),
				intersct);
		float similarity = (intersct.size() * 1.0f) / refTree.getNodeCount() ;
		Set treeNodes = tree.getNodes(IGNORE_ORDERBY);
		int sizeOfTree = treeNodes.size();
		Set refNodes = refTree.getNodes(IGNORE_ORDERBY);
		treeNodes.removeAll(refNodes);
		float diff = treeNodes.size() * 1.0f / sizeOfTree;
		return similarity - diff ;		
	}

	/**
	 * 找出condNodes与refNodes相匹配的节点，并将其加入到intersec中
	 * 
	 * @param condNodes
	 * @param refNodes
	 * @param intersct
	 */
	private void lookupIntersect(List<SQLTreeNode> condNodes, List<SQLTreeNode> refNodes, Set<SQLTreeNode> intersct)
	{
		//List<SQLTreeNode> tempNodes = new ArrayList<>(condNodes);
		for (SQLTreeNode refNode : refNodes)
		{			
			for (int i = 0; i < condNodes.size(); i++)
			{
				SQLTreeNode condNode = condNodes.get(i);
				if (refNode.getSimpleData().equals(condNode.getSimpleData()))
				{
					if (refNode.isTempNode() == false)
					{
						intersct.add(refNode);						
					}
					// 遇到WithSubSequery，它的子节点会包含一堆的winthSub,这些withSub如何进行排列组合就是一个大问题了．
					// 不过里面应该全部都是WithSub, 在这次就先不管了．．后续扩展再加吧
					/*
					 * if (refNode.getSimpleData().contains("WithSubSequery") ||
					 * refNode.getSimpleData().contains("Union")) { if (refNode.hasChild() &&
					 * condNode.hasChild()) lookupIntersect(condNode.getChildrenWithoutTempNode(),
					 * refNode.getChildrenWithoutTempNode(), intersct); }
					 */
					if (refNode.hasChild() && condNode.hasChild())
						lookupIntersect(condNode.getChildrenWithoutTempNode(), refNode.getChildrenWithoutTempNode(),
								intersct);
					if(condNode.hasChild() == false)
						condNode.removeNodeFromTree(condNode.getData());
					condNodes.remove(i);
					break;
				}
			}
		}
	}


	/**
	 * 返回指定exercise_id的所有正确答案的TreeNode集合
	 * 
	 * @param exercise_id
	 * @return
	 */
	public List<List<String>> getRefNodes(int exercise_id)
	{
		List<List<String>> result = new ArrayList<>();
		List<RefAnswer> refAnswers = dbHelper.getCorrectAnswers(exercise_id);

		for (RefAnswer refAnswer : refAnswers)
		{
			String answer = refAnswer.getAnswer();
			if(IGNORE_CASE)
				answer = answer.toLowerCase();
			SQLTree tree = parser.parse(answer);
			result.add(tree.getResultNodes());
			
		}
		return result;
	}

	private List<SQLTree> getRefTrees(int exercise_id)
	{
		List<SQLTree> refTrees = new ArrayList<>();
		//List<RefAnswer> refAnswers = dbHelper.getCorrectAnswers(exercise_id);
		List<RefAnswer> refAnswers = dbHelper.getRefAnswers(exercise_id);
		for (RefAnswer refAnswer : refAnswers)
		{
			//System.out.printf("%d: %s\n", refAnswers.indexOf(refAnswer), refAnswer.getAnswer());
			String answer = refAnswer.getAnswer();
			if(IGNORE_CASE)
				answer = answer.toLowerCase();
			SQLTree tree = parser.parse(answer);
			refTrees.add(tree);
		}
		return refTrees;
	}
	



	private void displayTree(SQLTree tree)
	{
		GenTreeGraph genTreeGraph = new GenTreeGraph();
		genTreeGraph.display(tree);
	}

}