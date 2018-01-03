package gui.softwareMeasurement.structureBrowser.tree;

import java.io.File;

public class ClassNode extends ProjectTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4333714407186896874L;
	String filePath;

	public ClassNode(NodeKind kind, String label, String filePath, String sizeMetrics) {
		super(kind, label, sizeMetrics);
		this.filePath = filePath;
	}

	/**
	 * ��ȡ��ڵ��Ӧ���ļ�����û�ж�Ӧ���ļ��򷵻ؿ�ֵ
	 * @return
	 */
	public File getFile() {
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public String getFilePath() {
		return filePath;
	}

	@Override
	public String getItemString(int index) {
		switch(index) {
		case 0:// ��
			return getSimpleName();
		case 1:// ��
			ProjectTreeNode packNode = (ProjectTreeNode)getParent();
			return packNode.getSimpleName();
		case 2:// �汾
			ProjectTreeNode verNode = (ProjectTreeNode)getParent().getParent();
			return verNode.getSimpleName();
		case 3:// ϵͳ
			ProjectTreeNode sysNode = (ProjectTreeNode)getParent().getParent().getParent();
			return sysNode.getSimpleName();
		case 4:// ·��
			return filePath;
		}
		return null;
	}
}
