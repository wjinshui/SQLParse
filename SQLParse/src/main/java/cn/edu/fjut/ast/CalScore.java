package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;
import cn.edu.fjut.util.Utils;
import tree.GenTreeGraph;

/**
 * 这个类完成打分操作
 * 
 * @author admin-u1064462
 *
 */
public class CalScore
{
	DBHelper dbHelper = DBHelper.getInstance();
	MySQLParse parse;

	public CalScore()
	{
		parse = new MySQLParse();
	}

	static int count = 0;

	public static void main(String[] args)
	{
		CalScore calScore = new CalScore();
		int i = 10;
		for (; i <= 24; i++)
			calScore.calExerciseScore(i);
		System.out.println(count);
		/*
		 * String sql =
		 * "SELECT count(*) FROM person p WHERE EXISTS (SELECT * FROM writer w WHERE  p.year_born = 1935);"
		 * ; SQLTree tree1 = calScore.parse.parse(sql); sql =
		 * "SELECT count(*) FROM person p WHERE EXISTS (SELECT * FROM writer w WHERE w.id = p.id AND p.year_born = 1935);"
		 * ; SQLTree tree2 = calScore.parse.parse(sql); calScore.calScore(tree1, tree2);
		 */

	}

	// with year_born as (select * from person natural join writer where year_born =
	// 1935), id as (select distinct id from year_born) select count(*) from id

	public void calExerciseScore(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where is_correct =0 and score is null and  exercise_id =  " + exercise_id); // not
																														// a.id的a无法引到．
		String sql = "SELECT COUNT(DISTINCT person.id) FROM writer LEFT JOIN person on person.id = writer.id WHERE year_born = 1935;";
		List<SQLTree> refTrees = getRefTrees(exercise_id);
		for (ExerciseSubmission submission : submissions)
		{			
			sql = submission.getSubmitted_answer();
			float score = calBestScore(sql, refTrees);
			count++;

		}

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
		SQLTree tree = parse.parse(sql);
		for (SQLTree refTree : refTrees)
		{
			float score = calScore(tree, refTree);
			if (score > result)
				result = score;
		}
		return result;
	}

	/**
	 * 用来计算相似度，最终得分score计算公式如下： score = intersect的结点数 / (refTree的长度 -1) -
	 * tree较refTree多出的结点数 / (tree的结点数 -1)
	 * 有个问题需要确认下，即非叶子节点还有不少是类如subquery之类的中间结点，此类结点其实是不应该算进去的． 因此，后面需要统计一下，到底有哪些中间结点．
	 * 
	 * @param tree
	 * @param refTree
	 * @return
	 */
	private float calScore(SQLTree tree, SQLTree refTree)
	{
		List<SQLTreeNode> intersct = new ArrayList<>(); // 相同节点
		lookupIntersect(tree.getRoot().getChildrenWithoutTempNode(), refTree.getRoot().getChildrenWithoutTempNode(),
				intersct);
		return 0;
	}

	/**
	 * 找出condNodes与refNodes相匹配的节点，并将其加入到intersec中
	 * 
	 * @param condNodes
	 * @param refNodes
	 * @param intersct
	 */
	private void lookupIntersect(List<SQLTreeNode> condNodes, List<SQLTreeNode> refNodes, List<SQLTreeNode> intersct)
	{
		for (SQLTreeNode refNode : refNodes)
		{
			boolean repeating = false;
			for (SQLTreeNode condNode : condNodes)
			{
				if (refNode.getSimpleData().equals(condNode.getSimpleData()))
				{
					intersct.add(refNode);
					//遇到WithSubSequery，它的子节点会包含一堆的winthSub,这些withSub如何进行排列组合就是一个大问题了．
					//不过里面应该全部都是WithSub
					if (refNode.getSimpleData().contains("WithSubSequery"))
					{
						List<SQLTreeNode> withsub1 = refNode.getChildren();
						List<SQLTreeNode> withsub2 = condNode.getChildren();
						List<SQLTreeNode> nodes = calPermutations(withsub1, withsub2);
					} else
					{
						if (refNode.hasChild() && condNode.hasChild())
							lookupIntersect(condNode.getChildrenWithoutTempNode(), refNode.getChildrenWithoutTempNode(),
									intersct);
						if (repeating && refNode.hasChild())
						{
							System.out.println("ERROR");
							System.exit(1);
						}
						repeating = true;
					}
				}
			}
		}
	}

	/**由于withSub1和withsub2各自都有好几个节点，因此需要考虑排列结合的搭配
	 * 所有节点都是WithEntry,而且所有的子节点没有包含With
	 * @param withsub1
	 * @param withsub2
	 * @return
	 */
	private List<SQLTreeNode> calPermutations(List<SQLTreeNode> withsub1, List<SQLTreeNode> withsub2)
	{
		for(int i=0; i< withsub1.size(); i++)
		{
			
		}
		return null;
	}

	/**
	 * 删除重复的答案
	 * 
	 * @param refNodes
	 */
	private void removeDuplicate(List<List<String>> refNodes)
	{
		// TODO Auto-generated method stub

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
		List<RefAnswer> refAnswers = dbHelper.getAllRefAnswer(exercise_id);

		for (RefAnswer refAnswer : refAnswers)
		{
			System.out.println(refAnswer.getId());
			SQLTree tree = parse.parse(refAnswer.getAnswer());
			result.add(tree.getResultNodes());
			System.out.println("end");
		}
		return result;
	}

	private List<SQLTree> getRefTrees(int exercise_id)
	{
		List<SQLTree> refTrees = new ArrayList<>();
		List<RefAnswer> refAnswers = dbHelper.getAllRefAnswer(exercise_id);
		for (RefAnswer refAnswer : refAnswers)
		{
			SQLTree tree = parse.parse(refAnswer.getAnswer());
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
