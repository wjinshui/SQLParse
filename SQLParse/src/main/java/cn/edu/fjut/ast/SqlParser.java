package cn.edu.fjut.ast;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class SqlParser {

    public static void main(String[] args) {
        String sql = "select count(*) from person p where exists (select * from writer w  where w.id = p.id and p.year_born = 1935)";

        // 新建 MySQL Parser
        SQLStatementParser parser = new MySqlStatementParser(sql);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement sqlStatement = parser.parseStatement();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(visitor);

        System.out.println("getTables:" + visitor.getTables());
        System.out.println("getParameters:" + visitor.getParameters());
        System.out.println("getOrderByColumns:" + visitor.getOrderByColumns());
        System.out.println("getGroupByColumns:" + visitor.getGroupByColumns());
        System.out.println("--------------------------  print visitor -------------------------------------------------");

        // 使用select访问者进行select的关键信息打印
        SelectPrintVisitor selectPrintVisitor = new SelectPrintVisitor();
        sqlStatement.accept(selectPrintVisitor);

        System.out.println("-----------------------------  print outputvisitor ----------------------------------------------");
        // 最终sql输出
        StringWriter out = new StringWriter();
        TableNameVisitor outputVisitor = new TableNameVisitor(out);
        sqlStatement.accept(outputVisitor);
        System.out.println(out.toString());
    }

}

/**
 * 查询语句访问者
 * 
 * @author xiezhengchao
 * @since 2018/6/1 12:08
 */
 class SelectPrintVisitor extends SQLASTVisitorAdapter {

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        List<SQLSelectItem> selectItemList = x.getSelectList();
        selectItemList.forEach(selectItem -> {
            System.out.println("attr:" + selectItem.getAttributes());
            System.out.println("expr:" + SQLUtils.toMySqlString(selectItem.getExpr()));
        });

        System.out.println("table:" + SQLUtils.toMySqlString(x.getFrom()));
        SQLExpr expr = x.getWhere(); 
        Map<String, Object> temp  = expr.getAttributes();
        System.out.println(temp);
        System.out.println("where:" + SQLUtils.toMySqlString(x.getWhere()));
       // System.out.println("order by:" + SQLUtils.toMySqlString(x.getOrderBy().getItems().get(0)));
       // System.out.println("limit:" + SQLUtils.toMySqlString(x.getLimit()));

        return true;
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
