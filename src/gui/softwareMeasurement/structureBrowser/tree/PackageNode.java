package gui.softwareMeasurement.structureBrowser.tree;

public class PackageNode extends ProjectTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3043801101461317861L;

	public PackageNode(NodeKind kind, String label, String sizeMetrics) {
		super(kind, label, sizeMetrics);
	}

	@Override
	public String getItemString(int index) {
		switch(index) {
		case 0:// ��
			return getSimpleName();
		case 1:// �汾
			ProjectTreeNode verNode = (ProjectTreeNode)getParent();
			return verNode.getSimpleName();
		case 2:// ϵͳ
			ProjectTreeNode sysNode = (ProjectTreeNode)getParent().getParent();
			return sysNode.getSimpleName();
		case 3:
			return getLocation();
		}
		return null;
	}

}
