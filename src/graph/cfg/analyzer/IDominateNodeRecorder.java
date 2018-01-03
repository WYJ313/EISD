package graph.cfg.analyzer;

import java.util.List;

import graph.basic.GraphNode;

/**
 * @author Zhou Xiaocong
 * @since 2017��9��10��
 * @version 1.0
 *
 */
public interface IDominateNodeRecorder extends graph.cfg.IFlowInfoRecorder {
	
	public void setDominateNodeList(List<GraphNode> nodeSet);

	public List<GraphNode> getDominateNodeList();
}
