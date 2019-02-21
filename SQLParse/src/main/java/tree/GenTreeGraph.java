package tree;

import javax.swing.JFrame;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.UnLabelEdge;

import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.bean.SQLTreeNode;

public class GenTreeGraph {
	public static void main(String[] args) {
		GenTreeGraph genTreeGraph = new GenTreeGraph();
        SQLTree tree = genTreeGraph.generateTreeDemo();
        genTreeGraph.display(tree);
        
	}
	
	public void display(SQLTree tree) {
		ListenableGraph<String, UnLabelEdge> g = convertToGraph(tree);
		CompleteGraph applet = new CompleteGraph();
		applet.setGraph(g);
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Abstract Syntax Tree of SQL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
	}
	
	private SQLTree generateTreeDemo() {
		SQLTree tree = new SQLTree();
		SQLTreeNode root = tree.getRoot();
		SQLTreeNode n1 = new SQLTreeNode("N1");
		SQLTreeNode n2 = new SQLTreeNode("N2");
		SQLTreeNode n3 = new SQLTreeNode("N3");
		SQLTreeNode n4 = new SQLTreeNode("N4");
		root.addChild(n1);
		root.addChild(n2);
		n2.addChild(n3);
		n3.addChild(n4);
		return tree;
	}

	public ListenableGraph<String, UnLabelEdge>  convertToGraph(SQLTree tree)
	{
		ListenableGraph<String, UnLabelEdge> g =
                new DefaultListenableGraph<>(new DefaultUndirectedGraph<>(UnLabelEdge.class));
		SQLTreeNode root = tree.getRoot();
		if(root.hasChild())
		{
			parseTree(root,g);
		}
		return g;
		
	}

	private void parseTree(SQLTreeNode root, ListenableGraph<String, UnLabelEdge> g) {
		g.addVertex(root.getData());
		for (SQLTreeNode child : root.getChildren()) {
			if(child == null)
				continue;
			g.addVertex(child.getData());
			g.addEdge(root.getData(), child.getData());
			if(child.hasChild())
				parseTree(child,g);
		}		
	}
	

}
