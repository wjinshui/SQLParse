package cn.edu.fjut.util;

public class QueryUtils
{
	public String standdardize(String query)
	{
		query = Utils.removeBlank(query);
		query = regularizedSpace(query);
		if(query.contains("  "))
			System.out.println(query);
		return query;
	}
	
	private String regularizedSpace(String query)
	{
		query = query.replaceAll("\\s*([^\\w])\\s*", "$1").trim();	
		query = query.replaceAll(";", "");
		return query;
	}
	private String replaceAlais(String query)
	{
		return query;
	}
}
