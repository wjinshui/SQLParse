package cn.edu.fjut.ast;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.edu.fjut.DBHelper;

public class Test
{

	public static void main(String[] args)
	{
		DBHelper helper = DBHelper.getInstance();
		String sql = "select title,production_year\n" + 
				"from MOVIE\n" + 
				"WHERE country='Australia';\n" + 
				"\n" + 
				"select count(*)\n" + 
				"from (select distinct id\n" + 
				"from writer);\n" + 
				"\n" + 
				"select p.id,first_name,last_name,award_name\n" + 
				"from WRITER_AWARD wa,PERSON p\n" + 
				"WHERE wa.id=p.id and wa.year_of_award=1994 and lower(result)='won';\n" + 
				"\n" + 
				"\n" + 
				"select year_born,count(*)\n" + 
				"from PERSON\n" + 
				"WHERE year_born=1965 or year_born=1966\n" + 
				"group by year_born;\n" + 
				"\n" + 
				"select distinct max(year_born)\n" + 
				"from DIRECTOR d inner join PERSON p ON d.id=p.id;\n" + 
				"\n" + 
				"select count(*)\n" + 
				"from(select id,count(*) as num\n" + 
				"from CREW_AWARD\n" + 
				"where lower(result)='won'\n" + 
				"group by id\n" + 
				"having num>1);\n" + 
				"\n" + 
				"select max(num)\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from SCENE\n" + 
				"group by title) ;\n" + 
				"\n" + 
				"select d.id,count(*)\n" + 
				"from director d,movie m\n" + 
				"where d.title =m.title and d.production_year=m.production_year and lower(m.country)='usa'\n" + 
				"group by d.id\n" + 
				"having count(*)>1;\n" + 
				"\n" + 
				"select a.id\n" + 
				"from (select id,count(*) as num\n" + 
				"from writer_award \n" + 
				"where result='won'\n" + 
				"group by id) as a\n" + 
				"where a.num=\n" + 
				"(select max(num)\n" + 
				"from (select id,count(*) as num\n" + 
				"from writer_award \n" + 
				"where result='won'\n" + 
				"group by id) );\n" + 
				"\n" + 
				"select w.id,wa.title,wa.production_year\n" + 
				"from writer_award wa,director_award da,writer w \n" + 
				"where wa.title=da.title \n" + 
				"and wa.production_year=da.production_year \n" + 
				"and wa.id=w.id \n" + 
				"and wa.title=w.title \n" + 
				"and wa.production_year=w.production_year\n" + 
				"and lower(wa.result)='won'\n" + 
				"and lower(da.result)='won';\n" + 
				"\n" + 
				"select p.id,p.first_name,p.last_name\n" + 
				"from (select a1.id,count(*)\n" + 
				"from actor_award aa1,role a1\n" + 
				"where aa1.title=a1.title and aa1.production_year=a1.production_year\n" + 
				"and a1.id not in (\n" + 
				"select id \n" + 
				"from actor_award aa ,role a\n" + 
				"where a.title=aa.title and a.production_year=aa.production_year and lower(aa.result)='won')\n" + 
				"group by a1.id\n" + 
				"having count(*)>1)as x,person p\n" + 
				"where p.id=x.id;\n" + 
				"\n" + 
				"\n" + 
				"select id\n" + 
				"from crew c,(select title,production_year,count(*) as num\n" + 
				"from movie_award\n" + 
				"where lower(result)='won'\n" + 
				"group by title,production_year) as a\n" + 
				"where a.title=c.title and a.production_year=c.production_year\n" + 
				"and a.num=\n" + 
				"(select max(num)\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from movie_award\n" + 
				"where lower(result)='won'\n" + 
				"group by title,production_year));\n" + 
				"\n" + 
				"select p.id,p.first_name,p.last_name\n" + 
				"from\n" + 
				"(select id,count(*) as num\n" + 
				"from (select distinct r.id, d.description\n" + 
				"from role r,restriction d\n" + 
				"where r.title =d.title and r.production_year=d.production_year and lower(d.country)='usa'\n" + 
				"order by r.id)\n" + 
				"group by id) as b,person p\n" + 
				"where p.id =b.id and b.num=(select count(distinct description)\n" + 
				"from restriction\n" + 
				"where lower(country)='usa');\n" + 
				"\n" + 
				"\n" + 
				"select distinct aid,bid from\n" + 
				"(select aid,bid,count(*) as s\n" + 
				"from(select w1.id as aid,w2.id as bid\n" + 
				"from writer w1,writer w2\n" + 
				"where w1.title=w2.title and w1.production_year=w2.production_year and w1.id!=w2.id and w1.title in\n" + 
				"(select a.title\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from writer \n" + 
				"group by title,production_year\n" + 
				"having num>1)as a))\n" + 
				"group by aid,bid)as b\n" + 
				"where b.s= (select max(s)\n" + 
				"from (select aid,bid,count(*) as s\n" + 
				"from(select w1.id as aid,w2.id as bid\n" + 
				"from writer w1,writer w2\n" + 
				"where w1.title=w2.title and w1.production_year=w2.production_year and w1.id!=w2.id and w1.title in\n" + 
				"(select a.title\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from writer \n" + 
				"group by title,production_year\n" + 
				"having num>1)as a))\n" + 
				"group by aid,bid)) ;\n" + 
				"\n" + 
				"select aid from\n" + 
				"(select aid,bid from\n" + 
				"(select aid,bid,count(*) as s\n" + 
				"from(select w1.id as aid,w2.id as bid\n" + 
				"from writer w1,writer w2\n" + 
				"where w1.title=w2.title and w1.production_year=w2.production_year and w1.id!=w2.id and w1.title in\n" + 
				"(select a.title\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from writer \n" + 
				"group by title,production_year\n" + 
				"having num>1)as a))\n" + 
				"group by aid,bid)as b\n" + 
				"where b.s= (select max(s)\n" + 
				"from (select aid,bid,count(*) as s\n" + 
				"from(select w1.id as aid,w2.id as bid\n" + 
				"from writer w1,writer w2\n" + 
				"where w1.title=w2.title and w1.production_year=w2.production_year and w1.id!=w2.id and w1.title in\n" + 
				"(select a.title\n" + 
				"from(select title,production_year,count(*) as num\n" + 
				"from writer \n" + 
				"group by title,production_year\n" + 
				"having num>1)as a))\n" + 
				"group by aid,bid)));\n" + 
				"\n" + 
				"\n" + 
				"select count(*)\n" + 
				"from (select distinct p.id\n" + 
				"from person p inner join writer w on w.id=p.id\n" + 
				"where p.year_born =1935);";
		try
		{
			ResultSet rSet = helper.executeQuery(sql);
			while(rSet.next())
				System.out.println(rSet.getString(1));
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
