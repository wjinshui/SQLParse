package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLTree {
	private SQLTreeNode root;
	private Map<String, SQLTreeNode> aliasMap ;
	private Map<SQLTreeNode, String> nodetoAlias;
	//private static int count = 0;
	
	public SQLTree()
	{			
		root = new SQLTreeNode(SQLTreeNode.ROOT, "ROOT") ;
		aliasMap = new HashMap<>();
		nodetoAlias= new HashMap<>();
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
	
	public List<SQLTreeNode> getLeafs()
	{
		List<SQLTreeNode> leafs = new ArrayList<>();
		collectLeaf(leafs, root);
		return leafs;
	}

	private void collectLeaf(List<SQLTreeNode> leafs, SQLTreeNode root) {
		if(root.getChildren().size() > 0)
			for (SQLTreeNode sqlTreeNode : leafs) {
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
				//travelNode(sb, node.getChildren(), " ");
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
}
