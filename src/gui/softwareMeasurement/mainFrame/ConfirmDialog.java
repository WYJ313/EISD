package gui.softwareMeasurement.mainFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ConfirmDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1801035877465470549L;
	static int res = -1;

	ConfirmDialog(String title, String message) {
		setModal(true);
		setTitle(title);

		JPanel mainPane = new JPanel();
		JPanel labelPane = new JPanel();

		mainPane.setLayout(new BorderLayout());
		JLabel label = new JLabel(message);
		labelPane.add(label);
		mainPane.add(labelPane, BorderLayout.CENTER);

		final JCheckBox checkBox = new JCheckBox("�Ժ�����ʾ�˶Ի���");

		mainPane.add(checkBox, BorderLayout.SOUTH);

		getContentPane().add(mainPane, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("ȷ��");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (command.equals("OK")) {
					if (checkBox.isSelected()) {
						File file = new File("res/.setting");
						SAXReader reader = new SAXReader();
						try {
							Document document = reader.read(file);
							Element root = document.getRootElement();
							Iterator<?> it = root.elementIterator();
							while (it.hasNext()) {
								Element ele = (Element) it.next();
								if(ele.getName().equals("alert")) {
									Attribute attribute = ele.attribute("value");
									if (attribute.getStringValue()
											.equals("true")) {
										attribute.setValue("false");// �޸����Խڵ��ֵ
									}
									break;
								}
							}
							OutputFormat format = OutputFormat.createPrettyPrint();
							format.setEncoding("utf8");
							FileWriter writer;
							writer = new FileWriter(file);
							XMLWriter xmlWriter = new XMLWriter(writer, format);
							xmlWriter.write(document);
							xmlWriter.flush();
							xmlWriter.close();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 

					}
					res = 1;
				} else {
					res = -1;
				}
				dispose();
			}
		};

		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static int showDlg(String title, String message) {
		new ConfirmDialog(title, message + "        ");
		return res;
	}

}
