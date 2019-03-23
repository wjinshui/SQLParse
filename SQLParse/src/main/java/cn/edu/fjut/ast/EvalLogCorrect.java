package cn.edu.fjut.ast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.RefAnswer;
import cn.edu.fjut.util.Log;

/**
 * 这个类用来确保log文件中的SQL还是可能得到正确解
 * @author admin-u1064462
 *
 */
public class EvalLogCorrect {
	public static void main(String[] args) {
		Log log = new Log(false);
		int exercise_id = 1１;
		List<String> sqls = log.getContent();
		DBHelper dbHelper = DBHelper.getInstance();
		EvalResult evalResult = new EvalResult();
		boolean result = true;
		//用来再次确认规范化之后的sql还是可以得到准确解
		for (String sql : sqls) {
			if(sql.split(";").length ==1)
				continue;
			String content = getSQL(sql);
			result = evalResult.evalAnswer(exercise_id, dbHelper.getAnswer(content));	
			if(result == false)
			{
				System.out.println(sql);
				break;
			}
		}
		if(result == true)
		{
			List<RefAnswer> refAnswers = new ArrayList<>();
			for (String content : sqls) {
				int id = Integer.parseInt( content.split(";")[0]);
				String answer = getSQL(content);
				RefAnswer refAnswer = new RefAnswer(exercise_id, id, answer);
				refAnswers.add(refAnswer);
			}
			dbHelper.insertRefAnswer(refAnswers);
			
		}
		System.out.println("Mission Completed");
	}

	private static String getSQL(String sql) {
		String result = "";
		for(int i=1; i< sql.split(";").length ; i++)
			result = result + sql.split(";")[i];
		return result;
	}
}
