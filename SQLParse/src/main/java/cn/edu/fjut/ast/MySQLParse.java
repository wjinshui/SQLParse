package cn.edu.fjut.ast;

import java.lang.module.ResolvedModule;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;

import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;
import cn.edu.fjut.util.DrawTree;
import cn.edu.fjut.util.TestDrawTree;

public class MySQLParse extends SQLASTVisitorAdapter {

	SchemaRepository repository;
	// final String dbType = JdbcConstants.SQLITE;
	final String dbType = JdbcConstants.MYSQL;

	public MySQLParse() {

		repository = new SchemaRepository(dbType);
		repository.console("CREATE TABLE Actor_Award(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  description CHAR(255), \n" + "  award_name CHAR(255), \n" + "  year_of_award FLOAT, \n"
				+ "  category CHAR(255), \n" + "  result CHAR(255));");
		repository.console("CREATE TABLE Appearance(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  description CHAR(255), \n" + "  scene_no FLOAT);");
		repository.console("CREATE TABLE Award(\n" + "  award_name CHAR(255), \n" + "  institution CHAR(255), \n"
				+ "  country CHAR(255));");
		repository.console("CREATE TABLE Crew(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n"
				+ "  production_year FLOAT, \n" + "  contribution CHAR(255));");
		repository.console("CREATE TABLE Crew_Award(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n"
				+ "  production_year FLOAT, \n" + "  award_name CHAR(255), \n" + "  year_of_award FLOAT, \n"
				+ "  category CHAR(255), \n" + "  result CHAR(255));");
		repository.console(
				"CREATE TABLE director(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n" + "  production_year FLOAT);");
		repository.console("CREATE TABLE Director_Award(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  award_name CHAR(255), \n" + "  year_of_award FLOAT, \n" + "  category CHAR(255), \n"
				+ "  result CHAR(255));");
		repository.console("CREATE TABLE movie(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  country CHAR(255), \n" + "  run_time FLOAT, \n" + "  major_genre CHAR(255));");
		repository.console("CREATE TABLE movie_award(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  award_name CHAR(255), \n" + "  year_of_award FLOAT, \n" + "  category CHAR(255), \n"
				+ "  result CHAR(255));");
		repository.console("CREATE TABLE person(\n" + "  id FLOAT, \n" + "  first_name CHAR(255), \n"
				+ "  last_name CHAR(255), \n" + "  year_born FLOAT);");
		repository.console("CREATE TABLE restriction(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  description CHAR(255), \n" + "  country CHAR(255));");
		repository.console(
				"CREATE TABLE Restriction_Category(\n" + "  description CHAR(255), \n" + "  country CHAR(255));");
		repository.console("CREATE TABLE Role(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n"
				+ "  production_year FLOAT, \n" + "  description CHAR(255), \n" + "  credits CHAR(255));");
		repository.console("CREATE TABLE Scene(\n" + "  title CHAR(255), \n" + "  production_year FLOAT, \n"
				+ "  scene_no FLOAT, \n" + "  description CHAR(255));");
		repository.console(
				"CREATE TABLE submitanswer(\n" + "  exercisesubmission_ptr_id INT, \n" + "  submitted_answer TEXT, \n"
						+ "  answer TEXT, \n" + "  exercise_id INT, \n" + "  is_correct boolean);");
		repository.console("CREATE TABLE writer(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n"
				+ "  production_year FLOAT, \n" + "  credits CHAR(255));");
		repository.console("CREATE TABLE Writer_Award(\n" + "  id FLOAT, \n" + "  title CHAR(255), \n"
				+ "  production_year FLOAT, \n" + "  award_name CHAR(255), \n" + "  year_of_award FLOAT, \n"
				+ "  category CHAR(255), \n" + "  result CHAR(255));");

	}

