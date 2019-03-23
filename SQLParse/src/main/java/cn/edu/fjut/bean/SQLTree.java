package cn.edu.fjut.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author admin-u1064462
 *
 */
public class SQLTree implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8042021062345942273L;
	private SQLTreeNode root;
	private Map<String, SQLTreeNode> aliasMap ;	
	private Map<SQLTreeNode, String> nodetoAlias;

	
	public SQLTree()
	{			
		root = new SQLTreeNode(SQLTreeNode.ROOT, "ROOT") ;
		aliasMap = new HashMap<>();
		nodetoAlias= new HashMap<>();
 		SQLTreeNode.initialKeys();
	}

	public SQLTree deepCopy()
	{
		SQLTree newTree = null;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			newTree = (SQLTree)ois.readObject();
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newTree;
	}

	public SQLTreeNode getRoot() {
		return root;
	}
	
	public void putAlias(String key, SQLTreeNode value)
	{
		aliasMap.put(key, value);
		nodetoAlias.put(value, key);
	}
	
	public String getNodeAlias(SQLTreeNode node)
	{
		return nodetoAlias.get(node);
	}
	
	public SQLTreeNode getAlias(String key)
	{
		return aliasMap.get(key);
	}
	
	/**获得被重复访问的节点
	 * @return
	 */
	public List<String> getMulitiVisitNodes()
	{
		List<SQLTreeNode> multiVisitedNodes = new ArrayList<>();
		List<SQLTreeNode> visitedNodes = new ArrayList<>();
		getNodeVisitTimes(root,multiVisitedNodes, visitedNodes);
		List<String> result = new ArrayList<>();
		for (SQLTreeNode sqlTreeNode : multiVisitedNodes) {				
			result.add( sqlTreeNode.getSimpleData());		
		}
		return result;
	}
	


	private void getNodeVisitTimes(SQLTreeNode node, List<SQLTreeNode> multiVisitedNodes,
			List<SQLTreeNode> visitedNodes) {
		if(visitedNodes.contains(node))
		{
			multiVisitedNodes.add(node);
			return;
		}
		else
			visitedNodes.add(node);
		for (SQLTreeNode sqlTreeNode : node.getChildren()) {
			getNodeVisitTimes(sqlTreeNode, multiVisitedNodes, visitedNodes);
		}
		
	}


	
	
	/** 
	 * 用于获得叶子节点，由于可能存在多父节点，因此使用Set而不是List，以避免叶子节点被重复计算．
	 * @return
	 */
	public List<String> getLeafs()
	{
		Set<SQLTreeNode> leafs = new HashSet<>();
		collectLeaf(leafs, root);
		List<String> result = new ArrayList<>();
		for (SQLTreeNode sqlTreeNode : leafs) {			
			result.add(sqlTreeNode.getSimpleData());
		}
		return result;
	}
	
	/**
	 * 需要返回两组节点之和，一个是所有的叶子节点，一个是所有被重复连接的节点．
	 * @return
	 */
	public List<String> getResultNodes()
	{
		List<String> result = getLeafs();
		result.addAll(getMulitiVisitNodes());
		return result;
	}
	


	private void collectLeaf(Set<SQLTreeNode> leafs, SQLTreeNode root) {
		if(root.getChildren().size() > 0)
			for (SQLTreeNode sqlTreeNode : root.getChildren()) {
				collectLeaf(leafs, sqlTreeNode);
			}
		else
			leafs.add(root);
		
	}
	
	public String convertToSQL()
	{		
		StringBuffer sb = new StringBuffer();
		return travelNode(root, root.getChildren(), " ");
		//return sb.toString();
	}

	private String getSimpleName(String name)
	{
		if(name.contains(":"))
			name = name.split(":")[0];
		return name;
	}
	
	//修改了getParent，使得现在的node可以有多个parent,原来的方法暂时不可用了．
	private String travelNode(SQLTreeNode parent,  SQLTreeNode cur) {		
		String result = "";
		
		String nodeName = getSimpleName(cur.getData());
		if(nodetoAlias.get(cur) != null && cur.getParent()!= null && cur.getParent().equals(parent) == false)
			result = result +  nodetoAlias.get(cur);
			
		else if(cur.hasChild())
		{			
			switch (nodeName) {
			case "Select":	case "From": case "Where": 
				result = result +  nodeName + " ";
				result = result + travelNode(cur,  cur.getChildren(), ",");				
				break;
			case "COMMA": 
				result = result +  travelNode(cur, cur.getChildren(), ",");	
				break;
			case "BinaryOp": 
				result = result + travelNode(cur,  cur.getChildren(), " ");
				break;
			case "SubSelect":
				result = result + "(" ;
				result = result +  travelNode(cur, cur.getChildren(), " ");
				result = result + ")";		
				if(nodetoAlias.get(cur) != null)
				{
					result = result + " as " + nodetoAlias.get(cur);
				}
				break;
			case "GroupBy":
				result = result + " group by ";
				SQLTreeNode lastnode = cur.getChildren().get( cur.getChildren().size() -1 );
				// having 被当作groupby的一个子节点．　因此，在显示having之前不应该存在任何的,
				if(getSimpleName(lastnode.getData()).equals("Having"))
				{
					cur.getChildren().remove(cur.getChildren().size() -1);
					result = result + travelNode(cur, cur.getChildren(), ",");
					result = result + travelNode(cur, lastnode);
				}
				else
					result = result + travelNode(cur, cur.getChildren(), ",");	
				break;
			
			case "Function":
				if(cur.getChildren().size() > 1)
				{  
					SQLTreeNode func = cur.getChildren().get(0);
					String funcName = getSimpleName(func.getData());
					result = result + funcName + "( ";
					cur.getChildren().remove(0);
					result = result +  travelNode(cur, cur.getChildren() , ",");
					result = result + ")";
					if(nodetoAlias.get(cur) != null)
						result = result + " as " + nodetoAlias.get(cur);
				}
				break;
			case "Having":
				result = result +  nodeName + " ";
				result = result + travelNode(cur, cur.getChildren(), " ");
				break;
			default:				
				break;
			}			
		}
		else
			result =  nodeName + " ";
		return result;
	}
	
	protected String travelNode(SQLTreeNode current, List<SQLTreeNode> children, String sep) {
		String result = "";
		reorderNode(children);
		for (SQLTreeNode child : children) {
			result = result + travelNode(current, child);		
			result = result + sep;
		}
		if(result.length() > 0 &&  result.endsWith(",") )
			result = result.substring(0, result.length() -1);			
		return result;
	}


	private void reorderNode(List< SQLTreeNode> curNode) {
		SQLTreeNode selectNode = null;
		for (int i =0; i< curNode.size(); i++) {
			SQLTreeNode sqlTreeNode = curNode.get(i);
			if(getSimpleName( sqlTreeNode.getData()).equals("Select"))
			{
				selectNode = sqlTreeNode;
				curNode.remove(selectNode);				
				curNode.add(0, selectNode);				
			}
		}		
	}

	public int getNodeCount()
	{
		Set<String> visitedNodes = new HashSet<>();
		visitTree(root, visitedNodes, false);
		return visitedNodes.size();
	}
	
	public Set<String> getNodes(boolean ignoreOrderBy)
	{
		Set<String> visitedNodes = new HashSet<>();
		visitTree(root, visitedNodes, ignoreOrderBy);
		return visitedNodes;
	}	
	

	private void visitTree(SQLTreeNode node, Set<String> visitedNodes, boolean ignoreOrderBy)
	{		
		if(ignoreOrderBy && node.getSimpleData().equals("OrderBy"))
			return;
		if(node.isTempNode() == false)			
			visitedNodes.add(node.getData());
		for (SQLTreeNode child : node.getChildren())
		{
			visitTree(child, visitedNodes, ignoreOrderBy);
		}
		
	}
}
