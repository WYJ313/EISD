package gui.softwareMeasurement.metricBrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;

public class RangeDecisionDlg extends JDialog {
	private static final long serialVersionUID = 7171293110985741827L;

	JPanel contentPane = new JPanel();
	
	JLabel label;
	JComboBox<String> rangeBox;
	MetricCalculationDlg mDlg;
	private ItemChoosenPane choosenPane;

	public RangeDecisionDlg(JDialog dlg) {
		mDlg = (MetricCalculationDlg)dlg;
		
		init();
		
		this.add(contentPane, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(buttonPane, BorderLayout.SOUTH);
		
		JButton lastStepBtn = new JButton("��һ��");
		lastStepBtn.setActionCommand("Last Step");
		JButton okButton = new JButton("��һ��");
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
					List<ProjectTreeNode> nodes = getSelectedNodes();
					if(nodes == null || nodes.isEmpty()) {
						JOptionPane.showMessageDialog(null, "��ȷ����������ʵ��������Χ��");
					} else {
						EntitySelectionDlg dlg = new EntitySelectionDlg(mDlg, RangeDecisionDlg.this);
						setVisible(false);
						dlg.setVisible(true);
					}
				}
				if(command.equals("Cancel")) {
					dispose();
					mDlg.dispose();
				}
				if(command.equals("Last Step")) {
					RangeDecisionDlg.this.dispose();
					mDlg.setVisible(true);
				}
			}
		};
		okButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		lastStepBtn.addActionListener(buttonListener);
		
		add(buttonPane, BorderLayout.SOUTH);
	}

	private void init() {
		setModal(true);
		
		setTitle("������ʵ��������Χ");
		setMinimumSize(new Dimension(700,400));
		setLocationRelativeTo(null);
		
		contentPane.setLayout(null);
		label = new JLabel("������Χ��");
		rangeBox = new JComboBox<String>();
		rangeBox.addItem("ϵͳ");
		
		List<Item> projectNodes = mDlg.manager.getAllProjectNodesAsItemList();
		String[] columnNames = {"", "ϵͳ","·��"};
		choosenPane = new ItemChoosenPane(projectNodes, columnNames, true);
		
		contentPane.add(choosenPane);
		if(mDlg.metricGranularity.equals("��")) {
			rangeBox.addItem("�汾");
		}
		if(mDlg.metricGranularity.equals("��")) {
			rangeBox.addItem("�汾");
			rangeBox.addItem("��");
		}
		
		rangeBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					contentPane.remove(choosenPane);
					contentPane.validate();
					if(e.getItem().equals("ϵͳ")) {
						List<Item> projectNodes = mDlg.manager.getAllProjectNodesAsItemList();
						String[] columnNames = {"", "ϵͳ","·��"};
						choosenPane = new ItemChoosenPane(projectNodes, columnNames, true);
						contentPane.add(choosenPane);
						repaint();
					}
					if(e.getItem().equals("�汾")) {
						List<Item> projectNodes = mDlg.manager.getAllVersionNodesAsItemList();
						String[] columnNames = {"", "�汾","����ϵͳ", "·��"};
						choosenPane = new ItemChoosenPane(projectNodes, columnNames, true);
						contentPane.add(choosenPane);
						repaint();
					}
					if(e.getItem().equals("��")) {
						List<Item> projectNodes = mDlg.manager.getAllPackageNodesAsItemList();
						String[] columnNames = {"", "��", "�����汾","����ϵͳ"};
						choosenPane = new ItemChoosenPane(projectNodes, columnNames, true);
						contentPane.add(choosenPane);
						repaint();
					}
				}
				
			}
		});
		
		
		contentPane.add(label);
		contentPane.add(rangeBox);
		
		contentPane.add(choosenPane);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		int contentPaneWidth = contentPane.getWidth();
		int fontSize = getFont().getSize();
		label.setBounds(50, 30, fontSize * 7, fontSize + 6);
		rangeBox.setBounds(label.getX() + label.getWidth(), label.getY() - 1, 
				contentPaneWidth - 2*label.getX() - label.getWidth(), fontSize + 8);
		choosenPane.setBounds(label.getX(), label.getY() + label.getHeight() + 20,
				contentPaneWidth - 2*label.getX(), contentPane.getHeight()-70);
	}
	
	public List<ProjectTreeNode> getSelectedNodes() {
		List<Item> itemList = choosenPane.getSelectedItems();
		List<ProjectTreeNode> projectNodeList = new ArrayList<ProjectTreeNode>();
		for (Item item : itemList) projectNodeList.add((ProjectTreeNode)item);
		return projectNodeList;
	}
}
