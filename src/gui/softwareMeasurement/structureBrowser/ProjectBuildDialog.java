package gui.softwareMeasurement.structureBrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ProjectBuildDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	protected JTextField textField;
	protected ProjectTreeManager manager;
	protected JComboBox<String> comboBox;
	protected JLabel label;
	protected JLabel label_1;
	protected JButton button;
	protected JButton okButton;
	private File dir_his;
	protected VersionChoosenPane chooser;
	final static String DIALOG_TITLE = "�½���Ŀ";
	private List<File> versionDirs;// ����������Ŀ�ļ����µİ汾��Ϣ
	private int fontSize;
	private int chooserX;

	/**
	 * Create the dialog.
	 */
	public ProjectBuildDialog(ProjectTreeManager manager) {
		this.manager = manager;
		versionDirs = new ArrayList<File>();
		setTitle(DIALOG_TITLE);
		setSize(440, 270);
		getContentPane().setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		label = new JLabel("����Ŀ¼��");
		fontSize = label.getFont().getSize();
		label.setBounds(30, 70, fontSize * 5, fontSize + 2);
		chooserX = label.getX();
		contentPanel.add(label);

		label_1 = new JLabel("��Ŀ���ƣ�");
		label_1.setBounds(30, 135, fontSize * 5, fontSize + 2);
		contentPanel.add(label_1);

		Vector<String> v = getHistory();
		if (v.size() > 0) {
			v.add("�����ʷ��¼...");
		}
		comboBox = new JComboBox<String>(v);
		contentPanel.add(comboBox);
		comboBox.setEditable(true);
		comboBox.setBounds(30 + fontSize * 5 + 10, 67, 247, fontSize + 8);

		button = new JButton("...");
		button.setBounds(comboBox.getX() + 247 + 10, 66, fontSize * 3 + 5, fontSize + 10);
		contentPanel.add(button);

		textField = new JTextField();
		textField.setBounds(30 + fontSize * 5 + 10, 132, 247, fontSize + 8);
		contentPanel.add(textField);
		textField.setColumns(10);

		String path = (String) comboBox.getSelectedItem();
		if (path != null) {
			String defaultName = path.substring(path.lastIndexOf("\\") + 1);
			textField.setText(defaultName);
		}
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String itemStr = (String) e.getItem();
					if (itemStr.equals("�����ʷ��¼...")) {
						if (dir_his != null && dir_his.exists()) {
							// ע�⣺�ļ�ɾ��ʱ��������ص��ļ�������Ҫ�رգ�����ɾ������
							dir_his.delete();
							comboBox.removeAllItems();
							textField.setText("");
							hideVersionSelectionPane();
						}
					} else {
						String path = (String) e.getItem();
						String defaultName = path.substring(path
								.lastIndexOf("\\") + 1);
						textField.setText(defaultName);
						File proDir = new File(path);
						dealVersionsOfProject(proDir);
					}
				}
			}

		});


		chooser = new VersionChoosenPane(null);
		chooser.setVisible(false);
		// ��ʼ����ѡ����������˰汾ѡ����չʾ�汾
		String comboBoxContent = (String) comboBox.getSelectedItem();
		if (comboBoxContent != null) {
			File file = new File(comboBoxContent);
			if (file.isDirectory()) {
				dealVersionsOfProject(file);
			}
		}
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("ȷ��");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("ȡ��");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		ButtonListener listener = new ButtonListener();

		button.addMouseListener(listener);
		okButton.addMouseListener(listener);
		cancelButton.addMouseListener(listener);
	}

	/**
	 * ������Ŀ�еİ汾��Ϣ
	 * @param proDir
	 */
	protected void dealVersionsOfProject(File proDir) {
		if (proDir.isDirectory()) {
			parseVersions(proDir);
			if (versionDirs.size() > 0) {
				addVersionSelectionPane();
			} else {
				hideVersionSelectionPane();
			}
		}
	}

	/**
	 * ������Ŀ�ļ����µİ汾
	 * @param projectDir
	 */
	private void parseVersions(File projectDir) {
		removeAllVersions();
		File listFiles[] = projectDir.listFiles();
		if (listFiles != null) {
			String projectDirName = projectDir.getName();
			Pattern p = Pattern.compile(projectDirName + "[-\\s*]");
			for (File f : listFiles) {
				if (f.isDirectory()) {
					Matcher m = p.matcher(f.getName());
					if (m.find()) {
						versionDirs.add(f);
					}
				}
			}
		}
	}

	/**
	 * �в�ͬ�İ汾�ļ��������ѡ����Ҫ���صİ汾
	 */
	protected void addVersionSelectionPane() {
		if (chooser != null) {
			if(!chooser.isVisible()) {
				this.setSize(getWidth(), getHeight() + 300);
				chooser.setVisible(true);
				setLocation(getX(), getY() - 150);
			}
			contentPanel.remove(chooser);
			chooser = new VersionChoosenPane(versionDirs);
			chooser.setBounds(chooserX, textField.getY() + 50,
					textField.getWidth() + label.getWidth() + 60, 300);
			contentPanel.add(chooser);
			this.repaint();
		}
	}

	/**
	 * û�в�ͬ�İ汾�ļ�����˵����Ϊ������Ŀ����Ҫ���汾�������� ͬʱ��chooser�е��������
	 */
	protected void hideVersionSelectionPane() {
		if (chooser != null && chooser.isVisible()) {
			this.setSize(getWidth(), getHeight() - 300);
			chooser.setVisible(false);
			setLocation(getX(), getY() + 150);
			this.repaint();
		}
	}

	private void removeAllVersions() {
		if(versionDirs != null) {
			for (int i = 0; i < versionDirs.size(); i++) {
				versionDirs.remove(i);
			}
		}
		versionDirs = new ArrayList<File>();
	}

	/**
	 * ��ȡѡ����Ĺ���Ŀ¼��¼
	 * 
	 * @return
	 */
	private Vector<String> getHistory() {
		Vector<String> v = new Vector<String>();

		dir_his = new File("res/dir_his");
		try {
			if (!dir_his.exists()) {
				dir_his.createNewFile();
			}
			Scanner scanner = new Scanner(dir_his);
			while (scanner.hasNextLine()) {
				String his_dir = scanner.nextLine();
				v.add(his_dir);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return v;
	}

	class ButtonListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			String command = button.getActionCommand();
			if (command.equals("...")) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY
						| JFileChooser.OPEN_DIALOG);
				int res = chooser.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					comboBox.setSelectedItem(file.getAbsolutePath());
				}
			}
			if (command.equals("OK")) {
				try {
					String projectName = textField.getText();
					String content = (String) comboBox.getSelectedItem();
					if (projectName == null || projectName.trim().equals("")) {
						JOptionPane.showMessageDialog(ProjectBuildDialog.this,
								"��������Ŀ��!");
					} else if (content == null || content.trim().equals("")) {
						JOptionPane.showMessageDialog(ProjectBuildDialog.this,
								"���������Ŀ¼!");
					} else {
						File contentFile = new File(content);
						if (!contentFile.exists()) {
							hideVersionSelectionPane();
							JOptionPane.showMessageDialog(
									ProjectBuildDialog.this, "����Ŀ¼������,������ѡ��");
						} else {
							if (dir_his != null && !dir_his.exists()) {
								dir_his.createNewFile();
							}
							FileWriter fw = new FileWriter(dir_his, true);
							PrintWriter pw = new PrintWriter(fw);
							Vector<String> v = getHistory();
							if (content != null && !v.contains(content)
									&& !content.equals("�����ʷ��¼...")) {
								pw.append(content + "\r\n");
							}
							pw.flush();
							pw.close();
							if (content != null && !"".equals(content.trim())
									&& projectName != null
									&& !"".equals(projectName.trim())) {
								manageProjectView(projectName, content);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (command.equals("Cancel")) {
				dispose();
			}
		}

		/**
		 * ����Ŀ¼�е�������ӵ���Ŀ�ļ�����
		 * 
		 * @param projectName
		 * @param dir
		 */
		private void manageProjectView(final String projectName,
				final String dir) {
			final File dirFile = new File(dir);
			if (dirFile.isFile()) {
				JOptionPane.showMessageDialog(ProjectBuildDialog.this,
						"ѡ�е���һ���ļ�����ѡ��һ���ļ��У�");
			}
			if (ProjectTreeManager.projectIds.contains(projectName)) {
				JOptionPane.showMessageDialog(ProjectBuildDialog.this,
						"��Ŀ���ظ�������������..");
			} else {
				
				Thread t = new Thread(new Runnable() {
					public void run() {
						// ͨ��manager��������Ŀ���
						ProjectInformation project = new ProjectInformation(
								dir, projectName);
						// ѡ��Ҫ���صİ汾֮��ֻ���ض�Ӧ�İ汾
						if (chooser != null && chooser.isVisible()) {
							List<File> versions = chooser.getSelectedVersion();
							if(versions.size() == 0) {// �û�û��ѡ��汾ʱ
								JOptionPane.showMessageDialog(null, "��ѡ��Ҫ���صİ汾��");
							} else {
								BuildWorker worker = new BuildWorker(manager, versions, project);
								worker.execute();
								dispose();
							}
						} else {// ���chooserû����ʾ����˵���Ը��ļ�����Ϊһ����һ����Ŀ
							List<File> version = new ArrayList<File>();
							File file = new File((String) comboBox.getSelectedItem());
							if(file.isDirectory()) {
								version.add(file);
								BuildWorker worker = new BuildWorker(manager, 
										version, project);
								worker.execute();
								dispose();
							}
						}
					}
				});
				t.start();
			}
		}

	}
	
}
