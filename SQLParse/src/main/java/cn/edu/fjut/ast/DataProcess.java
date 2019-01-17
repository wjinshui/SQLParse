package cn.edu.fjut.ast;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.util.Log;

public class DataProcess {

	public static void main(String[] args) {
		DBHelper helper = DBHelper.getInstance();
		List<ExerciseSubmission> solutions = helper.getAllSubmission();
		Log log = new Log();
		MySQLParse parse = new MySQLParse();
		String[] errorID = new String[] {"8764"};
		List<String> arrays = Arrays.asList(errorID);
		for (ExerciseSubmission exerciseSubmission : solutions) {
			if(exerciseSubmission.is_correct())
			{
				if(arrays.contains(exerciseSubmission.getId()))
					continue;
				if(exerciseSubmission.getSubmitted_answer().toLowerCase().contains("delete"))
					continue;
				try {
					System.out.println("######################");
					String answer = exerciseSubmission.getSubmitted_answer().trim().toLowerCase();						
					System.out.println( answer);
					System.out.println("*** " + exerciseSubmission.is_correct() + " **** " + exerciseSubmission.getId() + " ********");
					MySqlSchemaStatVisitor visitor =  parse.getVisitor(answer);
					System.out.println(visitor.getTables());	
				} catch (Exception e) {
					log.log(exerciseSubmission.getSubmitted_answer());
					log.log("************************************************");
				}
				
				
				
				
								
			}
		}		
		
		helper.close();

	}

}
