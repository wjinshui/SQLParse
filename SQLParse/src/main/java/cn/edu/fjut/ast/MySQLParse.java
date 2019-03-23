package cn.edu.fjut.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.CaseUtils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;
import cn.edu.fjut.util.Log;
import tree.GenTreeGraph;

public class MySQLParse extends SQLASTVisitorAdapter {

	SchemaRepository repository;
	final String dbType = JdbcConstants.MYSQL;

	public static void main(String[] args) {
		/***
		 * 第一条成功双向解析的语句： select p.first_name, p.last_name, r.title, r.production_year,
		 * r.description from role r, movie m, person p where p.id = r.id and r.title =
		 * m.title and r.production_year = m.production_year and r.id = ( select r2.id
		 * from role r2, movie m2 where r2.title = m2.title and r2.production_year =
		 * m2.production_year group by r2.id having count(r2.id) = (select
		 * max(uni1.work_total) from ( select r1.id, count(*) as work_total from role
		 * r1, movie m1 where r1.title = m1.title and r1.production_year =
		 * m1.production_year group by r1.id) as uni1));
		 */
		MySQLParse sqlParse = new MySQLParse();
		DBHelper dbHelper = DBHelper.getInstance();
		List<ExerciseSubmission> submissions = dbHelper.getSubmissionWithCond(" where is_correct =1  "); // not
																											// a.id的a无法引到．

		String sql;
		//sql = "SELECT count(production_year) from movie WHERE production_year = 1993 or production_year = 1992 or production_year = 1991 Group by production_year;";
		sql =  " select production_year,count (*)\n" + 
				" from movie\n" + 
				" where production_year<1994 and 1990 < production_year\n" + 
				" group by production_year;";
		boolean singlesql = true;
		//singlesql = false;
		int begin = 0;
		if (singlesql) {
			SQLTree tree = singleSQL(sqlParse, sql);
			System.out.println(tree.getNodeCount());
		} else
			travelAll(sqlParse, submissions, begin);
	}

	private static void travelAll(MySQLParse sqlParse, List<ExerciseSubmission> submissions, int begin) {
		String sql;
		String[] cannotParse = { "5390", "8138", "8256", "8311" };
		// 由于druid问题，一些无法顺利解析的SQL语句
		List<String> cannotParseList = Arrays.asList(cannotParse);
		Set<String> allNodes = new HashSet<>();
		for (int i = begin; i < submissions.size(); i++) {
			ExerciseSubmission exerciseSubmission = submissions.get(i);
			System.out.printf("%d / %d : %s\n", i, submissions.size(), exerciseSubmission.getId());
			if (cannotParseList.contains(exerciseSubmission.getId()))
				continue;
			sql = exerciseSubmission.getSubmitted_answer();
			if (sql.toLowerCase().contains("delete"))
				continue;
			SQLTree tree = sqlParse.parse(sql);
			allNodes.addAll(tree.getNodes(false));

		}
		Set<String> newSet = new HashSet<>();
		for (String string : allNodes)
		{
			String name = string.split(":")[0];
			
			newSet.add(name);
		}
		System.out.println("*************");
		for (String string : newSet)
		{
			System.out.println(string);
		}
		System.out.println("*************");
	}

	private static SQLTree singleSQL(MySQLParse sqlParse, String sql) {

		System.out.println(sql);
		GenTreeGraph genTreeGraph = new GenTreeGraph();
		SQLTree tree = sqlParse.parse(sql);
		genTreeGraph.display(tree);
		return tree;
	}

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

	protected final void parseChildren(SQLTreeNode parent, List<? extends SQLObject> curNode) {
		for (SQLObject cur : curNode) {
			parseChild(parent, cur);
		}
	}

	private String getQualitifyName(SQLPropertyExpr expr, SQLTreeNode parent) {
		SQLObject owner = ((SQLPropertyExpr) expr).getResolvedOwnerObject();
		String result = "";
		if (owner instanceof SQLExprTableSource) {
			String tablename = SQLUtils.toMySqlString(((SQLPropertyExpr) expr).getResolvedOwnerObject()).split(" ")[0];
			result = tablename + "." + expr.getName();
		} else if (owner instanceof SQLSubqueryTableSource || owner instanceof SQLUnionQueryTableSource) {
			result = "." + expr.getName();
		} else if (owner instanceof SQLJoinTableSource) {
			if (expr.getResolvedColumn() != null && expr.getResolvedColumn().getParent() != null
					&& expr.getResolvedColumn().getParent() instanceof SQLCreateTableStatement) {
				SQLCreateTableStatement stmt = (SQLCreateTableStatement) expr.getResolvedColumn().getParent();
				result = stmt.getName() + "." + expr.getName();
			} else
				result = expr.toString();
		}

		else {
			System.out.println(owner);
		}

		return result;
	}

