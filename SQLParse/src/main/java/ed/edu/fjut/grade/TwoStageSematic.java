package ed.edu.fjut.grade;

import java.util.List;
import java.util.Map;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.bean.RefAnswer;

public class TwoStageSematic extends TwoStageCombine
{
	public TwoStageSematic()
	{
		super();
		approach = GradeApproach.two_stage_sematic;
	}

	
	@Override
	protected void gradeStatement(int exercise_id)
	{
		gradeCorrect(exercise_id);
		gradeOthers(exercise_id);	
	
	}
	
	private void gradeOthers(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where remark != 'correct' and exercise_id =   " + exercise_id);
		List<RefAnswer> correctAnswers = dbHelper.getCorrectAnswers(exercise_id);	
		for (ExerciseSubmission submission : submissions)
		{
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), approach);
			Map<String, Object> keyValues = getMostSematicSim(submission.getSubmitted_answer(), correctAnswers);
			grade.setGrade(Double.parseDouble( keyValues.get("score").toString()));
			grade.setMost_similary(keyValues.get("query").toString() );
			grades.add(grade);
		}	
	}


}
