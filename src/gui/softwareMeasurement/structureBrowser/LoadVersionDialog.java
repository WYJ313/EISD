package gui.softwareMeasurement.structureBrowser;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LoadVersionDialog extends ProjectBuildDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4336356389401204111L;

	public LoadVersionDialog(ProjectTreeManager manager) {
		super(manager);
		init();
	}

	private void init() {
		setTitle("ѡ��Ҫ���صİ汾");
		label.setText("װ���°汾����Ŀ��");
		label_1.setText("Ҫ���صİ汾Ŀ¼��");
		int fontSize = label.getFont().getSize();
		label.setBounds(30, 87, fontSize * 10, fontSize + 2);
		label_1.setBounds(30, 142, fontSize * 10, fontSize + 2);
		
		textField.setBounds(label_1.getX() + label_1.getWidth(), 139, 200, fontSize + 8);
		Point p = button.getLocation();
		
		textField.setText("");
		removeAllListener();

		p.y = label_1.getY() - 4;
		button.setLocation(p);
		
		Container container = comboBox.getParent();
		container.remove(comboBox);
		comboBox = new ScrollComboBox<String>();
		for(String proName : ProjectTreeManager.projectIds) {
			comboBox.addItem(proName);
		}
		comboBox.setBounds(label.getX() + label.getWidth(), 84, 200, fontSize + 8);
		container.add(comboBox);

//		final JLabel versionNameLabel = new JLabel("�汾�ţ�");
//		final JTextField versionText = new JTextField();
//		versionNameLabel.setBounds(label_1.getX() + label_1.getWidth()/2,
//				label_1.getY() + 50,
//				fontSize * 5, fontSize + 5);
//		versionText.setBounds(versionNameLabel.getX() + versionNameLabel.getWidth(),
//				versionNameLabel.getY(),
//				200, fontSize + 8);
//		container.add(versionNameLabel);
//		container.add(versionText);
		
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY
						| JFileChooser.OPEN_DIALOG);
				int res = chooser.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					textField.setText(file.getAbsolutePath());
					dealVersionsOfProject(file);
				}
			}
		});
		
		okButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(textField.getText() == null || textField.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(LoadVersionDialog.this, "������Ҫ���صİ汾Ŀ¼");
				} else {
					File f = new File(textField.getText());
					if(!f.exists()) {
						JOptionPane.showMessageDialog(LoadVersionDialog.this, "�汾Ŀ¼�����ڣ����������룡");
					} else {
						if(chooser != null) {
							ProjectInformation info = new ProjectInformation(null, (String)comboBox.getSelectedItem());
							List<File> versions = chooser.getSelectedVersion();
							BuildWorker worker = null;
							if(!chooser.isVisible()) {
								versions.add(f);
							} else if(chooser.isVisible() && versions.isEmpty()){
								JOptionPane.showMessageDialog(LoadVersionDialog.this, "��ѡ��汾��");
								return;
							}
							worker = new BuildWorker(manager, versions, info);
							worker.execute();
							dispose();
						}
					}
				}
			}
		});
		
		hideVersionSelectionPane();
	}

	private void removeAllListener() {
		for(MouseListener listener : button.getMouseListeners()) {
			button.removeMouseListener(listener);
		}
		for(MouseListener listener : okButton.getMouseListeners()) {
			okButton.removeMouseListener(listener);
		}
	}
	
}
