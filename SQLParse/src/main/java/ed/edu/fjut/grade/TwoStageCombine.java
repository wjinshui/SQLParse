package ed.edu.fjut.grade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.util.EditDistance;
import cn.edu.fjut.util.QueryUtils;

public class TwoStageCombine extends TwoStageSyntatic
{
	QueryUtils utils = new QueryUtils();
	EditDistance simCaltor = EditDistance.getInstance();
	
	public TwoStageCombine()
	{
		super();
		approach = GradeApproach.two_stage_combine;
	}
	
	@Override
	protected void gradeNoninterpretable(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where remark = 'noninterpretable' and exercise_id =   " + exercise_id);
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
	
	protected Map<String, Object> getMostSematicSim(String submitted_answer, List<RefAnswer> correctAnswers)
	{
		Map<String, Object> keyValues = new HashMap<String, Object>();
		double result  = -1;
		submitted_answer = utils.standdardize(submitted_answer);
		for (RefAnswer refAnswer : correctAnswers)
		{			
			double value = simCaltor.getSimiarity(utils.standdardize( refAnswer.getAnswer()), submitted_answer);		
			if(value > result)
			{
				result = value;
				keyValues.put("score", value);
				keyValues.put("query", refAnswer.getAnswer());
			}
		}
		return keyValues;
	}
}
