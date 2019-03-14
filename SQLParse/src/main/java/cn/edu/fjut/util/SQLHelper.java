package cn.edu.fjut.util;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

public class SQLHelper {	
	
	public static void main(String[] args) {
		String sql = "select count(distinct person.id) from person inner join writer on person.id=writer.id and person.year_born='1935'; select count(distinct writer.id) from writer,person where writer.id=person.id and person.year_born='1935';";
		sql = "select p.first_name, p.last_name, r.title, r.production_year, r.description from person p, role r where p.id = r.id and p.id = ( select r.id from person p inner join role r on p.id = r.id group by r.id having count(r.id) = (select max(tt) from ( select r.id, count(r.id) as tt from person p inner join role r on p.id = r.id group by r.id) t1)) ;";
		sql = "select first_name,last_name,title,production_year,description from person natural join role where id =( select id from person natural join role group by id having count(*) =( select count(*) as n1 from person natural join role group by id order by n1 desc limit 1 ) ) ;";
		sql = "SELECT COUNT(*) FROM (writer_award wa     NATURAL JOIN person) AS pr, writer wr WHERE pr.title = wr.title     AND pr.production_year = wr.production_year     AND lower(pr.first_name) = 'woody'     AND lower(pr.last_name) = 'allen'     AND pr.year_of_award <= 1995     AND pr.year_of_award >= 1991     AND lower(pr.result) = 'won'; ";
		sql = "select first_name, last_name, title, production_year, description from role natural join person where id = ( select id from role natural join person group by id having count(*) = ( select max(cnt) from ( select count(*) as cnt from role group by id ) ) ) ;";
		sql = "select p.first_name,p.last_name, r.title, r.production_year, r.description from person p ,role r where r.id=p.id and p.id = (select id from (select id,count(*) as c from role group by id) where c= (select max(c) from (select id,count(*) as c from role group by id)));";
		sql = "with cou as ( select id, count(*) as numberofrole from role group by id) select p.first_name, p.last_name, r.title, r.production_year, r.description from person p, role r, cou b where r.id = p.id and p.id = b.id and b.numberofrole in ( select max(a.numberofrole) from cou a);";	

		System.out.println(SQLHelper.getFormatSQL(sql));
	}
	
	public static String getFormatSQL(String sql)
	{
		return SQLUtils.format(sql, JdbcConstants.MYSQL);
	}	
}
