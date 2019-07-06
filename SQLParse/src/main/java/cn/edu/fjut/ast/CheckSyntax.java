package cn.edu.fjut.ast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseRemark;
import cn.edu.fjut.bean.ExerciseSubmission;

/**
 * 这个类用来做些语法检查，但好像没啥用了
 * @author admin-u1064462
 *
 */
public class CheckSyntax
{
	public static void main(String[] args)
	{
		DBHelper dbHelper = DBHelper.getInstance();
		//List<ExerciseSubmission> submissions = dbHelper.getAllSubmission();
		List<ExerciseSubmission> submissions = dbHelper.getSubmissionWithCond(" where  submitted_answer like '%;%;%' and remark != 'noninterpretable' order by length(submitted_answer) desc ");
		List<ExerciseRemark> remarks = new ArrayList<>();
		int i =0;
		for (ExerciseSubmission exerciseSubmission : submissions)
		{
			System.out.printf("%d / %d: %s\n", ++i, submissions.size(), exerciseSubmission.getId());
			String id = exerciseSubmission.getId();
			ExerciseRemark remark = new ExerciseRemark(id);
			remark.setAnswer(exerciseSubmission.getSubmitted_answer());
			if(exerciseSubmission.getSubmitted_answer().contains("delete") == false)
			{
				try
				{
					ResultSet rs = dbHelper.executeQuery(exerciseSubmission.getSubmitted_answer());
					while(rs.next())
					{
						System.out.println(rs.getObject(1));
					}
					remark.setRemark("pass");
				} catch (Exception e)
				{
					remark.setRemark("fail");
				}				
			}
			else
				remark.setRemark("contain delete");
			System.out.println(remark.getRemark());
			remarks.add(remark);
		}
		//dbHelper.insertRemarks(remarks);
	}
}
