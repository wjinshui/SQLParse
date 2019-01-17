package cn.edu.fjut.ast;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;



public class MySQLParse {

	SchemaRepository repository;
	//final String dbType = JdbcConstants.SQLITE;
	final String dbType = JdbcConstants.MYSQL;
	
	
	public MySQLParse() {
			
		repository = new SchemaRepository(dbType);
		repository.console("CREATE TABLE Actor_Award(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  description CHAR(255), \n" + 
				"  award_name CHAR(255), \n" + 
				"  year_of_award FLOAT, \n" + 
				"  category CHAR(255), \n" + 
				"  result CHAR(255));");
		repository.console("CREATE TABLE Appearance(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  description CHAR(255), \n" + 
				"  scene_no FLOAT);");
		repository.console("CREATE TABLE Award(\n" + 
				"  award_name CHAR(255), \n" + 
				"  institution CHAR(255), \n" + 
				"  country CHAR(255));");
		repository.console("CREATE TABLE Crew(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  contribution CHAR(255));");
		repository.console("CREATE TABLE Crew_Award(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  award_name CHAR(255), \n" + 
				"  year_of_award FLOAT, \n" + 
				"  category CHAR(255), \n" + 
				"  result CHAR(255));");
		repository.console("CREATE TABLE director(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT);");
		repository.console("CREATE TABLE Director_Award(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  award_name CHAR(255), \n" + 
				"  year_of_award FLOAT, \n" + 
				"  category CHAR(255), \n" + 
				"  result CHAR(255));");
		repository.console("CREATE TABLE movie(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  country CHAR(255), \n" + 
				"  run_time FLOAT, \n" + 
				"  major_genre CHAR(255));");
		repository.console("CREATE TABLE movie_award(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  award_name CHAR(255), \n" + 
				"  year_of_award FLOAT, \n" + 
				"  category CHAR(255), \n" + 
				"  result CHAR(255));");
		repository.console("CREATE TABLE person(\n" + 
				"  id FLOAT, \n" + 
				"  first_name CHAR(255), \n" + 
				"  last_name CHAR(255), \n" + 
				"  year_born FLOAT);");
		repository.console("CREATE TABLE restriction(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  description CHAR(255), \n" + 
				"  country CHAR(255));");
		repository.console("CREATE TABLE Restriction_Category(\n" + 
				"  description CHAR(255), \n" + 
				"  country CHAR(255));");
		repository.console("CREATE TABLE Role(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  description CHAR(255), \n" + 
				"  credits CHAR(255));");
		repository.console("CREATE TABLE Scene(\n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  scene_no FLOAT, \n" + 
				"  description CHAR(255));");
		repository.console("CREATE TABLE submitanswer(\n" + 
				"  exercisesubmission_ptr_id INT, \n" + 
				"  submitted_answer TEXT, \n" + 
				"  answer TEXT, \n" + 
				"  exercise_id INT, \n" + 
				"  is_correct boolean);");
		repository.console("CREATE TABLE writer(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  credits CHAR(255));");
		repository.console("CREATE TABLE Writer_Award(\n" + 
				"  id FLOAT, \n" + 
				"  title CHAR(255), \n" + 
				"  production_year FLOAT, \n" + 
				"  award_name CHAR(255), \n" + 
				"  year_of_award FLOAT, \n" + 
				"  category CHAR(255), \n" + 
				"  result CHAR(255));");
		
	}
	
	public MySqlSchemaStatVisitor getVisitor(String sql)
	{
		
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
		SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);		
		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		repository.resolve(stmt);		
		stmt.accept(visitor);		
		return visitor;
	}
	
	public static void main(String[] args) {
		

		String sql ;
		sql = "with t1 as (\n" + 
				"	select count(*) as tt\n" + 
				"	from movie\n" + 
				"),\n" + 
				"t2 as (\n" + 
				"	select count(distinct m.title) as tt\n" + 
				"	from movie as m\n" + 
				"	-- get director information\n" + 
				"	inner join director as d on d.title = m.title and d.production_year = m.production_year\n" + 
				"	-- get crew infromaiton\n" + 
				"	inner join crew as c on c.title = m.title and c.production_year = m.production_year\n" + 
				"	-- get writer info\n" + 
				"	inner join writer as w on m.title = w.title and m.production_year = w.production_year\n" + 
				"	-- get actor info\n" + 
				"	inner join role as r on r.title = m.title and r.production_year = w.production_year\n" + 
				"	-- get award infromation\n" + 
				"	left join movie_award as ma on ma.title = m.title and ma.production_year = m.production_year\n" + 
				"	left join director_award as da on da.title = d.title and da.production_year = d.production_year\n" + 
				"	left join writer_award as wa on wa.id = w.id and wa.title = w.title and wa.production_year = w.production_year\n" + 
				"	left join crew_award as ca on ca.id = c.id and ca.title = c.title and ca.production_year = c.production_year\n" + 
				"	left join actor_award as aa on aa.title = r.title and aa.production_year = r.production_year and aa.description = r.description\n" + 
				"	where lower(ma.result) = 'won'\n" + 
				"		or lower(da.result) = 'won'\n" + 
				"		or lower(wa.result) = 'won'\n" + 
				"		or lower(ca.result) = 'won'\n" + 
				"		or lower(aa.result) = 'won'\n" + 
				")\n" + 
				"select t1.tt - t2.tt\n" + 
				"from t1, t2;";
		sql = "select kw.title,kw.production_year \n" + 
				"from (person p inner join writer w inner join movie m on w.title = m.title and p.first_name ='kevin' and p.last_name = 'williamson' and w.id = p.id) as kw\n" + 
				"group by kw.title";
		MySQLParse sqlParse = new MySQLParse();		
		MySqlSchemaStatVisitor visitor = sqlParse.getVisitor(sql);
		System.out.println(visitor.getTables());
		System.out.println(visitor.getColumns());
		
		

	}

}
