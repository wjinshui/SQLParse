package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

public class SQLTreeNode {
	
	//"WithSubSequery"和"WithEntry"被抽取出来，因为需要"WithSubSequery"来提示之后会跟着若干个"WithEntry",而"WithEntry"则是用来将一些Sequery包装起来．
	private static final String[] TempNodes = {"SubSelect", "INNER_JOIN","BinaryOp","MethodInvoke" ,  "UnionQuery", "On","Left", "Right", 
			"UnionQuery","InList","List", "InSubQuery",
			"BetweenOper","Range","NotExpr","Function" };
	private static List<String> tempNodeList;
	public static SQLExpr ROOT = new SQLCharExpr("ROOT"); 
	private SQLObject type;
	private String  data;
	private SQLTreeNode parent;	
	private List<SQLTreeNode> children;
	private static Set<String> keys = new HashSet<>();
	
	
	
	public static void initialKeys()
	{
		keys = new HashSet<>();
	}
	
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
	
	public String getSimpleData()
	{
		return data.split(":")[0];
	}

	public SQLTreeNode(SQLExpr type, String data) {
		data = getUnusedName(data);
		keys.add(data);
		this.data = data;
		children = new ArrayList<>();		
		tempNodeList = Arrays.asList(TempNodes);
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
		this.data = getUnusedName(data);
	}


	public List<SQLTreeNode> getChildren() {
		
		return children;
	}
	
	/**
	 * 去除无特定语义的子节点集合
	 * 与getChildren相比，它会先判断子节点是否为中间节点，如果是，则当其子节点的子节点加入．
	 * 其中有一个很特殊的节点需要考虑，即unionQuery
	 * @return
	 */
	public List<SQLTreeNode> getChildrenWithoutTempNode()
	{
		List<SQLTreeNode> children = getChildren();
		List<SQLTreeNode> result = new ArrayList<>();
		for (SQLTreeNode sqlTreeNode : children) {
			if(sqlTreeNode.getSimpleData().equals("UnionQuery"))
			{
				//result.addAll(sqlTreeNode.getChildrenWithoutTempNode());
				//TODO: UnionQuery是个很麻烦的事．．
			}
			
			else if(tempNodeList.contains(sqlTreeNode.getSimpleData()))
			{				
				result.addAll(sqlTreeNode.getChildrenWithoutTempNode() );
			}
			else {
				result.add(sqlTreeNode);
			}
		}
		return result;
	}
	
	public void setChildren(List<SQLTreeNode> children) {
		this.children = children;
	}
	public boolean hasChild() {
		return children.size()> 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		SQLTreeNode node = (SQLTreeNode) obj;		
		return getData().equals(node.getData());
	}
	
	@Override
	public int hashCode() {		
		return data.hashCode();
	}
	
	@Override
	public String toString() {		
		return data;
	}
	
	
}
