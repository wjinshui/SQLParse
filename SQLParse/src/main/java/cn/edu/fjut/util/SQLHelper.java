package cn.edu.fjut.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

public class SQLHelper {	
	
	public static void main(String[] args) {
		String sql = "select count(distinct person.id) from person inner join writer on person.id=writer.id and person.year_born='1935'; select count(distinct writer.id) from writer,person where writer.id=person.id and person.year_born='1935';";	
		System.out.println(SQLHelper.getFormatSQL(sql));
	}
	
	public static String getFormatSQL(String sql)
	{
		return SQLUtils.format(sql, JdbcConstants.MYSQL);
	}	
}
