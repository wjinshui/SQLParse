package tree;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;
import org.jgrapht.*;
import org.jgrapht.demo.JGraphXAdapterDemo;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;

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

    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

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

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        
        add(component);
        
        resize(DEFAULT_SIZE);

      

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 100;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
        // that's all there is to it!...
    }
}
