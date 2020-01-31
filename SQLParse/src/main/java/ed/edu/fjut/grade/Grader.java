package ed.edu.fjut.grade;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.ast.GlobalSetting;
import cn.edu.fjut.bean.ExerciseGrade;
import cn.edu.fjut.bean.GradeApproach;

public abstract class Grader
{

	protected DBHelper dbHelper = DBHelper.getInstance();
	protected boolean isUpdateDB = true;
	protected GradeApproach approach;
	
	List<ExerciseGrade> grades = new ArrayList<>();
	
	protected abstract void gradeStatement(int exercise_id);
	
	public void updateDB()
	{
		if(isUpdateDB)
		{
			dbHelper.clearGrader(approach);
			dbHelper.insertGrader(grades);
			System.out.println("Update Completed!");
		}
	}
	
	public void updateResult()
	{
		dbHelper.updateResult(grades);
	}
	
	public static void main(String[] args)
	{
		Grader grader = new TwoStageSyntatic();
		for(int i = 1; i<= GlobalSetting.MAX_EXERCISE_ID; i++)
		{
			System.out.println("grading exericse_id: " + i);
			grader.gradeStatement(i);
		}
		//grader.updateDB();
		
		grader.updateResult();  //# 只用在2-steps model中，后面的数据都将评分结果保存在grade表中了
		System.out.println("Completed!");
	}

}
