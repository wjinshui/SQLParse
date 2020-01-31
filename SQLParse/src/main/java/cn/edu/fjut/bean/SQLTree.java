package cn.edu.fjut.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.NodeSetData;

/**
 * @author admin-u1064462
 *
 */
public class SQLTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8042021062345942273L;
	private SQLTreeNode root;
	private Map<String, SQLTreeNode> aliasMap;
	private Map<SQLTreeNode, String> nodetoAlias;
	private List<String> needsortedItem ;
	public SQLTree() {
		root = new SQLTreeNode(SQLTreeNode.ROOT, "ROOT");
		aliasMap = new HashMap<>();
		nodetoAlias = new HashMap<>();
		String[] NeedSortedItem =  {"select", "common", "list", "natural join", "inner_join", "join","subselect"};
		needsortedItem = Arrays.asList(NeedSortedItem);
		SQLTreeNode.initialKeys();
	}

	public SQLTree deepCopy() {
		SQLTree newTree = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			newTree = (SQLTree) ois.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newTree;
	}

	public SQLTreeNode getRoot() {
		return root;
	}

	public void putAlias(String key, SQLTreeNode value) {
		aliasMap.put(key, value);
		nodetoAlias.put(value, key);
	}

	public String getNodeAlias(SQLTreeNode node) {
		return nodetoAlias.get(node);
	}

	public SQLTreeNode getAlias(String key) {
		return aliasMap.get(key);
	}

	/**
	 * 获得被重复访问的节点
	 * 
	 * @return
	 */
	public List<String> getMulitiVisitNodes() {
		List<SQLTreeNode> multiVisitedNodes = new ArrayList<>();
		List<SQLTreeNode> visitedNodes = new ArrayList<>();
		getNodeVisitTimes(root, multiVisitedNodes, visitedNodes);
		List<String> result = new ArrayList<>();
		for (SQLTreeNode sqlTreeNode : multiVisitedNodes) {
			result.add(sqlTreeNode.getSimpleData());
		}
		return result;
	}

	private void getNodeVisitTimes(SQLTreeNode node, List<SQLTreeNode> multiVisitedNodes,
			List<SQLTreeNode> visitedNodes) {
		if (visitedNodes.contains(node)) {
			multiVisitedNodes.add(node);
			return;
		} else
			visitedNodes.add(node);
		for (SQLTreeNode sqlTreeNode : node.getChildren()) {
			getNodeVisitTimes(sqlTreeNode, multiVisitedNodes, visitedNodes);
		}

	}

	/**
	 * 用于获得叶子节点，由于可能存在多父节点，因此使用Set而不是List，以避免叶子节点被重复计算．
	 * 
	 * @return
	 */
	public List<String> getLeafs() {
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
	 * 
	 * @return
	 */
	public List<String> getResultNodes() {
		List<String> result = getLeafs();
		result.addAll(getMulitiVisitNodes());
		return result;
	}

	private void collectLeaf(Set<SQLTreeNode> leafs, SQLTreeNode root) {
		if (root.getChildren().size() > 0)
			for (SQLTreeNode sqlTreeNode : root.getChildren()) {
				collectLeaf(leafs, sqlTreeNode);
			}
		else
			leafs.add(root);

	}



	public int getNodeCount() {
		Set<String> visitedNodes = new HashSet<>();
		boolean containsLimit = containsLimitNodes(root);
		visitTree(root, visitedNodes, containsLimit);
		return visitedNodes.size();
	}

	public Set<String> getNodes() {
		Set<String> visitedNodes = new HashSet<>();
		boolean containsLimit = containsLimitNodes(root);
		visitTree(root, visitedNodes, !containsLimit);
		return visitedNodes;
	}

	public String convertToString() {
		StringBuilder sb = new StringBuilder();
		buildAPT(root, sb);
		return sb.toString();
	}

	private void buildAPT(SQLTreeNode node, StringBuilder sb)
	{
		if(node != null)
		{
			sb.append("{" + node.getSimpleData() );			
			if(needSortNodes(node))				
				for (SQLTreeNode subNode : sortNodesByName(node.getChildren())) 
					buildAPT(subNode, sb);				
			else 
				for (SQLTreeNode subNode : node.getChildren()) 
					buildAPT(subNode, sb);						
			sb.append("}");
		}
	}


	private boolean needSortNodes(SQLTreeNode node) {
		if(needsortedItem.contains(node.getSimpleData()))
			return true;
		if(node.getSimpleData().equalsIgnoreCase("BinaryOp"))
			for (SQLTreeNode child : node.getChildren()) {
				if(child.getSimpleData().equals("=") || child.getSimpleData().equalsIgnoreCase("AND") || child.getSimpleData().equalsIgnoreCase("OR"))
					return true;
			}
		return false;
	}

	SQLTreeNode[] sortNodesByName(List<SQLTreeNode> nodes) {
		SQLTreeNode[] result = new SQLTreeNode[nodes.size()];
		nodes.toArray(result);
		Arrays.sort(result);
		return result;
	}

	/**
	 * 如果一个SQL语句中只有orderby 且没有limit，则该orderby对结果的输出没有影响，因此可以删除该orderby节点
	 */
	public void removeOrderBy(SQLTreeNode node) {
		for (int i = 0; i < node.getChildren().size(); i++) {
			SQLTreeNode child = node.getChildren().get(i);
			if (child.getSimpleData().equalsIgnoreCase("OrderBy")) {
				node.getChildren().remove(i);
				i--;
			} else if (child.getChildren().size() > 0)
				removeOrderBy(child);
		}

	}
	
	public void removeOrderBy() {
		removeOrderBy(root);

	}

	public boolean containsLimitNodes() {		
		return containsLimitNodes(root);
	}
	
	public boolean containsLimitNodes(SQLTreeNode node) {
		boolean result = false;
		for (SQLTreeNode child : node.getChildren()) {
			if (child.getSimpleData().equalsIgnoreCase("Limit"))
				return true;
			if (containsLimitNodes(child))
				return true;
		}
		return result;
	}

	private void visitTree(SQLTreeNode node, Set<String> visitedNodes, boolean ignoreOrderBy) {
		if (ignoreOrderBy && node.getSimpleData().equalsIgnoreCase("OrderBy"))
			return;
		// if(node.isTempNode() == false)
		visitedNodes.add(node.getData());
		for (SQLTreeNode child : node.getChildren()) {
			visitTree(child, visitedNodes, ignoreOrderBy);
		}

	}
}
