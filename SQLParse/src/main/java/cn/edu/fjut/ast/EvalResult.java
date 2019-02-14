package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;

/**
 * 通过对比提交的SQL与参考答案SQL两者执行结果，判断结果是否正确
 * @author admin-u1064462
 * @date 2019.1.17
 */
public class EvalResult {

	DBHelper dbHelper;
	HashMap<Integer, Set<Set<String>>> refAnswer ;
	public EvalResult() {
		dbHelper = DBHelper.getInstance();
		//refAnswer = dbHelper.getRefAnswer();
	}
	
	public boolean evalAnswer(int id, Set<Set<String>> answer)
	{		
		Set<Set<String>> correct = refAnswer.get(id);
		return correct.equals(answer);
	}
	
	
	public static void main(String[] args) {		
		EvalResult evalResult = new EvalResult();		
		evalResult.dbHelper.initialScore();
	}
	
	/**对指定题进行改题，　依据是对比输入sql与答案sql是否相似进行
	 * 需注意的是，输入sql可能会包含delete语句，从而影响数据库中的数据，因此在代码中删除跳过包含delete语句的sql
	 * @param exerciseID 
	 */
	public void evalExercise(int exerciseID)
	{		
		List<ExerciseSubmission> submissions =  dbHelper.getSubmission(exerciseID);		
		boolean correct = false;
		List<String> updateToTrue = new ArrayList<String>();
		List<String> updateToFalse = new ArrayList<String>();
		for (ExerciseSubmission exerciseSubmission : submissions) {
			String answer = exerciseSubmission.getSubmitted_answer();	
			// 3374那道题执行是错的，但在jdbc中却一直能得到正确的解．．
			if(answer.toLowerCase().contains("delete") || exerciseSubmission.getId().equals("3374") )
			{
				System.out.println(exerciseSubmission);
				continue;
			}		
			correct = evalAnswer(exerciseID, dbHelper.getAnswer(answer));			
			if(correct != exerciseSubmission.is_correct())
			{
				if(correct)
					updateToTrue.add(exerciseSubmission.getId());
				else
					updateToFalse.add(exerciseSubmission.getId());				
			}			
		}
		System.out.println("To True: " + updateToTrue.size() + " To False: " + updateToFalse.size());
		for (String id : updateToTrue) {
			System.out.println(id);
		}
		
		System.out.println("To False");
		for (String id : updateToFalse) {
			System.out.println(id);
		}
		
		dbHelper.updateJudgement(updateToTrue, true);
		dbHelper.updateJudgement(updateToFalse, false);
		
	}

}
