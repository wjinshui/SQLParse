import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import cn.edu.fjut.ast.MySQLParse;
import cn.edu.fjut.bean.SQLTree;
import cn.edu.fjut.util.NormalizedAPTED;
import tree.GenTreeGraph;

public class APTDemo {
	public static void main(String[] args) {
		BracketStringInputParser parser = new BracketStringInputParser();
		String sourceTree = "{a{x}{c{e}{f}}{d}}";
		String destinationTree = "{a{b}{c}{d}}";
		destinationTree = "{a{x}{c{f}{e}}{d}}";
		Node<StringNodeData> t1 = parser.fromString(sourceTree);
		Node<StringNodeData> t2 = parser.fromString(destinationTree);
		// Initialise APTED.
		APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
		// Execute APTED.
		float result = apted.computeEditDistance(t1, t2);
		System.out.println(result);
		testByGUI();

	}

	private static void testByGUI() {
		MySQLParse sqlParse = new MySQLParse();
		String sql;
		sql = "SELECT  adf, cxx, asdf, werq \n" + "FROM person p \n";		
		SQLTree tree = singleSQL(sqlParse, sql);		
		System.out.println(tree.convertToString());
		String source = tree.convertToString();
		
		sql = "SELECT  adf, asdf, werq, cxx \n" + "FROM person where xcc > 0  \n";
		tree = singleSQL(sqlParse, sql);
		String destination = tree.convertToString();
		BracketStringInputParser parser = new BracketStringInputParser();
		
		Node<StringNodeData> t1 = parser.fromString(source);
		Node<StringNodeData> t2 = parser.fromString(destination);
		// Initialise APTED.
		NormalizedAPTED<StringUnitCostModel, StringNodeData> apted = new NormalizedAPTED<>(new StringUnitCostModel());
		// Execute APTED.
		
		float dist = apted.computeEditDistance(t1, t2);
		System.out.printf("dist: %f, count of t1: %d, count of t2: %d \n", dist, t1.getNodeCount(), t2.getNodeCount());
		double sim = 0;	
		sim = 1 - dist;
		System.out.println(sim);
		

	}

	private static SQLTree singleSQL(MySQLParse sqlParse, String sql) {

		System.out.println(sql);
		GenTreeGraph genTreeGraph = new GenTreeGraph();
		SQLTree tree = sqlParse.parse(sql);
		genTreeGraph.display(tree);
		return tree;
	}
}
