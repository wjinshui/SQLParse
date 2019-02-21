package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

public class SQLTreeNode {
	public static SQLExpr ROOT = new SQLCharExpr("ROOT"); 
	private SQLObject type;
	private String  data;
	private SQLTreeNode parent;	
	private List<SQLTreeNode> children;
	private static Set<String> keys = new HashSet<>();
	
	
	
	public SQLTreeNode getParent() {
		return parent;
	}

	public void setParent(SQLTreeNode parent) {
		this.parent = parent;
	}

	public SQLTreeNode(String data)
	{		
		this( new SQLCharExpr(data), data);		
	}

	public SQLTreeNode(SQLExpr type, String data) {
		data = getUnusedName(data);
		keys.add(data);
		this.data = data;
		this.type = type;
		children = new ArrayList<>();		
	}

	private String getUnusedName(String data) {
		if(keys.contains(data) == false)
			return data;
		char ch = ':';
		int count = 0;
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext())
		{
			String str = iterator.next();
			if(str.split(":")[0].equals(data))
				count ++;
		}
		data = data + ch + count;
		return data;
	}

	public void addChild(SQLTreeNode node)
	{
		addChild(node, true);
	}
	
	
	public void addChild(SQLTreeNode node, boolean isRealParent)
	{
		if(isRealParent)
		{
			children.add(node);
			node.setParent(this);
		}
		//只是引用该子节点，但并不是创建者
		else
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
		
		return type.toString();
	}
	
	
}
