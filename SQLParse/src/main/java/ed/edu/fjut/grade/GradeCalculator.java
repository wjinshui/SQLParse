package ed.edu.fjut.grade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.edu.fjut.DBHelper;
import cn.edu.fjut.ast.GlobalSetting;
import cn.edu.fjut.ast.MySQLParse;
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
public class GradeCalculator extends SyntacticGrader
{
	DBHelper dbHelper = DBHelper.getInstance();
	public static final boolean IGNORE_ORDERBY = true; //Ignore the order by clause

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


	public void calExerciseScore(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where (remark = 'cheating' or remark = 'partially correct')  and  exercise_id =  " + exercise_id); 																														
		String sql ;
		List<SQLTree> refTrees = getRefTrees(exercise_id);
		
		for (ExerciseSubmission submission : submissions)
		{
			sql = submission.getSubmitted_answer(); 
			double score = calBestScore(sql, refTrees);			
			submission.setScore(score);
			System.out.printf("%d: %d/%d, id: %s, score: %f\n", exercise_id, submissions.indexOf(submission), submissions.size(), submission.getId(), score);	
		}
		
		dbHelper.updateScore(submissions);
		System.out.println("Mission Completed!");

	}

	/**
	 * 将sql与所有正确解的解析树进行对比，取出其中的最高分做为最终得分
	 * @param sql
	 * @param refTrees
	 * @return
	 */
	private double calBestScore(String sql, List<SQLTree> refTrees)
	{
		double result = 0;
		SQLTree tree = parser.parse(sql);		
		for (SQLTree refTree : refTrees)
		{
			SQLTree tempTree = tree.deepCopy();
			double score = calScore(tempTree, refTree);
			if (score > result)
				result = score;
		}
		return result;
	}




	private List<SQLTree> getRefTrees(int exercise_id)
	{
		List<SQLTree> refTrees = new ArrayList<>();
		List<RefAnswer> refAnswers = dbHelper.getRefAnswers(exercise_id);
		for (RefAnswer refAnswer : refAnswers)
		{
			String answer = refAnswer.getAnswer();
			SQLTree tree = parser.parse(answer);
			refTrees.add(tree);
		}
		return refTrees;
	}

}