	private void parseChild(SQLTreeNode parentNode, SQLObject curObj) {

		if (curObj instanceof SQLSelectItem) {
			SQLSelectItem item = (SQLSelectItem) curObj;
			SQLExpr expr = item.getExpr();
			parseChild(parentNode, expr);
		} else if (curObj instanceof SQLSubqueryTableSource) {
			SQLSubqueryTableSource subquery = (SQLSubqueryTableSource) curObj;
			String alias = subquery.getAlias();
			SQLTreeNode node = tree.getAlias(alias);
			if (node == null) {
				node = new SQLTreeNode("SubSelect");
				parentNode.addChild(node, true);
				parseChild(node, subquery.getSelect());
				tree.putAlias(subquery.getAlias(), node);
			} else {
				parentNode.addChild(node);
			}

		} else if (curObj instanceof SQLPropertyExpr) {
			SQLPropertyExpr _exp = (SQLPropertyExpr) curObj;
			String qualitifyName = getQualitifyName(_exp, parentNode);
			SQLTreeNode node = null;
			/// 找不到表名，不知道with语句会不会也出现类似问题
			if (qualitifyName.startsWith(".")) {
				node = tree.getAlias(qualitifyName.substring(1));
				// 当子查询没有别名时，node就会找不到alias对应的字段名
				if (node == null)
					node = new SQLTreeNode(qualitifyName.substring(1));
				parentNode.addChild(node, false);
			} else {
				node = new SQLTreeNode(_exp, qualitifyName);
				parentNode.addChild(node);
			}

		}

		else if (curObj instanceof SQLJoinTableSource) {
			SQLJoinTableSource source = (SQLJoinTableSource) curObj;
			SQLTreeNode node = new SQLTreeNode(source.getJoinType().toString());
			parentNode.addChild(node);
			if (source.getAlias() != null)
				tree.putAlias(source.getAlias(), node);
			parseChild(node, source.getLeft());
			parseChild(node, source.getRight());
			if(source.getCondition() != null)
			{
				SQLTreeNode condition = new SQLTreeNode("On");
				node.addChild(condition);
				parseChild(condition, source.getCondition());
			}
		}
		// 先试一下同级
		else if (curObj instanceof SQLUnionQueryTableSource) {
			SQLUnionQueryTableSource source = (SQLUnionQueryTableSource) curObj;
			SQLTreeNode unionNode = new SQLTreeNode("UnionQuery");
			parentNode.addChild(unionNode);
			if (source.getAlias() != null)
				tree.putAlias(source.getAlias(), unionNode);
			SQLUnionQuery unionQuery = source.getUnion();

			SQLTreeNode unionLeft = new SQLTreeNode("Left");
			unionNode.addChild(unionLeft);
			parseChild(unionLeft, unionQuery.getLeft());
			SQLTreeNode unionOper = new SQLTreeNode(unionQuery.getOperator().toString());
			unionNode.addChild(unionOper);
			SQLTreeNode unionRight = new SQLTreeNode("Right");
			unionNode.addChild(unionRight);
			parseChild(unionRight, unionQuery.getRight());
		} else if (curObj instanceof SQLUnionQuery) {
			SQLUnionQuery query = (SQLUnionQuery) curObj;
			SQLTreeNode unionNode = new SQLTreeNode("UnionQuery");
			parentNode.addChild(unionNode);

			SQLTreeNode unionLeft = new SQLTreeNode("Left");
			unionNode.addChild(unionLeft);
			parseChild(unionLeft, query.getLeft());
			SQLTreeNode unionOper = new SQLTreeNode(query.getOperator().toString());
			unionNode.addChild(unionOper);
			SQLTreeNode unionRight = new SQLTreeNode("Right");
			unionNode.addChild(unionRight);
			parseChild(unionRight, query.getRight());
		} else if (curObj instanceof SQLExprTableSource) {
			SQLExprTableSource source = (SQLExprTableSource) curObj;
			String name = source.getName().getSimpleName();
			SQLTreeNode node = tree.getAlias(name);
			if (node == null)
				node = new SQLTreeNode(source.getExpr(), source.getName().getSimpleName());
			parentNode.addChild(node);
		} else if (curObj instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr expr = (SQLBinaryOpExpr) curObj;
			SQLTreeNode node = new SQLTreeNode(expr, "BinaryOp");
			parentNode.addChild(node);			
			//当opNode是　<, >, <=和　>=时，需要对leaf 和　right进行对齐，后面比较需要．．
			adjustSequence(expr);
			SQLTreeNode opNode = new SQLTreeNode(expr, expr.getOperator().getName());
			parseChild(node, expr.getLeft());
			node.addChild(opNode);
			parseChild(node, expr.getRight());
		}

		else if (curObj instanceof SQLSelectGroupByClause) {
			SQLSelectGroupByClause expr = (SQLSelectGroupByClause) curObj;
			parseChildren(parentNode, expr.getItems());
			if (expr.getHaving() != null) {
				SQLTreeNode node = new SQLTreeNode("Having");
				parentNode.addChild(node);
				parseChild(node, expr.getHaving());
			}
		} else if (curObj instanceof SQLSelectQueryBlock) {
			SQLSelectQueryBlock query = (SQLSelectQueryBlock) curObj;
			if (query.getFrom() != null) {
				SQLTreeNode fromNode = new SQLTreeNode("From");
				parentNode.addChild(fromNode);
				parseChild(fromNode, query.getFrom());
			}
			if (query.getSelectList().size() > 0) {
				SQLTreeNode selectNode = new SQLTreeNode("Select");
				parentNode.addChild(selectNode);
				if (query.getDistionOption() == 2) {
					SQLTreeNode distinctNode = new SQLTreeNode("Distinct");
					selectNode.addChild(distinctNode);
				}
				parseChildren(selectNode, query.getSelectList());
			}

			if (query.getWhere() != null) {
				SQLTreeNode whereNode = new SQLTreeNode("Where");
				parentNode.addChild(whereNode);
				parseChild(whereNode, query.getWhere());
			}
			if (query.getGroupBy() != null) {
				SQLTreeNode groupNode = new SQLTreeNode("GroupBy");
				parentNode.addChild(groupNode);
				parseChild(groupNode, query.getGroupBy());
			}
			if (query.getLimit() != null) {
				SQLTreeNode limitNode = new SQLTreeNode("Limit");
				parentNode.addChild(limitNode);
				parseChild(limitNode, query.getLimit());
			}
			if (query.getOrderBy() != null) {
				SQLTreeNode orderNode = new SQLTreeNode("OrderBy");
				parentNode.addChild(orderNode);
				parseChild(orderNode, query.getOrderBy());

			}
		} else if (curObj instanceof SQLAllColumnExpr) {
			SQLTreeNode node = new SQLTreeNode("*");
			parentNode.addChild(node);
		} else if (curObj instanceof SQLIdentifierExpr) {
			SQLIdentifierExpr expr = (SQLIdentifierExpr) curObj;
			SQLTreeNode node = tree.getAlias(expr.getName());
			if (node == null)
				node = new SQLTreeNode(expr.getName());
			if (node.getData().contains(".") == false && expr.getResolvedTableSource() != null && node.isTempNode() ==false) {
				if (expr.getResolvedTableSource().getAlias() != null)
					node.setData(expr.getResolvedTableSource().getAlias() + "." + node.getData());
				else
					node.setData(expr.getResolvedTableSource().toString() + "." + node.getData());
			}
			parentNode.addChild(node);
		} else if (curObj instanceof SQLInListExpr) {
			SQLInListExpr expr = (SQLInListExpr) curObj;
			SQLTreeNode inList = new SQLTreeNode("InList");
			parentNode.addChild(inList);
			parseChild(inList, expr.getExpr());
			SQLTreeNode node;
			if (expr.isNot())
				node = new SQLTreeNode("Not In");
			else
				node = new SQLTreeNode("In");
			inList.addChild(node);
			SQLTreeNode list = new SQLTreeNode("List");
			inList.addChild(list);
			parseChildren(list, expr.getTargetList());
		} else if (curObj instanceof SQLListExpr) {
			SQLListExpr expr = (SQLListExpr) curObj;
			SQLTreeNode listNode = new SQLTreeNode("List");
			parentNode.addChild(listNode);
			parseChildren(listNode, expr.getItems());

		} else if (curObj instanceof SQLQueryExpr) {
			SQLQueryExpr expr = (SQLQueryExpr) curObj;			
			SQLTreeNode node = new SQLTreeNode(expr, "SubSelect");
			parentNode.addChild(node);
			parseChild(node, expr.getSubQuery());
		}
		// 将Exists关键字视为一个叶子节点，以进行计数
		else if (curObj instanceof SQLExistsExpr) {
			SQLExistsExpr expr = (SQLExistsExpr) curObj;
			SQLTreeNode existNode = null;
			if (expr.not)
				existNode = new SQLTreeNode("Not Exists");
			else
				existNode = new SQLTreeNode("Exists");
			parentNode.addChild(existNode);
			if (expr.getSubQuery() != null) {
				SQLTreeNode node = new SQLTreeNode(expr, "SubSelect");
				parentNode.addChild(node);
				parseChild(node, expr.getSubQuery());
			}
		} else if (curObj instanceof SQLWithSubqueryClause) {
			SQLWithSubqueryClause clause = (SQLWithSubqueryClause) curObj;
			SQLTreeNode withNode = new SQLTreeNode("WithSubSequery");
			parentNode.addChild(withNode);

			parseChildren(withNode, clause.getEntries());
		} else if (curObj instanceof Entry) {
			Entry entry = (Entry) curObj;
			SQLTreeNode entryNode = new SQLTreeNode("WithEntry");
			parentNode.addChild(entryNode);
			if (entry.getColumns().size() > 0)
				System.out.println("Info Missing");
			if (entry.getAlias() != null)
				tree.putAlias(entry.getAlias(), entryNode);
			parseChild(entryNode, entry.getSubQuery());
		} else if (curObj instanceof SQLInSubQueryExpr) {
			SQLInSubQueryExpr expr = (SQLInSubQueryExpr) curObj;
			SQLTreeNode expNode = new SQLTreeNode("InSubQuery");
			parentNode.addChild(expNode);
			if (expr.getExpr() != null)
				parseChild(expNode, expr.getExpr());
			SQLTreeNode inNode;
			if (expr.isNot())
				inNode = new SQLTreeNode("NOT In");
			else
				inNode = new SQLTreeNode("In");
			expNode.addChild(inNode);
			if (expr.getSubQuery() != null) {
				SQLTreeNode node = new SQLTreeNode(expr, "SubSelect");
				expNode.addChild(node);
				parseChild(node, expr.getSubQuery());
			}
		} else if (curObj instanceof SQLBetweenExpr) {
			SQLBetweenExpr expr = (SQLBetweenExpr) curObj;
			SQLTreeNode betweenNode = new SQLTreeNode("BetweenOper");
			parentNode.addChild(betweenNode);
			parseChild(betweenNode, expr.getTestExpr());
			SQLTreeNode between;
			if (expr.isNot())
				between = new SQLTreeNode("Not Between");
			else
				between = new SQLTreeNode("Between");
			betweenNode.addChild(between);
			SQLTreeNode range = new SQLTreeNode("Range");
			betweenNode.addChild(range);
			parseChild(range, expr.getBeginExpr());
			parseChild(range, expr.getEndExpr());
		} else if (curObj instanceof SQLLimit) {
			SQLLimit limit = (SQLLimit) curObj;
			parseChild(parentNode, limit.getRowCount());
		} else if (curObj instanceof SQLOrderBy) {
			SQLOrderBy orderby = (SQLOrderBy) curObj;
			parseChildren(parentNode, orderby.getItems());
		} else if (curObj instanceof SQLSelectOrderByItem) {
			SQLSelectOrderByItem item = (SQLSelectOrderByItem) curObj;
			SQLTreeNode itemNode = new SQLTreeNode("OrderbyItem");
			parentNode.addChild(itemNode);
			parseChild(itemNode, item.getExpr());
			if (item.getType() == SQLOrderingSpecification.DESC) {
				SQLTreeNode type = new SQLTreeNode(SQLOrderingSpecification.DESC.toString());
				itemNode.addChild(type);
			}
		}

		else if (curObj instanceof SQLSelect) {
			SQLSelect select = (SQLSelect) curObj;
			if (select.getWithSubQuery() != null)
				parseChild(parentNode, select.getWithSubQuery());
			if (select.getQuery() != null)
				parseChild(parentNode, select.getQuery());

		} else if (curObj instanceof SQLIntegerExpr) {
			SQLIntegerExpr expr = (SQLIntegerExpr) curObj;
			SQLTreeNode node = new SQLTreeNode(expr.toString());
			parentNode.addChild(node);
		} else if (curObj instanceof SQLNullExpr) {
			SQLNullExpr expr = (SQLNullExpr) curObj;
			SQLTreeNode node = new SQLTreeNode(expr.toString());
			parentNode.addChild(node);

		} else if (curObj instanceof SQLCharExpr) {
			SQLCharExpr expr = (SQLCharExpr) curObj;
			SQLTreeNode node = new SQLTreeNode(expr.toString());
			parentNode.addChild(node);
		} else if (curObj instanceof SQLCastExpr) {
			SQLCastExpr expr = (SQLCastExpr) curObj;
			SQLTreeNode node = new SQLTreeNode("Cast");
			parentNode.addChild(node);
			parseChild(node, expr.getExpr());
			SQLTreeNode datatype = new SQLTreeNode(expr.getDataType().toString());
			node.addChild(datatype);
		} else if (curObj instanceof SQLNotExpr) {
			SQLNotExpr expr = (SQLNotExpr) curObj;
			SQLTreeNode node = new SQLTreeNode("NotExpr");
			parentNode.addChild(node);
			SQLTreeNode notnode = new SQLTreeNode("Not");
			node.addChild(notnode);
			parseChild(node, expr.getExpr());
		} else if (curObj instanceof SQLMethodInvokeExpr) {
			SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) curObj;
			SQLTreeNode node = new SQLTreeNode("MethodInvoke");
			if (expr.getParent() instanceof SQLSelectItem) {
				SQLSelectItem item = (SQLSelectItem) expr.getParent();
				if (item.getAlias() != null)
					tree.putAlias(item.getAlias(), node);
			}
			parentNode.addChild(node);
			SQLTreeNode methodNode = new SQLTreeNode(expr.getMethodName());
			node.addChild(methodNode);
			if (expr.getParameters().size() > 0)
				parseChildren(node, expr.getParameters());
		}

