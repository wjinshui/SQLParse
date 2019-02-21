package org.jgrapht.graph;

import org.jgrapht.graph.IntrusiveEdge;

public class UnLabelEdge extends IntrusiveEdge {
	private static final long serialVersionUID = 3258408452177932857L;

	/**
	 * Retrieves the source of this edge. This is protected, for use by subclasses
	 * only (e.g. for implementing toString).
	 *
	 * @return source of this edge
	 */
	protected Object getSource() {
		return source;
	}

	/**
	 * Retrieves the target of this edge. This is protected, for use by subclasses
	 * only (e.g. for implementing toString).
	 *
	 * @return target of this edge
	 */
	protected Object getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "";
		// return "( " + source + " : " + target + ")";
	}
}
