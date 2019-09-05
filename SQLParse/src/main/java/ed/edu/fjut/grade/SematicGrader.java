package ed.edu.fjut.grade;

import java.util.List;

import cn.edu.fjut.ast.GlobalSetting;
import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;
import cn.edu.fjut.util.EditDistance;
import cn.edu.fjut.util.QueryUtils;

public class SematicGrader extends Grader
{

	public SematicGrader()
	{
		approach = GradeApproach.sematic;
	}

	

	
	EditDistance simCaltor = EditDistance.getInstance();
	int count = 0;
	
	
	
	@Override
	protected void gradeStatement(int exercise_id)
	{		
		List<ExerciseSubmission> submissions = dbHelper.getSubmission(exercise_id);
		String ref = dbHelper.getRefAnswer(exercise_id);
			
		for (ExerciseSubmission submission : submissions)
		{
			double sim = calculateSim(submission.getSubmitted_answer(), ref);
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), GradeApproach.sematic);
			grade.setGrade(sim);
			grade.setMost_similary(ref);
			grades.add(grade);			
		}		
		
	}

	QueryUtils utils = new QueryUtils();
	
	public double calculateSim(String submitted_answer, String ref)
	{
		ref = utils.standdardize(ref).toLowerCase();
		submitted_answer = utils.standdardize(submitted_answer).toLowerCase();
		double value =  simCaltor.getSimiarity( ref, submitted_answer);		
		return value;
	}

}