		else if (curObj instanceof SQLAggregateExpr) {
			SQLAggregateExpr expr = (SQLAggregateExpr) curObj;
			SQLTreeNode node = new SQLTreeNode(expr, "Function");
			if (expr.getParent() instanceof SQLSelectItem) {
				SQLSelectItem item = (SQLSelectItem) expr.getParent();
				if (item.getAlias() != null)
					tree.putAlias(item.getAlias(), node);
			}
			parentNode.addChild(node);
			SQLTreeNode methodNode = new SQLTreeNode(expr.getMethodName());
			node.addChild(methodNode);
			if(expr.getOption() != null)
			{
				SQLTreeNode distinct = new SQLTreeNode(expr.getOption().toString());
				node.addChild(distinct);
			}
			if (expr.getArguments().size() > 0)
				parseChildren(node, expr.getArguments());
			else if(expr.getMethodName().equals("COUNT") && expr.getArguments().size() ==0) //当count()没有参数时，需要手工加上一个*
				parseChild(node, new SQLAllColumnExpr());
		} else {
			System.out.println(curObj);
			log.log("###################################\n" + curObj.toString() + "#####################\n");

		}

	}

	
	/**当opNode是　<, >, <=和　>=时，需要对leaf 和　right进行对齐，后面比较需要．．
	 * 让右边都是SQLIntegerExpr或SQLCharExpr
	 * @param expr
	 */
	private void adjustSequence(SQLBinaryOpExpr expr)
	{		
		if(expr.getRight() instanceof SQLIntegerExpr || expr.getRight() instanceof SQLCharExpr)
			return;
		String opername = expr.getOperator().getName();
		if(opername.equals("<") || opername.equals("<=") || opername.equals(">") || opername.equals(">="))
		{
			switch (expr.getOperator().getName())
			{
			case "<":
				expr.setOperator(SQLBinaryOperator.GreaterThan);
				break;
			case ">":
				expr.setOperator(SQLBinaryOperator.LessThan);
				break;
			case "<=":
				expr.setOperator(SQLBinaryOperator.GreaterThanOrEqual);
				break;
			case ">=":
				expr.setOperator(SQLBinaryOperator.LessThanOrEqual);
				break;
			default:
				break;
			}
			SQLExpr expr2 = expr.getRight().clone();
			expr.setRight(expr.getLeft());
			expr.setLeft(expr2);
		}

		
	}

	SQLTree tree;
	Log log = new Log();

	public SQLTree parse(String sql) {
		try {
			List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
			SQLStatement stmt = stmtList.get(0);
			resolve(stmt);
			SQLSelect select = (SQLSelect) (stmt.getChildren().get(0));
			tree = new SQLTree();
			SQLTreeNode root = tree.getRoot();
			parseChild(root, select);
		} catch (Exception e) {
			log.log(sql + "\n ******************************\n");
		}

		return tree;

	}

}
