package gui.softwareMeasurement.structureBrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.softwareMeasurement.structureBrowser.tree.NodeKind;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;
import gui.softwareMeasurement.structureBrowser.tree.VersionNode;

public class CloseVersionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4417224848346055889L;
	private final JPanel contentPanel = new JPanel();
	private ProjectTreeManager manager;
	private VersionChoosenPane choosePane;
	private ScrollComboBox<String> comboBox;
	
	public CloseVersionDialog(ProjectTreeManager manager) {
		this.manager = manager;
		setTitle("ѡ��Ҫ�رյİ汾");
		setSize(450, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		init();
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("ȷ��");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		ButtonHandler handler = new ButtonHandler();
		okButton.addActionListener(handler);
		cancelButton.addActionListener(handler);
		
	}
	
	private void init() {
		JLabel label = new JLabel("ѡ����Ŀ:");
		int fontSize = label.getFont().getSize();
		label.setBounds(50,30, fontSize*8,fontSize + 5);
		contentPanel.add(label);
		List<ProjectTreeNode> proNodes = manager.getAllProjectNodes();
		comboBox = new ScrollComboBox<String>();
		comboBox.setBounds(label.getX() + label.getWidth()
 				, 30, 250, comboBox.getFont().getSize() + 5);
		contentPanel.add(comboBox);
		for(ProjectTreeNode node : proNodes) {
			comboBox.addItem(node.getSimpleName());
		}
		ProjectTreeNode node = manager.getProjectNodeByProjectName((String)comboBox.getSelectedItem());
		List<File> versions = parseVersion(node);
		choosePane = new VersionChoosenPane(versions);
		choosePane.setBounds((450-350)/2, 80, 350, 150);
		contentPanel.add(choosePane);
		
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					ProjectTreeNode node = manager.getProjectNodeByProjectName((String)comboBox.getSelectedItem());
					List<File> versions = parseVersion(node);
					if(choosePane != null) {
						contentPanel.remove(choosePane);
					}
					choosePane = new VersionChoosenPane(versions);
					choosePane.setBounds((450-350)/2, 80, 350, 150);
					contentPanel.add(choosePane);
				}
			}
		});
	}
	
	
	
	/**
	 * ������Ŀ�ڵ��ȡ�ڵ��µİ汾��Ϣ
	 * @param node
	 * @return
	 */
	private List<File> parseVersion(ProjectTreeNode node) {
		if(node.getNodeKind() == NodeKind.PROJECT_NODE) {
			List<File> res = new ArrayList<File>();
			@SuppressWarnings("unchecked")
			Enumeration<ProjectTreeNode> children = node.children();
			while(children.hasMoreElements()) {
				ProjectTreeNode child = children.nextElement();
				if(child.getNodeKind() == NodeKind.VERSION_NODE) {
					VersionNode n = (VersionNode)child;
					File file = new File(n.getVersionDir());
					res.add(file);
				}
			}
			return res;
		}
		return null;
	}
	
	
	class ButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if("OK".equals(command)) {
				List<File> versions = choosePane.getSelectedVersion();
				if(versions != null) {
					final String proName = (String)comboBox.getSelectedItem();
					ProjectTreeNode node = manager.getProjectNodeByProjectName(proName);
					for(File file : versions) {
						@SuppressWarnings("unchecked")
						Enumeration<ProjectTreeNode> versionNodes = node.children();
						while(versionNodes.hasMoreElements()) {
							VersionNode versionNode = (VersionNode)versionNodes.nextElement();
							if(versionNode.getVersionDir().equals(file.getAbsolutePath())){
								versionNode.removeFromParent();
								break;
							}
						}
					}
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							manager.getTree().updateUI();
							manager.saveProject(proName);
						}
					});
				}
			}
			dispose();
		}
		
	}
}
