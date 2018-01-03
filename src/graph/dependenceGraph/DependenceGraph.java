package graph.dependenceGraph;

import graph.basic.AbstractGraph;

/**
 * @author Zhou Xiaocong
 * @since 2017��1��8��
 * @version 1.0
 *
 */
public class DependenceGraph extends AbstractGraph {
	DependenceGraphKind kind = DependenceGraphKind.DGK_ALL;

	public DependenceGraph(String id) {
		super(id);
	}

	public DependenceGraph(String id, DependenceGraphKind kind) {
		super(id);
		this.kind = kind;
	}
	
	public DependenceGraphKind getKind() {
		return kind;
	}
}
