package tree;

import java.awt.Toolkit;

import javax.swing.JApplet;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.UnLabelEdge;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on
 * JGraphAdapterDemo.
 *
 */
@SuppressWarnings("deprecation")
public class CompleteGraph
    extends
    JApplet
{
    private static final long serialVersionUID = 2202072534703043194L;
    

    private JGraphXAdapter<String, UnLabelEdge> jgxAdapter;
    
    ListenableGraph<String, UnLabelEdge> graph;

    public void setGraph(ListenableGraph<String, UnLabelEdge> graph) {
		this.graph = graph;
	}

    @Override
    public void init()
    {
        // create a JGraphT graph
        ListenableGraph<String, UnLabelEdge> g =   graph;

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        //
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);        
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);        
        add(component);
        
        treeLayout();
       // fastOrganicLayout();       
        //circleLayout();      
    }

	private void treeLayout() {
		mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter);
        layout.setHorizontal(false);
        layout.setNodeDistance(30);
        layout.setLevelDistance(60);
        layout.execute(jgxAdapter.getDefaultParent());
	}

	private void circleLayout() {
		mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        int radius = 100;
        layout.setX0((Toolkit.getDefaultToolkit().getScreenSize().width / 2.0 ) - radius - 300);
        layout.setY0((Toolkit.getDefaultToolkit().getScreenSize().height / 2.0) - radius - 300);
        layout.setRadius(radius);
        layout.setMoveCircle(true);        
        layout.execute(jgxAdapter.getDefaultParent());
	}

	private void fastOrganicLayout() {
		mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter); 
        // set some properties 
        layout.setForceConstant(40); // the higher, the more separated 
        layout.setDisableEdgeStyle(false); // true transforms the edges and makes them direct lines 

        // layout graph 
        layout.execute(jgxAdapter.getDefaultParent());
	}
}
