package gui.softwareMeasurement.structureBrowser.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ProjectTreeModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9160950175133471782L;

	public ProjectTreeModel(TreeNode root) {
		super(root);
	}

	public boolean isLeaf(Object node) {
		if (node instanceof ProjectTreeNode) {
			ProjectTreeNode n = (ProjectTreeNode) node;
			if (n != null) {
				return (n.NODE_KIND == NodeKind.METHOD_NODE || n.NODE_KIND == NodeKind.FIELD_NODE);
			}
		}
		return false;// ע�⣺���ĸ��ڵ�û�й���PackageManagerTreeNode
	}
}
