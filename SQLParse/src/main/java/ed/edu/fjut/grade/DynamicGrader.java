package ed.edu.fjut.grade;

import java.util.List;

import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.GradeApproach;

public class DynamicGrader extends Grader
{

	@Override
	protected void gradeStatement(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper.getSubmission(exercise_id);
			
		for (ExerciseSubmission submission : submissions)
		{			
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), GradeApproach.dynamic);
			if(submission.getRemark().equals("cheating") || submission.getRemark().equals("correct"))
				grade.setGrade(1.0);
			else
				grade.setGrade(0);
			grades.add(grade);			
		}		
	}
	
}
