package cn.edu.fjut.bean;

public class SQLTree {
	private SQLTreeNode root;
	
	public SQLTree()
	{			
		root = new SQLTreeNode(SQLTreeNode.ROOT, "ROOT") ;
	}

	public SQLTreeNode getRoot() {
		return root;
	}
}
