package gui.softwareMeasurement.structureBrowser.tree;

public class VersionNode extends ProjectTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8572632888985061268L;

	public VersionNode(NodeKind kind, String label, String sizeMetrics) {
		super(kind, label, sizeMetrics);
	}

	public String getVersionDir() {
		return getLocation();
	}

	@Override
	public String getItemString(int index) {
		switch(index) {
		case 0:// �汾
			return getSimpleName();
		case 1:// ϵͳ
			ProjectTreeNode sysNode = (ProjectTreeNode)getParent();
			return sysNode.getSimpleName();
		case 2:
			return getLocation();
		}
		return null;
	}
	
	
}
