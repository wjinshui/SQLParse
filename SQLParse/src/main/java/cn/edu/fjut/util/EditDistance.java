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
	
	public static void main(String[] args)
	{
		String stu = "SELECT a.id, a.award_name, a.year_of_award, a.category ,a.result\n" + 
				"\n" + 
				"FROM (SELECT * FROM director NATURAL JOIN director_award) AS a\n" + 
				"\n" + 
				"WHERE a.id IN\n" + 
				"(SELECT p.id\n" + 
				"FROM person p\n" + 
				"WHERE \n" + 
				"p.id \n" + 
				"NOT IN\n" + 
				"(SELECT id FROM (director NATURAL JOIN (SELECT * FROM director_award d WHERE lower(d.result)=\"won\")))\n" + 
				"AND\n" + 
				"p.id\n" + 
				"IN\n" + 
				"(SELECT id FROM (director NATURAL JOIN (SELECT * FROM director_award d WHERE lower(d.result)=\"nominated\"))) \n" + 
				"\n" + 
				") \n" + 
				";";
		String ref = "SELECT\n" + 
				"    d.id,\n" + 
				"    da.award_name,\n" + 
				"    da.year_of_award,\n" + 
				"    da.category\n" + 
				"FROM\n" + 
				"    director_award da,\n" + 
				"    director d\n" + 
				"WHERE\n" + 
				"    da.title = d.title\n" + 
				"    AND da.production_year = d.production_year\n" + 
				"    AND lower(da.result)= 'nominated'\n" + 
				"    AND d.id NOT IN (\n" + 
				"        SELECT\n" + 
				"            id\n" + 
				"        FROM\n" + 
				"            director_award da,\n" + 
				"            director d\n" + 
				"        WHERE\n" + 
				"            da.title = d.title\n" + 
				"            AND da.production_year = d.production_year\n" + 
				"            AND lower(da.result)= 'won' );";
		System.out.println(EditDistance.getInstance().getSimiarity(ref, stu));
	}
}
