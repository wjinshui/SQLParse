package cn.edu.fjut.ast;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class FixBugs {

	public static void main(String[] args) throws Exception {
		String sql = "SELECT dw.id, dw.first_name, dw.last_name \n" + 
				"FROM (person p NATURAL JOIN director NATURAL JOIN writer) AS dw \n" + 
				"GROUP BY dw.id \n" + 
				"HAVING count(*) > 1;" ; 
			/*	"where r.id in (\n" + 
				"select q1.id from (\n" + 
				"select id, count(*) as c from role group by id) as q1\n" + 
				"where q1.c = \n" + 
				"(select max(q1.c) from (select id, count(*) as c from role group by id) as q1));";*/
		MySQLParse parse = new MySQLParse();
		MySqlSchemaStatVisitor visitor =  parse.getVisitor(sql);
		System.out.println(visitor.getTables());	
		

	}

}
