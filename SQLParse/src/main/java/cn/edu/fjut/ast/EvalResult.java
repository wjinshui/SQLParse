package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseRemark;
import cn.edu.fjut.bean.ExerciseSubmission;

/**
 * 通过对比提交的SQL与参考答案SQL两者执行结果，以完成对答案的分类 
 * @author admin-u1064462
 * @date 2019.1.17
 */
public class EvalResult {

	DBHelper dbHelper;
	HashMap<Integer, Set<Set<String>>> refAnswer ;
	public EvalResult() {
		dbHelper = DBHelper.getInstance();
		refAnswer = dbHelper.getRefExeResult();
	}
	
	public boolean evalAnswer(int id, Set<Set<String>> answer)
	{		
		Set<Set<String>> correct = refAnswer.get(id);		
		return correct.equals(answer);
	}
	

	
	public static void main(String[] args) {		
		EvalResult evalResult = new EvalResult();		
		//evalResult.dbHelper.initialScore();
		for(int i = 10; i <= 24; i++)
		{
			System.out.println("************************** " + i + "**************************");
			evalResult.evalExercise(i);
		}
	}
	
	/**对指定题进行改题，　依据是对比输入sql与答案sql是否相似进行
	 * 需注意的是，输入sql可能会包含delete语句，从而影响数据库中的数据，因此在代码中删除跳过包含delete语句的sql
	 * 因此，执行这个方法可以快速找出判分正确且结果错误，以及判分错误且结果正确的记录．
	 * @param exerciseID 
	 */
	public void evalExercise(int exerciseID)
	{		
		List<ExerciseSubmission> submissions =  dbHelper.getSubmission(exerciseID);		
		boolean correct = false;
		List<String> updateToTrue = new ArrayList<String>();
		List<String> updateToFalse = new ArrayList<String>();
		List<ExerciseRemark> remarks = new ArrayList<>();
		for (ExerciseSubmission exerciseSubmission : submissions) {
			String answer = exerciseSubmission.getSubmitted_answer();	
			// 3374那道题执行是错的，但在jdbc中却一直能得到正确的解．．
			if(answer.toLowerCase().contains("delete") || exerciseSubmission.getId().equals("3374") )
			{
				System.out.println(exerciseSubmission);
				ExerciseRemark remark = new ExerciseRemark(exerciseSubmission.getId(), "delete", exerciseSubmission.getSubmitted_answer());
				remarks.add(remark);
				continue;
			}		
			Set<Set<String>> results = dbHelper.getAnswer(answer);
			if(results == null)
				correct = false;
			else
				correct = evalAnswer(exerciseID, results );
			ExerciseRemark remark = null;
			if(results  == null)
				remark = new ExerciseRemark(exerciseSubmission.getId(), "Noninterpretable", exerciseSubmission.getSubmitted_answer());
			else
				remark = new ExerciseRemark(exerciseSubmission.getId(), String.valueOf(correct), exerciseSubmission.getSubmitted_answer());
			remarks.add(remark);
			if(correct != exerciseSubmission.is_correct())
			{
				if(correct)
					updateToTrue.add(exerciseSubmission.getId());
				else
					updateToFalse.add(exerciseSubmission.getId());				
			}			
		}
		//dbHelper.updateRemarks(remarks);
		System.out.println("To True: " + updateToTrue.size() + " To False: " + updateToFalse.size());
		for (String id : updateToTrue) {
			System.out.println(id);
		}
		
		System.out.println("To False");
		for (String id : updateToFalse) {
			System.out.println(id);
		}
		
		//dbHelper.updateJudgement(updateToTrue, true);
		//dbHelper.updateJudgement(updateToFalse, false);
		
	}

}
