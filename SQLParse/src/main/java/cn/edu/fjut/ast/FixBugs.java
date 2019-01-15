package cn.edu.fjut.ast;

import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;

public class FixBugs {

	public static void main(String[] args) throws Exception {
		DBHelper helper = DBHelper.getInstance();
		List<ExerciseSubmission> solutions = helper.getAllSubmission();
		int success =0 , total = 0;		
		
		for (ExerciseSubmission solu : solutions) {
			String solution = solu.getSubmitted_answer().trim();
			if(solution.length() < 20)
				continue;
			if(solution.endsWith(";"))
				solution = solution.substring(0, solution.length() - 1);	
			total ++;
	
			
			success ++;				

			System.out.printf("%d / %d\n", success, total);
		}
		

	}

}
