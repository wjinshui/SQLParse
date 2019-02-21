package cn.edu.fjut.ast;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;

import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;
import tree.GenTreeGraph;

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



	public void resolve(SQLStatement stmt) {
		repository.resolve(stmt);
	}

	protected final void addChild(SQLTreeNode parent,  List<? extends SQLObject> curNode) {		
		for (SQLObject cur : curNode) {
			addChild(parent, cur);			
		}
	}
	
	private String getQualitifyName(SQLPropertyExpr expr, SQLTreeNode parent)
	{	
		SQLObject owner = ((SQLPropertyExpr) expr).getResolvedOwnerObject();		
		String result = "";
		if(owner instanceof SQLExprTableSource)
		{
			 String tablename = SQLUtils.toMySqlString(((SQLPropertyExpr) expr).getResolvedOwnerObject()).split(" ")[0];
			 result = tablename + "." +  expr.getName();
		}
		else if(owner instanceof SQLSubqueryTableSource)
		{
			result = "." +  expr.getName(); 
			//addChild(parent, owner);			
		}
		else 
		{
			System.out.println(owner);
		}
		 
		return result;
	}

	private void addChild(SQLTreeNode parent, SQLObject curNode) {		
		if(curNode instanceof SQLSelectItem)
		{
			SQLSelectItem item = (SQLSelectItem) curNode;
			SQLExpr expr = item.getExpr();
			addChild(parent, expr);			
		}
		else if(curNode instanceof SQLSubqueryTableSource)
		{
			SQLSubqueryTableSource subquery = (SQLSubqueryTableSource) curNode; 
			String alias = subquery.getAlias();
			if(StringUtils.isEmpty(alias) == false)
			{
				SQLTreeNode node = tree.getAlias(alias);
				if(node == null)
				{
					node = new SQLTreeNode("SubSelect");
					parent.addChild(node, false);				
					addChild(node, subquery.getSelect().getQueryBlock());
					tree.putAlias(subquery.getAlias(), node);	
				}
				else
				{
					parent.addChild(node);
				}
			}
				
		}
		else if( curNode instanceof SQLPropertyExpr)
		{
			SQLPropertyExpr _exp = (SQLPropertyExpr) curNode;
			String qualitifyName =  getQualitifyName(_exp, parent);
			SQLTreeNode node = null;
			///　找不到表名，不知道with语句会不会也出现类似问题
			if(qualitifyName.startsWith("."))
			{
				node = tree.getAlias(qualitifyName.substring(1));
				parent.addChild(node, false);	
			}
			else
			{
				node = new SQLTreeNode(_exp, qualitifyName);
				parent.addChild(node);
			}
			
		}
		
		else if(curNode instanceof SQLJoinTableSource)
		{
			SQLJoinTableSource source = (SQLJoinTableSource) curNode; 
			SQLTreeNode node = new SQLTreeNode(source.getJoinType().toString());
			parent.addChild(node);
			addChild(node, source.getLeft());
			addChild(node, source.getRight());		
		}
		else if(curNode instanceof SQLExprTableSource)
		{
			SQLExprTableSource source = (SQLExprTableSource) curNode;
			SQLTreeNode node = new SQLTreeNode(source.getExpr(), source.getName().getSimpleName());
			parent.addChild(node);
		}
		else if(curNode instanceof SQLBinaryOpExpr)
		{
			SQLBinaryOpExpr expr = (SQLBinaryOpExpr) curNode;			
			SQLTreeNode node = new SQLTreeNode(expr, "BinaryOp");
			parent.addChild(node);
			SQLTreeNode opNode = new SQLTreeNode(expr, expr.getOperator().getName());			
			addChild(node, expr.getLeft());
			node.addChild(opNode);
			addChild(node, expr.getRight());			
		}
		else if(curNode instanceof SQLQueryExpr)
		{
			SQLQueryExpr expr = (SQLQueryExpr) curNode;			
			SQLSelectQueryBlock queryBlock = expr.getSubQuery().getQueryBlock();
			SQLTreeNode node = new SQLTreeNode(expr, "SubSelect");
			parent.addChild(node);
			addChild(node, queryBlock);			
		}
		else if(curNode instanceof SQLSelectGroupByClause)
		{
			SQLSelectGroupByClause expr = (SQLSelectGroupByClause) curNode;
			addChild(parent, expr.getItems());
			if(expr.getHaving() != null)
			{
				SQLTreeNode node = new SQLTreeNode("Having");
				parent.addChild(node);
				addChild(node, expr.getHaving());
			}		
		}
		else if(curNode instanceof SQLSelectQueryBlock)
		{
			SQLSelectQueryBlock query = (SQLSelectQueryBlock) curNode;
			if(query.getFrom() != null)
			{
				SQLTreeNode fromNode = new SQLTreeNode("From");
				parent.addChild(fromNode);
				addChild(fromNode, query.getFrom());
			}
			if(query.getSelectList().size()  > 0)
			{
				SQLTreeNode selectNode = new SQLTreeNode("Select");
				parent.addChild(selectNode);
				addChild(selectNode,  query.getSelectList());	
			}
			
			if(query.getWhere() != null)
			{
				SQLTreeNode whereNode = new SQLTreeNode("Where");
				parent.addChild(whereNode);
				addChild(whereNode, query.getWhere());
			}
			if(query.getGroupBy() != null)
			{
				SQLTreeNode groupNode = new SQLTreeNode("GroupBy");
				parent.addChild(groupNode);
				addChild(groupNode, query.getGroupBy());				
			}			
		}
		else if(curNode instanceof SQLAllColumnExpr)
		{
			SQLTreeNode node = new SQLTreeNode("*");
			parent.addChild(node);
		}
		//TODO: distinct lower(xxx) 和 lower (distinct(xxx))　是否一样? 这个可能会影响不同函数之间的调用顺序是否无视的问题 
		else if (curNode instanceof SQLAggregateExpr)
		{
			SQLAggregateExpr expr = (SQLAggregateExpr) curNode;
			//distinct　咋处理？？
			SQLTreeNode node = new SQLTreeNode(expr, "Function");
			/// 别的地方可能会引用这个Item 例如 max(nnn) as maxvalue
			if( expr.getParent() instanceof  SQLSelectItem )
			{
				SQLSelectItem item = (SQLSelectItem) expr.getParent();
				if(item.getAlias() != null)
					tree.putAlias(item.getAlias(), node);	
			}
			parent.addChild(node);			
			SQLTreeNode methodNode = new SQLTreeNode(expr.getMethodName());
			node.addChild(methodNode);
			if(expr.getArguments().size() > 0)
				addChild(node, expr.getArguments());

		}
		else {
			System.out.println(curNode);
		}
		
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
		// sql = "select count(*) from person p where exists (select * from writer w
		// where w.id = p.id and p.year_born = 1935)";
		/*
		 * select count(*) 20% from person 10% where exists 10% ( select * 10% from
		 * writer 10% where writer.id = person.id 20% and 10% person.year_born = 1935
		 * 20% )
		 */
		sql = "select p.first_name, p.last_name, r.title, r.production_year, r.description "
				+ "from role r, movie m, person p "
				+ "where p.id = r.id and r.title = m.title and r.production_year = m.production_year and r.id = ("
					+ "	select r2.id from role r2, movie m2 "
					+ "where r2.title = m2.title and r2.production_year = m2.production_year "
					+ "group by r2.id having count(r2.id) = ("
					+ "select max(uni1.work_total) from ("
						+ " select r1.id, count(*) as work_total from role r1, movie m1 "
						+ "where r1.title = m1.title and r1.production_year = m1.production_year group by r1.id) as uni1));";
		System.out.println(sql);
		MySQLParse sqlParse = new MySQLParse();
		SQLTree tree = sqlParse.parse(sql);
		GenTreeGraph genTreeGraph = new GenTreeGraph();
		genTreeGraph.display(tree);
		System.out.println(tree.convertToSQL());
		
	}

	SQLTree tree ;
	public SQLTree parse(String sql) {
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
		SQLStatement stmt = stmtList.get(0);
		resolve(stmt);
		SQLSelect select = (SQLSelect) (stmt.getChildren().get(0));
		
		SQLSelectQueryBlock query = select.getQueryBlock();		
		// Has withSubQuery
		if (select.getWithSubQuery() != null && select.getWithSubQuery().getEntries().size() > 0) {
			System.out.println("have withSubQuery");
		}
		tree = new SQLTree();
		SQLTreeNode root = tree.getRoot();
		addChild(root, query);
		
		return tree;
		

	}

}
