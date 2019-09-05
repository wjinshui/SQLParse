package cn.edu.fjut.util;

import info.debatty.java.stringsimilarity.Levenshtein;

public class EditDistance
{
	Levenshtein caltor ;
	public static EditDistance instance = new EditDistance();
	
	public static EditDistance getInstance()
	{
		return instance;
	}
	
	public EditDistance()
	{
		caltor = new Levenshtein();
	}	

	public double getSimiarity(String ref, String stu)
	{
		double sim = 0;
		double dis =  caltor.distance(ref, stu);
		if(dis < ref.length() )		
			sim = 1 - dis / ref.length();
		return sim;
	}
}
