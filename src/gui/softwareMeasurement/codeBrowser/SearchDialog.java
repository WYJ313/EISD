package gui.softwareMeasurement.codeBrowser;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import gui.softwareMeasurement.mainFrame.JsMetricFrame;

public class SearchDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8275752496550006325L;
	private final JPanel contentPanel = new JPanel();
	private JButton button;
	private JButton cancelButton;
	private JLabel content;
	private JCheckBox checkBox;
	private JTextField textField;
	
	/*����ʱ����ظ�����־*/
	int offset = 0;
	int lastSearchStrLength = 0;
	int preLength = 0;
	boolean isFind = false;

	public SearchDialog(final Component viewer) {
		setTitle("����");
		setSize(350, 150);
		setLocation(3 * JsMetricFrame.desktopDimension.width / 4 - 175,
				JsMetricFrame.desktopDimension.height / 3 - 75);
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 334, 112);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		cancelButton = new JButton();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		contentPanel.add(cancelButton);

		button = new JButton();
		button.setMnemonic(KeyEvent.VK_ENTER);
		button.setEnabled(false);

		contentPanel.add(button);

		content = new JLabel("���ݣ�");
		contentPanel.add(content);

		textField = new JTextField();
		textField.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (textField.getText() == null
						|| textField.getText().equals("")) {
					button.setEnabled(false);
				} else {
					button.setEnabled(true);
				}
			}
		});
		
		contentPanel.add(textField);
		textField.setColumns(10);

		checkBox = new JCheckBox("���ִ�Сд");
		contentPanel.add(checkBox);

		setVisible(true);
		
		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				// �������������ת�Ƶ������ڣ���������������Ϊalways on top
				if(e.getOppositeWindow() instanceof JsMetricFrame) {
					setAlwaysOnTop(true);
				} else {// ת�Ƶ������Ĵ��ڣ�������Ϊalways on top
					setAlwaysOnTop(false);
				}
			}
			
		});
		
		setSearchViewer(viewer);
	}

	public void setSearchViewer(final Component textViewer) {
		removeAllListener();
		offset = 0;
		lastSearchStrLength = 0;
		preLength = 0;
		isFind = false;
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(textViewer);
				button.requestFocus();
			}
		});
		
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					search(textViewer);
					textField.requestFocus();
				} else {
					isFind = false;
					offset = 0;
					preLength = 0;
					lastSearchStrLength = 0;
				}
			}
		});
	}
	
	private void search(final Component textViewer) {
		if(textViewer != null) {
			FocusAdapter adapter = new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					isFind = false;
					offset = 0;
					preLength = 0;
					lastSearchStrLength = 0;
				}
			};
			checkBox.addFocusListener(adapter);

			String contentStr = textField.getText();
			if (contentStr == null || contentStr.trim().equals("")) {
				JOptionPane.showMessageDialog(SearchDialog.this,
						"������Ҫ���ҵ����ݣ�");
			} else {
				if(textViewer instanceof JSrcViewer) {
					JSrcViewer viewer = (JSrcViewer) textViewer;
					offset = viewer.setSelectedString(offset, contentStr,
							checkBox.isSelected());
					if (offset != -1) {// �ҵ�������
						isFind = true;
					}
					if (!isFind && offset == -1) {// �������δ�ҵ��򵯳��Ի���
						viewer.setCaretPosition(0);
						JOptionPane.showMessageDialog(SearchDialog.this,
								"δ�ҵ�Ҫ���ҵ����ݣ�");
					}
					if (isFind && offset == -1) {// �ҵ���һ������֮��ֱ�����û���ҵ��Ļ��򲻵����Ի���
						offset = viewer.setSelectedString(0, contentStr,
								checkBox.isSelected());
					}
				} else {
					JTextArea viewer = (JTextArea) textViewer;
					String fileContents = viewer.getText().substring(preLength);
					if(checkBox.isSelected()) {
						offset = fileContents.indexOf(contentStr);
					} else {
						String lowerFileContents = fileContents.toLowerCase();
						String lowerContentStr = contentStr.toLowerCase();
						offset = lowerFileContents.indexOf(lowerContentStr);
					}
					
					if (offset != -1) {// �ҵ�������
						isFind = true;
					}
					if (!isFind && offset == -1) {// �������δ�ҵ��򵯳��Ի���
						viewer.setCaretPosition(0);
						JOptionPane.showMessageDialog(SearchDialog.this,
								"δ�ҵ�Ҫ���ҵ����ݣ�");
					}
					if (isFind && offset != -1) {// �ҵ���һ������֮��ֱ�����û���ҵ��Ļ��򲻵����Ի���
						viewer.setSelectionStart(preLength + offset);
						viewer.setSelectionEnd(preLength + offset + contentStr.length());
						viewer.requestFocus();
					}
					lastSearchStrLength = contentStr.length();
					preLength += offset + lastSearchStrLength;

					if(isFind && offset == -1) {
						// �ҵ����ı���������б�־�ָ�
						isFind = false;
						offset = 0;
						preLength = 0;
						lastSearchStrLength = 0;
					}
				}
			}
		} 
	}

	private void removeAllListener() {
		ActionListener[] listeners = button.getActionListeners();
		for (ActionListener listener : listeners) {
			button.removeActionListener(listener);
			listener = null;
		}
		KeyListener[] ls = textField.getKeyListeners();
		for (KeyListener l : ls) {
			textField.removeKeyListener(l);
			l = null;
		}
	}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		
		int fontSize = content.getFont().getSize();
		content.setBounds(15, 30,
				fontSize * 3, fontSize + 6);
		textField.setBounds(content.getWidth() + content.getX() + 5, 32, 265,
				21);
		

		checkBox.setBounds(10, 83,
				fontSize * 5 + 50, fontSize + 6);
		
		String searchStr = "����";
		button.setText(searchStr);
		button.setSize(fontSize * 2 + 45, fontSize + 10);
		button.setLocation(175, checkBox.getY() - (button.getHeight()-checkBox.getHeight())/2);
		
		String cancelStr = "ȡ��";
		cancelButton.setText(cancelStr);
		cancelButton.setBounds(button.getWidth() + button.getX() + 10, button.getY(),
				fontSize * 2 + 45, fontSize + 10);
	}

}
