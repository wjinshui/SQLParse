package cn.edu.fjut.util;


import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

public class EditDistance
{
	NormalizedLevenshtein caltor ;
	public static EditDistance instance = new EditDistance();
	
	public static EditDistance getInstance()
	{
		return instance;
	}
	
	public EditDistance()
	{
		caltor = new NormalizedLevenshtein();
	}	

	public double getSimiarity(String ref, String stu)
	{
		double sim = 0;
		double dis =  caltor.distance(ref, stu);
		sim = 1 - dis;
		return sim;
	}
	
	public static void main(String[] args)
	{
		String stu = "SELE a.id, a.award_name, a.year_of_award, a.category ,a.result\n" ;
		String ref = "SELECT a.id, a.award_name, a.year_of_award, a.category ,a.result\n";
		System.out.println(EditDistance.getInstance().getSimiarity(ref, stu));
	}
}
