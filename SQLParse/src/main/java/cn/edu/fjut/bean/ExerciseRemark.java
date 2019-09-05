package cn.edu.fjut.bean;

public class ExerciseRemark
{
	int id;
	String remark;
	String answer;
	
	
	
	public ExerciseRemark(int id, String remark, String answer)
	{
		super();
		this.id = id;
		this.remark = remark;
		this.answer = answer;
	}
	public String getAnswer()
	{
		return answer;
	}
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public ExerciseRemark(int id, String remark)
	{
		super();
		this.id = id;
		this.remark = remark;
	}
	public ExerciseRemark(int id)
	{
		this.id = id;
	}
	
}
