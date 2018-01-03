package gui.softwareMeasurement.resultBrowser;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import gui.softwareMeasurement.mainFrame.StatusBar;

public class ResultTreePane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3042646050281948271L;
	ResultFileTree tree;
	
	public ResultTreePane(JTabbedPane tabbedPane, StatusBar bar) {
		tree = new ResultFileTree(bar);
		tree.registerTextArea(tabbedPane);
		setViewportView(tree);
	}

}
