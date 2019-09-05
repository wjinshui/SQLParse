package ed.edu.fjut.grade;

import java.util.List;

import cn.edu.fjut.ast.MySQLParse;
import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.SQLTree;

public abstract class BaseSyntacticGrader extends Grader
{
	protected MySQLParse parser  = new MySQLParse();
	
	protected void gradeNoninterpretable(int exercise_id)
	{
		List<ExerciseSubmission> submissions = dbHelper
				.getSubmissionWithCond(" where remark = 'noninterpretable' and exercise_id =   " + exercise_id);
		for (ExerciseSubmission submission : submissions)
		{
			ExerciseGrade grade = new ExerciseGrade(submission.getId(), submission.getSubmitted_answer(), approach);
			grade.setGrade(0);
			grades.add(grade);
		}		
	}
	
	public SQLTree parseQuery(String query)
	{
		return parser.parse(query);
	}
}
