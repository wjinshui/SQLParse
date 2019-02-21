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
		
		sql = "select first_name, last_name, title, production_year, description from person natural join role where id in ( select id from (select id, max(kk.mm) from (select count(id) as mm,id from person natural join role group by id) as kk)) ;";
		sql = "select p.first_name,p.last_name,r.title,r.production_year,r.description from( select id,max(t.times) from (select p.id,count(*) as times from person p,role r where p.id=r.id group by p.id order by times desc) as t)as tt,person p,role r where p.id=tt.id and p.id = r.id ;";
		sql = "select t.first_name,t.last_name,r.title,r.production_year,r.description from (select p.first_name as first_name,p.last_name as last_name,p.id as id from person p,role r where p.id=r.id group by p.id order by count(distinct r.title) desc limit 1) t,role r where t.id=r.id;";
		sql = "SELECT r2.first_name, r2.last_name,description FROM (SELECT r1.id,r1.first_name, r1.last_name FROM (SELECT *, COUNT(*)number_of_roles FROM ROLE natural join person GROUP BY id)r1 WHERE r1.number_of_roles= (SELECT max(r.number_of_roles)FROM (SELECT id, COUNT(*)number_of_roles FROM ROLE GROUP BY id)r))r2 NATURAL JOIN role; ";
		sql = "with cou as ( select id, count(*) as numberofrole from role group by id) select p.first_name, p.last_name, r.title, r.production_year, r.description from person p, role r, cou b where r.id = p.id and p.id = b.id and b.numberofrole in ( select max(a.numberofrole) from cou a);";
		
		sql = "select p.first_name,p.last_name,r.production_year,r.description,r.title from person p, role r, (select id,count(*) as noofrole from role group by id) as idno where p.id = r.id and idno.id = p.id and idno.noofrole in (select max(idno1.noofrole) from (select r0.id,count(*) as noofrole from role r0 group by r0.id)as idno1);";
		sql = "select first_name, last_name, title, production_year, description from role natural join person where id = ( select id from role natural join person group by id having count(*) = ( select max(cnt) from ( select count(*) as cnt from role group by id ) ) ) ;\n" ;
		sql = "Select person.first_name ,person.last_name ,role.title ,role.production_year ,role.description  From role ,movie ,person  Where person.id  =  role.id   AND  role.title  =  movie.title    AND  role.production_year  =  movie.production_year    AND  role.id  =  (Select role.id  From role ,movie  Where role.title  =  movie.title   AND  role.production_year  =  movie.production_year     )   ";
		sql = "select p.first_name, p.last_name, r.title, r.production_year, r.description from role r, movie m, person p where p.id = r.id and r.title = m.title and r.production_year = m.production_year and r.id = (select r2.id from role r2, movie m2 where r2.title = m2.title and r2.production_year = m2.production_year group by r2.id having count(r2.id) = (select max(uni1.work_total) from (select r1.id, count(*) as work_total from role r1, movie m1 where r1.title = m1.title and r1.production_year = m1.production_year group by r1.id) as uni1));";
		sql = "Select person.first_name ,person.last_name ,role.title ,role.production_year ,role.description  From role ,movie ,person  Where person.id  =  role.id   AND  role.title  =  movie.title    AND  role.production_year  =  movie.production_year    AND  role.id  =  (Select role.id  From role ,movie  Where role.title  =  movie.title   AND  role.production_year  =  movie.production_year     group by role.id Having COUNT( role.id ) =  (Select MAX( (Select role.id ,COUNT( * ) From role ,movie  Where role.title  =  movie.title   AND  role.production_year  =  movie.production_year     group by role.id  ),) From (Select role.id , From role ,movie  Where role.title  =  movie.title   AND  role.production_year  =  movie.production_year     group by role.id  ) )   )   ";
		
		System.out.println(SQLHelper.getFormatSQL(sql));
	}
	
	public static String getFormatSQL(String sql)
	{
		return SQLUtils.format(sql, JdbcConstants.MYSQL);
	}	
}
