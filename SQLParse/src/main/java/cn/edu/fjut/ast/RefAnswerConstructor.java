package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.bean.SQLTree;
import ed.edu.fjut.grade.GradeCalculator;
import ed.edu.fjut.grade.SyntacticGrader;

public class RefAnswerConstructor
{
	DBHelper dbHelper = DBHelper.getInstance();

	
	SyntacticGrader calculator;
	public RefAnswerConstructor()
	{
		calculator = new SyntacticGrader();
	}
	public static void main(String[] args)
	{
		RefAnswerConstructor constructor = new RefAnswerConstructor();		
		int i = 1;
		for(; i<= GlobalSetting.MAX_EXERCISE_ID; i++)
		{
			List<SQLTree> trees = constructor.getRefTrees(i);
			System.out.println(trees.size());			
		}
	}
	
	private boolean contains(List<SQLTree> refTrees, SQLTree tree)
	{
		for (SQLTree sqlTree : refTrees)
		{
			if(calculator.calScore(sqlTree, tree) == 1)
			{
				return true;			
			}
		}
		return false;
	}

	
	private List<SQLTree> getRefTrees(int exercise_id)
	{
		List<SQLTree> refTrees = new ArrayList<>();
		List<RefAnswer> correctAnws = dbHelper.getCorrectAnswers(exercise_id);
		System.out.println(correctAnws.size());
		List<RefAnswer> refAnswers = new ArrayList<>();
		for (RefAnswer correctAnw : correctAnws)
		{
			String answer = correctAnw.getAnswer();
			SQLTree tree = calculator.parseQuery(answer);
			if(contains(refTrees, tree) == false)
			{
				RefAnswer refAnswer = new RefAnswer(correctAnw.getExercise_id(), correctAnw.getId(), correctAnw.getAnswer());
				refAnswers.add(refAnswer);
				refTrees.add(tree);
			}			
		}
		dbHelper.insertRefAnswer(refAnswers);
		return refTrees;
	}

}
