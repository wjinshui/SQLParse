package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

public class SQLTreeNode {
	public static SQLExpr ROOT = new SQLCharExpr("ROOT"); 
	private SQLObject type;
	private String  data;
	private SQLTree parent;
	private List<SQLTreeNode> children;
	
	
	public SQLTreeNode() {	
		
		children = new ArrayList<>();
	}
	
	public SQLTreeNode(String data)
	{
		this();
		this.data = data;		
	}

	public SQLTreeNode(SQLExpr type, String data) {
		this(data);
		this.type = type;
		
	}

	public void addChild(SQLTreeNode node)
	{
		children.add(node);
	}
	

	public SQLObject getType() {
		return type;
	}

	public void setType(SQLObject type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public SQLTree getParent() {
		return parent;
	}
	public void setParent(SQLTree parent) {
		this.parent = parent;
	}
	public List<SQLTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<SQLTreeNode> children) {
		this.children = children;
	}
	public boolean hasChild() {
		// TODO Auto-generated method stub
		return children.size()> 0;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return type.toString();
	}
	
	
}
