package cn.edu.fjut.util;

import at.unisalzburg.dbresearch.apted.costmodel.CostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.Node;

public class NormalizedAPTED<C extends CostModel, D> extends APTED<CostModel, D>
{

	public NormalizedAPTED(CostModel costModel) {
		super(costModel);		
	}

	@Override
	public float computeEditDistance(Node<D> t1, Node<D> t2) {
        int m_len = Math.max(t1.getNodeCount(), t2.getNodeCount()); 
        if (m_len == 0) {
            return 0;
        }
        float distance = super.computeEditDistance(t1, t2);
        if(distance < 0)
        	System.out.println(distance);
        
		return  (2 * distance) / (t1.getNodeCount() + t2.getNodeCount() + distance);
	}
}
