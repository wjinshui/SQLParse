package cn.edu.fjut.ast;

import java.io.StringWriter;
import java.util.List;

import javax.lang.model.element.QualifiedNameable;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import cn.edu.fjut.DBHelper;
import cn.edu.fjut.bean.ExerciseSubmission;
import cn.edu.fjut.bean.Validation;

public class SqlParser {

	public static void main(String[] args) {
		 pareAll();
		 //test();

	}

	private static void pareAll() {
		DBHelper helper = DBHelper.getInstance();
		List<ExerciseSubmission> submissions = helper
				.getSubmissionWithCond(" where exercise_id = 11 and is_correct =1");
		List<Validation> validations = helper.getValidation();
		Validation validation = validations.get(0);
		System.out.println(validation);
		for (ExerciseSubmission exerciseSubmission : submissions) {			
			String sql = exerciseSubmission.getSubmitted_answer();			
			MySqlStatementParser parser = new MySqlStatementParser(sql);
			SQLStatement sqlStatement = parser.parseStatement();
			SQLSchemaStatVisitor visitor = new SQLSchemaStatVisitor();
			try
			{
				sqlStatement.accept(visitor);
				boolean result = validateVisitor(visitor, validation);
				if (result == false) {
					System.out.println(exerciseSubmission.getId() + " " + sql);
					System.out.println("getTables:" + visitor.getTables());
					System.out.println("getParameters:" + visitor.getParameters());
					System.out.println("condition:" + visitor.getConditions());
				}
			} catch (Exception e)
			{
				System.out.println(sql);
			}
			
		}
	}

	private static boolean validateVisitor(SQLSchemaStatVisitor visitor, Validation validation) {
		if (validation.validateTables(visitor.getTables()) == false)
			return false;
		if (validation.validateCondition(visitor.getConditions()) == false)
			return false;

		return true;
	}

	private static void test() {
		String sql = "select count(*) from person p where exists (select * from writer w where w.id=p.id group by w.id having year_born=1935);";
		sql = "select count(*) where exists (select * from writer,person where year_born = 1935 and writer.id = person.id);";
		// 新建 MySQL Parser
		SQLStatementParser parser = new MySqlStatementParser(sql);

		// 使用Parser解析生成AST，这里SQLStatement就是AST
		SQLStatement sqlStatement = parser.parseStatement();
		SQLSchemaStatVisitor visitor = new SQLSchemaStatVisitor();
		sqlStatement.accept(visitor);

		System.out.println("getTables:" + visitor.getTables());
		System.out.println("getParameters:" + visitor.getParameters());
		System.out.println("condition:" + visitor.getConditions());
		System.out.println("getOrderByColumns:" + visitor.getOrderByColumns());
		System.out.println("getGroupByColumns:" + visitor.getGroupByColumns());
		System.out
				.println("--------------------------  print visitor -------------------------------------------------");

		DBHelper helper = DBHelper.getInstance();
		List<Validation> validations = helper.getValidation();
		Validation validation = validations.get(0);
		boolean result = validateVisitor(visitor, validation);
		System.out.println(result);
		System.out.println(
				"-----------------------------  print outputvisitor ----------------------------------------------");
		// 最终sql输出
		StringWriter out = new StringWriter();
		TableNameVisitor outputVisitor = new TableNameVisitor(out);
		sqlStatement.accept(outputVisitor);
		System.out.println(out.toString());
	}

	public static void updateValidation() {
		String sql;
		sql = "insert into validation ( exercise_id, requirTables, requireCondition) VALUES (11, 'movie', '1935');\n";
		DBHelper helper = DBHelper.getInstance();
		helper.executeUpdate(sql);

	}

}

/**
 * 数据库表名访问者
 * 
 * @author xiezhengchao
 * @since 2018/6/1 11:52
 */
class TableNameVisitor extends MySqlOutputVisitor {

	public TableNameVisitor(Appendable appender) {
		super(appender);
	}

	@Override
	public boolean visit(SQLExprTableSource x) {
		SQLName table = (SQLName) x.getExpr();
		String tableName = table.getSimpleName();

		// 改写tableName
		print0("new_" + tableName.toUpperCase());

		return true;
	}

}
