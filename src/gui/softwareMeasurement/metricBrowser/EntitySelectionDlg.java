package gui.softwareMeasurement.metricBrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.softwareMeasurement.structureBrowser.tree.NodeKind;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;

public class EntitySelectionDlg extends JDialog {
	private static final long serialVersionUID = 7919982647087878810L;

	JPanel contentPane = new JPanel();
	MetricCalculationDlg mDlg;
	RangeDecisionDlg rDlg;
	ItemChoosenPane choosenPane;
	List<Item> nodes = new ArrayList<Item>();
	
	public EntitySelectionDlg(JDialog metricCaldlg, JDialog rangeDecDlg) {
		setModal(true);
		setSize(780, 400);
		setLocationRelativeTo(null);
		setTitle("��ѡ��Ҫ������ʵ��");
		this.mDlg = (MetricCalculationDlg)metricCaldlg;
		this.rDlg = (RangeDecisionDlg)rangeDecDlg;
		
		this.add(contentPane, BorderLayout.CENTER);
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(buttonPane, BorderLayout.SOUTH);
		
		String[] columnNames = {"", "ϵͳ", "·��"};
		if(mDlg.metricGranularity.equals("ϵͳ")){
			List<ProjectTreeNode> projectNodeList = mDlg.manager.getAllProjectNodes();
			for (ProjectTreeNode node : projectNodeList) nodes.add(node);
		} else {
			List<ProjectTreeNode> rangeNodes = rDlg.getSelectedNodes();
			obtainNodesByGranularityInRangeNodes(rangeNodes);
			if(mDlg.metricGranularity.equals("�汾")) {
				columnNames = new String[]{"","�汾", "����ϵͳ", "·��"};
			}
			if(mDlg.metricGranularity.equals("��")) {
				columnNames = new String[]{"","��","�����汾", "����ϵͳ"};
			}
			if(mDlg.metricGranularity.equals("��")) {
				columnNames = new String[]{"","��","������","�����汾", "����ϵͳ", "·��"};
			}
		}
		
		choosenPane = new ItemChoosenPane(nodes, columnNames, true);
		contentPane.setLayout(null);
		contentPane.add(choosenPane);
		
		JButton lastStepBtn = new JButton("��һ��");
		lastStepBtn.setActionCommand("Last Step");
		JButton okButton = new JButton("����");
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(lastStepBtn);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		
		ActionListener buttonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("OK")) {
					String resultFilePath = mDlg.resPathField.getText();
					String[] metrics = mDlg.field.getText().split(";");
					List<Item> items = choosenPane.getSelectedItems();
					if(items == null || items.isEmpty()) {
						JOptionPane.showMessageDialog(null, "��ѡ��Ҫ��������ʵ�壡");
					} else {
						List<ProjectTreeNode> nodes = new ArrayList<ProjectTreeNode>();
						for(Item i : items) {
							ProjectTreeNode n = (ProjectTreeNode)i;
							nodes.add(n);
						}
						MetricCalculator calculator = new MetricCalculator(mDlg.formatDate, resultFilePath,
								nodes, metrics, mDlg.srcTabbedPane);
						calculator.start();
						dispose();
					}
				}
				if(command.equals("Cancel")) {
					mDlg.dispose();
					if(rDlg != null) {
						rDlg.dispose();
					}
					dispose();
				}
				if(command.equals("Last Step")) {
					EntitySelectionDlg.this.dispose();
					if(rDlg != null) {
						rDlg.setVisible(true);
					} else {
						mDlg.setVisible(true);
					}
				}
			}
		};
		lastStepBtn.addActionListener(buttonListener);
		okButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
	}

	public void paint(Graphics g) {
		super.paint(g);
		
		int xVar = 30;
		int yVar = 20;
//		setSize(choosenPane.getTable().getWidth()*3/4, getHeight());
		choosenPane.setBounds(xVar, yVar, contentPane.getWidth() - 2*xVar,
				contentPane.getHeight() - 2*yVar);
		
	}
	
	private void obtainNodesByGranularityInRangeNodes(List<ProjectTreeNode> rangeNodes) {
		String granularity = mDlg.metricGranularity;
		for(ProjectTreeNode n : rangeNodes) {
			if(granularity.equals("�汾")) {
				if(n.getNodeKind() == NodeKind.VERSION_NODE) {
					nodes.add(n);
				}
			}
			if(granularity.equals("��")) {
				if(n.getNodeKind() == NodeKind.PACKAGE_NODE) {
					nodes.add(n);
				}
			}
			if(granularity.equals("��")) {
				if(n.getNodeKind() == NodeKind.CLASS_NODE) {
					nodes.add(n);
				}
			}
			List<ProjectTreeNode> children = n.getChildNodes();
			obtainNodesByGranularityInRangeNodes(children);
		}
	}
	
	
	/**
	 * ѡ���Ķ�����Χ��ProjectTreeNode�������ٴ�����Ѱ�����ҵ���Ӧ���ȵ��ӽڵ�
	 * @param n 
	 * @return ���²�ѯ�Ĵ���
	 */
/*	private int getLevelByGranularity(ProjectTreeNode n) {
		String granularity = mDlg.metricGranularity;
		int res = 1;
		ProjectTreeNode node = (ProjectTreeNode)n.getChildAt(0);
		if(granularity.equals("�汾")) {
			while(node != null && node.getNodeKind() != NodeKind.VERSION_NODE) {
				node = (ProjectTreeNode)node.getChildAt(0);
				res ++;
			}
			if(node == null) res = -1;
			return res;
		}
		if(granularity.equals("��")) {
			while(node != null && node.getNodeKind() != NodeKind.PACKAGE_NODE) {
				node = (ProjectTreeNode)node.getChildAt(0);
				res ++;
			}
			if(node == null) res = -1;
			return res;
		}
		if(granularity.equals("��")) {
			while(node != null && node.getNodeKind() != NodeKind.CLASS_NODE) {
				node = (ProjectTreeNode)node.getChildAt(0);
				res ++;
			}
			if(node == null) res = -1;
			return res;
		}
		return -1;
	}
*/
	
}
