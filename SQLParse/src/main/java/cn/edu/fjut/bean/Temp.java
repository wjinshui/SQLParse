package cn.edu.fjut.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.CaseUtils;

public class Temp {
	private SQLTreeNode root;
	private Map<String, SQLTreeNode> aliasMap ;
	private final String[] SQLKeyWords = {"Select", "From", "Where", "GroupBy","Having" };
	private List<String> keywords;
	
	public Temp()
	{			
		root = new SQLTreeNode(SQLTreeNode.ROOT, "ROOT") ;
		aliasMap = new HashMap<>();
		keywords = Arrays.asList(SQLKeyWords);
	}

	public SQLTreeNode getRoot() {
		return root;
	}
	
	public void putAlias(String key, SQLTreeNode value)
	{
		aliasMap.put(key, value);
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
		return travelNode(root.getChildren(), " ");
		//return sb.toString();
	}

	private String getSimpleName(String name)
	{
		if(name.contains(":"))
			name = name.split(":")[0];
		return name;
	}
	private String travelNode(SQLTreeNode node) {		
		String result = "";
		String nodeName = getSimpleName(node.getData());
		if(node.hasChild())
		{			
			switch (nodeName) {
			case "Select":	case "From": case "Where": 
				result = result +  nodeName + " ";
				result = result + travelNode( node.getChildren(), ",");				
				break;
			case "COMMA": 
				result = result +  travelNode(node.getChildren(), ",");	
				break;
			case "BinaryOp": 
				result = result + travelNode( node.getChildren(), " ");
				break;
			case "SubSelect":
				result = result + "(" ;
				result = result +  travelNode(node.getChildren(), " ");
				result = result + ")";				
				break;
			case "GroupBy":
				result = result + " group by ";
				SQLTreeNode lastnode = node.getChildren().get( node.getChildren().size() -1 );
				// having 被当作groupby的一个子节点．　因此，在显示having之前不应该存在任何的,
				if(getSimpleName(lastnode.getData()).equals("Having"))
				{
					node.getChildren().remove(node.getChildren().size() -1);
					result = result + travelNode(node.getChildren(), ",");
					result = result + travelNode(lastnode);
				}
				else
					result = result + travelNode(node.getChildren(), ",");	
				break;
			
			case "Function":
				if(node.getChildren().size() > 1)
				{
					SQLTreeNode func = node.getChildren().get(0);
					String funcName = getSimpleName(func.getData());
					result = result + funcName + "( ";
					node.getChildren().remove(0);
					result = result +  travelNode(node.getChildren() , ",");
					result = result + ")";
				}
				break;
			case "Having":
				result = result +  nodeName + " ";
				result = result + travelNode(node.getChildren(), " ");
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
	
	protected String travelNode(List<? extends SQLTreeNode> curNode, String sep) {
		String result = "";
		for (SQLTreeNode cur : curNode) {
			result = result + travelNode( cur);		
			result = result + sep;
		}
		if(result.length() > 0 &&  result.endsWith(",") )
			result = result.substring(0, result.length() -1);			
		return result;
	}
}
