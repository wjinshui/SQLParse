package cn.edu.fjut.bean;


public class ExerciseGrade
{
	int submission_id;
	String submitted_answer;
	double grade;
	GradeApproach approach;
	String most_similary;
	
	
	public ExerciseGrade(int submission_id, String submitted_answer, GradeApproach approach)
	{
		super();
		this.submission_id = submission_id;
		this.submitted_answer = submitted_answer;
		this.approach = approach;
	}
	public int getSubmission_id()
	{
		return submission_id;
	}
	public void setSubmission_id(int submission_id)
	{
		this.submission_id = submission_id;
	}
	public String getSubmitted_answer()
	{
		return submitted_answer;
	}
	public void setSubmitted_answer(String submitted_answer)
	{
		this.submitted_answer = submitted_answer;
	}
	public double getGrade()
	{
		return grade;
	}
	public void setGrade(double sim)
	{
		this.grade = sim;
	}
	public GradeApproach getApproach()
	{
		return approach;
	}
	public void setApproach(GradeApproach approach)
	{
		this.approach = approach;
	}
	public String getMost_similary()
	{
		return most_similary;
	}
	public void setMost_similary(String most_similary)
	{
		this.most_similary = most_similary;
	}
	
	
}
