package cn.edu.fjut.ast;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;

/**
 * 删除用户提交答案中的注释，以及除了最后一条可执行语句外的所有SQL。
 * 只变更exercise_result中的记录，原始记录的内容没有变化
 * @author admin-u1064462
 *
 */
public class CleanSubmission
{

	
	public static void main(String[] args)
	{

		CleanSubmission cleanSubmission = new CleanSubmission();
		//cleanSubmission.cleanComment();
		cleanSubmission.retainLastStatement();
		

	}

	void retainLastStatement()
	{
		List<ExerciseSubmission> submissions = dbHelper.getSubmissionWithCond(" where submitted_answer like '%;%;%' ");
		int count = 0;
		for (ExerciseSubmission submission : submissions)
		{
			System.out.printf("%d/%d, %s", submissions.indexOf(submission) + 1, submissions.size(), submission.getId());
			String sql = submission.getSubmitted_answer();			
			int lenth = sql.length();
			sql = retainLastStatement(sql);
			System.out.printf(": %d\n",sql.length() - lenth);			
			try{
				if(sql.toLowerCase().contains("delete") == false)
					dbHelper.executeQuery(sql);
			} catch (SQLException e)
			{			
				System.out.println(sql);				
			}			
			submission.setSubmitted_answer(sql);
		}		
		
		dbHelper.updateSubmitAnswer(submissions);
		System.out.println("Completed");
	}
	
	String retainLastStatement(String sql)
	{
		String[] subs = sql.split(";");
		for(int i= subs.length -1; i>=0; i--)
		{
			if(subs[i].trim().length() == 0)
				continue;
			return subs[i]+ ";";
		}
		return sql;
	}
	
	void cleanComment()
	{
		List<ExerciseSubmission> submissions = dbHelper.getSubmissionWithCond(" where ( submitted_answer like '%/*%' or submitted_answer like '%--%') ");
		submissions = dbHelper.getAllSubmission();
		for (ExerciseSubmission submission : submissions)
		{
			System.out.printf("%d/%d, %s\n", submissions.indexOf(submission) + 1, submissions.size(), submission.getId());
			String sql = submission.getSubmitted_answer();
			Set<Set<String>> answerA = dbHelper.getAnswer(sql);
			sql = removeComment(sql);
			Set<Set<String>> answerB = dbHelper.getAnswer(sql);
			if(answerA != null && answerA.equals(answerB) == false)
			{
				System.out.println("Error");
				System.exit(1);
			}
			submission.setSubmitted_answer(sql);
		}
		
		dbHelper.updateSubmitAnswer(submissions);
		System.out.println("Completed");
	}
	
	DBHelper dbHelper = DBHelper.getInstance();
	
	/**
	 * 删除注释，在SQL中注释可由/*或--带出，因此只需查找这两类字符的出现即可
	 * @param sql
	 * @return
	 */
	private String removeComment(String sql)
	{
		while(sql.contains("/*"))
		{
			int start = sql.indexOf("/*");
			int end = sql.indexOf("*/");
			if(end >0)
				sql = sql.substring(0, start) + sql.substring(end + 2);
			else
				sql = sql.substring(0, start);
			sql = sql.trim();
		}
		while(sql.contains("--"))
		{
			int start = sql.indexOf("--");
			int end = sql.indexOf('\n', start);			
			sql = sql.substring(0, start) + sql.substring(end+1);
			sql = sql.trim();
			
		}
		
		return sql;
	}
}
