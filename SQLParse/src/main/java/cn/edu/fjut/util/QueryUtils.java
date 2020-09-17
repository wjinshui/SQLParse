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
		query = query.replaceAll("\\s*([^\\w\\W])\\s*", "$1").trim();	
		query = query.replaceAll(";", "");
		return query;
	}

	public static void main(String[] args) {
		String string = " select    *  a	-      sadff      	a"
				+ " from FFF ";
		QueryUtils utils = new QueryUtils();
		System.out.println(utils.standdardize(string));
	}
}