	public MySqlSchemaStatVisitor getVisitor(String sql) {

		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
		SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);
		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		repository.resolve(stmt);
		stmt.accept(visitor);
		return visitor;
	}
	
	public void resolve(SQLStatement stmt)
	{
		repository.resolve(stmt);
	}
	
	private void addChild(SQLTreeNode parent, List<SQLSelectItem> selectList) {
		for (SQLSelectItem sqlSelectItem : selectList) {
			SQLExpr expr = sqlSelectItem.getExpr();
			if(expr instanceof SQLPropertyExpr)
			{
				SQLPropertyExpr m_expr = (SQLPropertyExpr) expr;
				SQLTreeNode node = new SQLTreeNode(expr, m_expr.getName());
				parent.addChild(node);				
			}
		}
		
	}

	@Override
	public boolean visit(SQLSelectQueryBlock x) {
		
		SQLTree tree = new SQLTree();
		SQLTreeNode root = tree.getRoot();
		
		List<SQLSelectItem> selectItemList = x.getSelectList();
		if(selectItemList.size() > 0)
		{
			SQLTreeNode  node = new SQLTreeNode("Select");
			root.addChild(node);
			addChild(node, x.getSelectList() );	
		}
		
		
		selectItemList.forEach(selectItem -> {
			System.out.println("attr:" + selectItem.getAttributes());
			System.out.println("expr:" + SQLUtils.toMySqlString(selectItem.getExpr()));
		});

		System.out.println("table:" + SQLUtils.toMySqlString(x.getFrom()));
		SQLExpr expr = x.getWhere();
		//Map<String, Object> temp = expr.getAttributes();
		//System.out.println(temp);
		//System.out.println("where:" + SQLUtils.toMySqlString(x.getWhere()));
		// System.out.println("order by:" +
		// SQLUtils.toMySqlString(x.getOrderBy().getItems().get(0)));
		// System.out.println("limit:" + SQLUtils.toMySqlString(x.getLimit()));
		TestDrawTree testDrawTree = new TestDrawTree(root);
		testDrawTree.setSize(500,500);
		testDrawTree.initComponents();
		testDrawTree.setVisible(true);
		return true;
	}



	public static void main(String[] args) {

		String sql;
		sql = "with t1 as (\n" + "	select count(*) as tt\n" + "	from movie\n" + "),\n" + "t2 as (\n"
				+ "	select count(distinct m.title) as tt\n" + "	from movie as m\n" + "	-- get director information\n"
				+ "	inner join director as d on d.title = m.title and d.production_year = m.production_year\n"
				+ "	-- get crew infromaiton\n"
				+ "	inner join crew as c on c.title = m.title and c.production_year = m.production_year\n"
				+ "	-- get writer info\n"
				+ "	inner join writer as w on m.title = w.title and m.production_year = w.production_year\n"
				+ "	-- get actor info\n"
				+ "	inner join role as r on r.title = m.title and r.production_year = w.production_year\n"
				+ "	-- get award infromation\n"
				+ "	left join movie_award as ma on ma.title = m.title and ma.production_year = m.production_year\n"
				+ "	left join director_award as da on da.title = d.title and da.production_year = d.production_year\n"
				+ "	left join writer_award as wa on wa.id = w.id and wa.title = w.title and wa.production_year = w.production_year\n"
				+ "	left join crew_award as ca on ca.id = c.id and ca.title = c.title and ca.production_year = c.production_year\n"
				+ "	left join actor_award as aa on aa.title = r.title and aa.production_year = r.production_year and aa.description = r.description\n"
				+ "	where lower(ma.result) = 'won'\n" + "		or lower(da.result) = 'won'\n"
				+ "		or lower(wa.result) = 'won'\n" + "		or lower(ca.result) = 'won'\n"
				+ "		or lower(aa.result) = 'won'\n" + ")\n" + "select t1.tt - t2.tt\n" + "from t1, t2;";
		//sql = "select count(*) from person p  where exists (select * from writer w  where w.id = p.id and p.year_born = 1935)";
		/*
		 * select count(*) 20% from person 10% where exists 10% ( select * 10% from
		 * writer 10% where writer.id = person.id 20% and 10% person.year_born = 1935
		 * 20% )
		 */
		sql = "select p.first_name, p.last_name, r.title, r.production_year, r.description from role r, movie m, person p where p.id = r.id and r.title = m.title and r.production_year = m.production_year and r.id = (select r2.id from role r2, movie m2 where r2.title = m2.title and r2.production_year = m2.production_year group by r2.id having count(r2.id) = (select max(uni1.work_total) from (select r1.id, count(*) as work_total from role r1, movie m1 where r1.title = m1.title and r1.production_year = m1.production_year group by r1.id) as uni1));";
		System.out.println(sql);
		MySQLParse sqlParse = new MySQLParse();
		MySqlSchemaStatVisitor visitor = sqlParse.getVisitor(sql);
		
		SQLStatementParser parser = new MySqlStatementParser(sql);        
        SQLStatement sqlStatement = parser.parseStatement();
        sqlParse.resolve(sqlStatement);
		
        sqlStatement.accept(sqlParse);
        
		
		/*System.out.println(visitor.getTables());
		System.out.println(visitor.getColumns());
		System.out.println(visitor.getGroupByColumns());*/

	}

}
