package cn.edu.fjut.ast;

import java.util.List;


import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.util.Log;

public class DataProcess {

	public static void main(String[] args) {
		DBHelper helper = DBHelper.getInstance();
		List<ExerciseSubmission> solutions = helper.getAllSubmission();
		Log log = new Log();

		for (ExerciseSubmission exerciseSubmission : solutions) {
			if(exerciseSubmission.is_correct())
			{
				String answer = exerciseSubmission.getSubmitted_answer().trim().toLowerCase();
				if(answer.endsWith(";"))
					answer = answer.substring(0, answer.length() - 1);		
				System.out.println(answer);	
								
			}
		}		
		
		helper.close();

	}

}
