package cn.edu.fjut.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;

public class SQLTreeNode implements Serializable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6205815808743619432L;
	//"WithSubSequery"和"WithEntry"被抽取出来，因为需要"WithSubSequery"来提示之后会跟着若干个"WithEntry",而"WithEntry"则是用来将一些Sequery包装起来．
	// Join类型不能删，是一个用来区别的很重要指标．例如3700和２１４０的区别：
	// 3700:select count(distinct p.id) from person p inner join writer w where p.year_born = 1935;  
	// 2140:select count(distinct id) from person p natural join writer w where p.year_born = 1935
	private static final String[] TempNodes = {"ROOT", "SubSelect",  "BinaryOp","MethodInvoke" ,  "UnionQuery", "On","Left", "Right", 
			"UnionQuery","InList","List", "InSubQuery", "COMMA",
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
	
	public void removeNodeFromTree(String data)
	{
		SQLTreeNode root = getRoot();
		removeNode(root.getChildren(), data);
	}

	private void removeNode(List<SQLTreeNode> nodes, String data)
	{
		for(int i =0; i< nodes.size(); i++)
			if( nodes.get(i).getData().equals(data))
			{
				nodes.remove(i);
				return;
			}
			else {
				removeNode(nodes.get(i).getChildren(), data);
			}
		
	}

	private SQLTreeNode getRoot()
	{
		if(getData().equals("ROOT"))
			return this;
		else
			return getParent().getRoot();
	}

	public SQLTreeNode(SQLExpr type, String data) {
		data = getUnusedName(data);
		keys.add(data);
		this.data = data;
		children = new ArrayList<>();		
		tempNodeList = Arrays.asList(TempNodes);
	}

	private String getUnusedName(String data) {
		//　当前数据集中'1991'与1991等价，为了避免后续比较出错，因此需要将'1991'转换为1991
		if( data.matches("^'[0-9]*'$"))			
				data = data.substring(1, data.length() -1);
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
		node.setParent(this);
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
		String oldData = this.getData();
		this.data = getUnusedName(data);
		keys.remove(oldData);
		keys.add(this.data);
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
			if(sqlTreeNode.getSimpleData().equals("COMMA"))
			{
				result.addAll(sqlTreeNode.getChildrenWithoutTempNode());
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

	public boolean isTempNode()
	{
		return tempNodeList.contains(this.getSimpleData());
		
	}
	
	
}
