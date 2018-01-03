package gui.softwareMeasurement.metricBrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import gui.softwareMeasurement.mainFrame.Checker;
import gui.softwareMeasurement.structureBrowser.ProjectTreeManager;
import gui.softwareMeasurement.structureBrowser.ScrollComboBox;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;
import softwareMeasurement.measure.SoftwareMeasureIdentifier;
/**
 * ���������������
 * @author Wu zhangsheng
 */
public class MetricFrame extends JDialog {
	private static final long serialVersionUID = -3644949431429425570L;

	private JPanel contentPane = new JPanel();
	ItemChoosenPane choosenPanel;
	JTextField field;
	JTextField resPathField;
	JComboBox<String> comboBox;
	
	JLabel label;
	JLabel metricLabel;
	JLabel pathLabel;
	JButton metricChoosenBtn;
	
	
	String formatDate;
	String date;
	
	public MetricFrame(final ProjectTreeManager manager,final JTabbedPane srcTabbedPane) {
		setModal(true);
		setSize(500, 400);
		setLocationRelativeTo(null);

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = (calendar.get(Calendar.MONTH)+1);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		formatDate = year +"/"+ month +"/"+ day +" "+ hour + ":" + min+ ":"+ second;
		date = year+""+month+""+day+""+hour+""+min+""+second;
		setTitle("��������-" + formatDate);

		label = new JLabel("�������ȣ�");
		int fontSize = label.getFont().getSize();
		contentPane.add(label);
		
		metricLabel = new JLabel("���������");
		metricLabel.setBounds(50, 25, fontSize * 5, fontSize + 6);
		field = new JTextField();

		contentPane.add(metricLabel);
		contentPane.add(field);
		
		metricChoosenBtn = new JButton("ѡ��");
		metricChoosenBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MetricDescriptionDlg frame = new MetricDescriptionDlg(field);
				frame.setVisible(true);
			}
		});
		
		
		contentPane.add(metricChoosenBtn);
		
		
		this.setLayout(new BorderLayout());
		this.add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);
		comboBox = new ScrollComboBox<String>();
		comboBox.addItem("ϵͳ");
		comboBox.addItem("�汾");
		comboBox.addItem("��");
		comboBox.addItem("��");
		
		contentPane.add(comboBox);
		
		// ��ʼѡ��Ϊϵͳ�����Գ�ʼ��ʾ���е�ϵͳѡ��
		List<Item> projects = manager.getAllProjectNodesAsItemList();
		String[] columnNames = {" ", "ϵͳ", "·��"};
		choosenPanel = new ItemChoosenPane(projects, columnNames, true);
		
		contentPane.add(choosenPanel);
		
		pathLabel = new JLabel("����ļ����Ŀ¼��");
		resPathField = new JTextField();
		resPathField.setEditable(false);
		resPathField.setText(Checker.getResultDir().getAbsolutePath()+"\\"+"result"+date+".jsm");
		
		contentPane.add(pathLabel);
		contentPane.add(resPathField);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("ȷ��");
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		
		ActionListener buttonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command.equals("OK")) {
					if(isLegalInput()) {
						List<Item> items = choosenPanel.getSelectedItems();
						List<ProjectTreeNode> nodes = new ArrayList<ProjectTreeNode>();
						for(Item i : items) {
							ProjectTreeNode n = (ProjectTreeNode)i;
							nodes.add(n);
						}
						String[] metrics = field.getText().split(";");
						
						String resFilePath = resPathField.getText();
						MetricCalculator calculator = new MetricCalculator(formatDate, resFilePath, nodes, metrics, srcTabbedPane);
						calculator.start();
						dispose();
					}
				}
				if(command.equals("Cancel")) {
					dispose();
				}
			}
		};
		okButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					contentPane.remove(choosenPanel);
					validate();
					if(e.getItem().equals("ϵͳ")) {
						List<Item> projects = manager.getAllProjectNodesAsItemList();
						String[] columnNames = {" ", "ϵͳ", "·��"};
						choosenPanel = new ItemChoosenPane(projects, columnNames, true);
						contentPane.add(choosenPanel);
						repaint();
					}
					if(e.getItem().equals("�汾")) {
						List<Item> versions = manager.getAllVersionNodesAsItemList();
						String[] columnNames = {" ", "�汾", "����ϵͳ", "·��"};
						choosenPanel = new ItemChoosenPane(versions, columnNames, true);
						contentPane.add(choosenPanel);
						repaint();
						
					}
					if(e.getItem().equals("��")) {
						List<Item> packages = manager.getAllPackageNodesAsItemList();
						String[] columnNames = {" ", "��", "�����汾", "����ϵͳ"};
						choosenPanel = new ItemChoosenPane(packages, columnNames, true);
						contentPane.add(choosenPanel);
						repaint();
					}
					if(e.getItem().equals("��")) {
						List<Item> classes = manager.getAllClassNodesAsItemList();
						String[] columnNames = {" ", "��", "������", "�����汾","����ϵͳ", "·��"};
						choosenPanel = new ItemChoosenPane(classes, columnNames, true);
						contentPane.add(choosenPanel);
						repaint();
					}
				}
				
			}
		});
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		int fontSize = getFont().getSize();
		field.setBounds(metricLabel.getX() + metricLabel.getWidth(), 25, 
				getWidth() - 2*metricLabel.getX() - metricLabel.getWidth() - 40, fontSize + 8);
		metricChoosenBtn.setBounds(field.getX() + field.getWidth() + 5, 25, 
				fontSize * 5, field.getHeight());
		
		label.setBounds(metricLabel.getX(), metricLabel.getY() + metricLabel.getHeight()+10,
				fontSize * 5, fontSize + 6);
		comboBox.setBounds(label.getX() + label.getWidth(), 50, 
				field.getWidth(), field.getHeight());
		
		pathLabel.setBounds(label.getX(),  label.getY() + label.getHeight() + 10,
				fontSize * 9, fontSize + 8);
		resPathField.setBounds(pathLabel.getX()+pathLabel.getWidth(), pathLabel.getY(),
				getWidth() - 2*pathLabel.getX() - pathLabel.getWidth(), fontSize + 8);

		choosenPanel.setBounds(pathLabel.getX(), pathLabel.getY() + pathLabel.getHeight() + 10, 
				label.getWidth() + comboBox.getWidth(),  getHeight() - 100 - comboBox.getY() - comboBox.getHeight());
		
	}
	
	private boolean isLegalInput() {
		String[] metrics = field.getText().split(";");
		for(String metricId : metrics){
			if(!SoftwareMeasureIdentifier.hasMeasure(metricId)) {
				JOptionPane.showMessageDialog(null, "Ҫ����Ķ��������ʽ�������������룡");
				return false;
			}
		}
		
		if(!choosenPanel.hasSelectedItems()) {
			JOptionPane.showMessageDialog(null, "��ѡ��Ҫ��������ʵ�壡");
			return false;
		}
		return true;
	}
	
//	public static void main(String args[]) {
//		MetricFrame frame = new MetricFrame();
//		frame.setVisible(true);
//	}
}

