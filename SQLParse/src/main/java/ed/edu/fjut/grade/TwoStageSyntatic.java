package ed.edu.fjut.grade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.bean.SQLTree;

public class TwoStageSyntatic extends SyntacticGrader
{	
	public TwoStageSyntatic()
	{
		super();
		approach = GradeApproach.two_stage_syntactic;
	}
	
	@Override
	protected void gradeStatement(int exercise_id)
	{		
		gradePartiallyCorrect(exercise_id);	
		gradeNoninterpretable(exercise_id);
		gradeCorrect(exercise_id);
	}
	
	protected void gradeCorrect(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where remark = 'correct' and exercise_id =   " + exercise_id);
		for (ExerciseSubmission submission : submissions)
		{
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), approach);
			grade.setGrade(1);
			grades.add(grade);
		}
	}


	protected void gradePartiallyCorrect(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where ( remark = 'partially correct' or remark = 'cheating' ) and exercise_id =   " + exercise_id);			 																														
		//List<RefAnswer> refAnswers = dbHelper.getCorrectAnswers(exercise_id);
		List<RefAnswer> refAnswers = dbHelper.getRefAnswers(exercise_id);
		List<SQLTree> refTrees = getRefTrees(refAnswers);	
		
		for (ExerciseSubmission submission : submissions)
		{
			String sql = submission.getSubmitted_answer(); 
			Map<String, Object> keyValues = calBestScore(sql, refTrees);
			double sim = Double.parseDouble( keyValues.get("score").toString());	
			int index = Integer.parseInt(keyValues.get("index").toString());
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), approach);
			grade.setGrade(sim );
			grade.setMost_similary( refAnswers.get(index).getAnswer() );
			grades.add(grade);				
		}
	}
	
	private List<SQLTree> getRefTrees(List<RefAnswer> refAnswers)
	{
		List<SQLTree> refTrees = new ArrayList<>();		
		for (RefAnswer refAnswer : refAnswers)
		{
			String answer = refAnswer.getAnswer();
			SQLTree tree = parser.parse(answer);
			refTrees.add(tree);
		}
		return refTrees;
	}


	
	private Map<String, Object> calBestScore(String sql, List<SQLTree> refTrees)
	{
		Map<String, Object> keyValues = new HashMap<>();
		double result = -1;
		SQLTree tree = parser.parse(sql);		
		for (SQLTree refTree : refTrees)
		{
			SQLTree tempTree = tree.deepCopy();
			double score = calScore(tempTree, refTree);
			if (score >= result)
			{
				result = score;
				keyValues.put("score", result);
				keyValues.put("index", refTrees.indexOf(refTree));
			}
			if(Double.parseDouble( keyValues.get("score").toString()) < 0)
				keyValues.put("score", 0);
		}
		
		return keyValues;
	}
	
	

}
