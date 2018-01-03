package gui.softwareMeasurement.mainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import gui.softwareMeasurement.codeBrowser.JSrcViewer;
import gui.softwareMeasurement.codeBrowser.SearchDialog;
import gui.softwareMeasurement.metricBrowser.MetricCalculationDlg;
import gui.softwareMeasurement.resultBrowser.RecorderTableFrame;
import gui.softwareMeasurement.structureBrowser.ProjectBuildDialog;
import gui.softwareMeasurement.structureBrowser.ProjectTreePanel;
import gui.softwareMeasurement.structureBrowser.tree.NodeKind;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTree;

/**
 * ����������
 * 
 * @author Wu zhangsheng
 */
public class JsMetricFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6286586547509855447L;
	final static String MENU_NEW_PROJECT_STR = "�½���Ŀ";
	final static String MENU_DEL_PROJECT_STR = "ɾ����Ŀ";
	final static String MENU_METRIC_STR = " ���� ";
	final static String MENU_RECORDER_BROWSER_STR = "������ʷ��¼";
	final static String MENU_CLOSE_STR = "�ر�";
	final static String MENU_CLOSE_ALL_STR = "�ر�����";
	final static String MENU_SEARCH_STR = "����";
	final static String MENU_CALCULATE_METRIC_STR = "�������ֵ";
	final static String MENU_METRIC_INFO_BROWSER_STR = "�鿴������Ϣ";
	
	JMenuItem mntmSearch;
	JMenuItem mntmClose;
	JMenuItem mntmCloseAll;

	public static Dimension desktopDimension = Toolkit.getDefaultToolkit()
			.getScreenSize();

	JSplitPane treeAndOtherPane;
	JSplitPane infoAndCRViewerPane;
	MainToolBar toolBar;
	StatusBar statusBar;
	ProjectTreePanel treePanel;

	SrcTabbedPane srcTabbedPane;

//	/* ������ʾ�����Ƿ�����������ͨ��SearcherDialog��setAlwaysOnTop�������ݹ�ȥ��*/
//	public static boolean isOpposedWindow = false;

	boolean isInit = true;

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "�����ʼ������������������");
		}
		UIDefaults uiDfts = UIManager.getLookAndFeelDefaults();
		uiDfts.put("SplitPane.background", new ColorUIResource(new Color(206, 221, 237)));

		Icon expandedIcon = new ImageIcon("res/img/expand.png");
		Icon collapsedIcon = new ImageIcon("res/img/collapse.png");
		uiDfts.put("Tree.expandedIcon", expandedIcon);
		uiDfts.put("Tree.collapsedIcon", collapsedIcon);
		
		boolean isAllFileExists = Checker.ifFileExists();
		if (isAllFileExists) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new JsMetricFrame();
				}
			});
		} else {
			int res = JOptionPane.showConfirmDialog(null,
					"��Դ�ļ�ȱʧ������������ܻᵼ������쳣���Ƿ�������", "����",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try{
							new JsMetricFrame();
						} catch (OutOfMemoryError e) {
							JOptionPane.showMessageDialog(null, "���ڴ�ռ䲻����");
						}
					}
				});
			}
		}
	}

	public JsMetricFrame() {
		setTitle("JsMetric");
		setSize(3 * desktopDimension.width / 4, 8 * desktopDimension.height / 9);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		toolBar = new MainToolBar(this);
		toolBar.setBackground(new Color(206, 221, 237));
		statusBar = new StatusBar();
		statusBar.setBackground(new Color(206, 221, 237));

		initMenuBar();
		
		srcTabbedPane = new SrcTabbedPane();
		JTabbedPane treeTabPane = new JTabbedPane();

		JScrollPane infoScroll = new JScrollPane();
		treePanel = createProjectTreePane(srcTabbedPane, infoScroll);
		

		// ��Ŀ������������
		treeAndOtherPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		treeAndOtherPane.setBackground(new Color(180, 205, 230));
		treeAndOtherPane.setDividerSize(8);
		
		// ���沿��Ϊ�����Ϣ�����������ļ�����ͽ�����
		infoAndCRViewerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		infoAndCRViewerPane.setBackground(new Color(180, 205, 230));
		infoAndCRViewerPane.setDividerSize(8);
		
		infoAndCRViewerPane.setContinuousLayout(true);
		// ����ΪԴ�������
		infoAndCRViewerPane.setTopComponent(srcTabbedPane);
		// ��������Ϣ�����
		JTabbedPane infoPane = new JTabbedPane();
		final JTextArea consoleText = new JTextArea();
		consoleText.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem("���");
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							consoleText.setText("");
						}
					});
					menu.add(item);
					menu.show(consoleText, e.getX(), e.getY());
				}
			}
		});

//		JTextPane infoTextPane = new JTextPane();
//		Log.setinfoArea(infoTextPane);
		infoPane.addTab("��Ϣ", infoScroll);
		
		JScrollPane consoleScroll = new JScrollPane(consoleText);
		infoPane.addTab("����̨", consoleScroll);
		infoAndCRViewerPane.setBottomComponent(infoPane);
		
		Log.setTextArea(consoleText);

		// �������Ŀ�ṹ�����
		treeAndOtherPane.setContinuousLayout(true);
		treeTabPane.addTab("��Ŀ�ṹ�����", treePanel);
		treeTabPane.setBackground(new Color(225, 230, 255));
		treeAndOtherPane.setLeftComponent(treeTabPane);
		// �ұ߾���ʣ�������ɵĽ���
		treeAndOtherPane.setRightComponent(infoAndCRViewerPane);

		getContentPane().add(treeAndOtherPane, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		// ��������������ڣ���������С����ʱ����������Ҳ���ɼ�
		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == Frame.ICONIFIED
						&& JSrcViewer.searcher != null) {
					JSrcViewer.searcher.setAlwaysOnTop(false);
					JSrcViewer.searcher.setVisible(false);
				}
				if (e.getNewState() == Frame.NORMAL
						&& JSrcViewer.searcher != null) {
					JSrcViewer.searcher.setAlwaysOnTop(true);
					JSrcViewer.searcher.setVisible(true);
				}
			}
		});

		
		// ���öԻ����һЩ��ʾ�������SearchDialog��Ҳ����Ӧ�����ã����������֤��������
		// ֻ�������ڵ���ǰ����������ʹ������������ϵͳ�������ǰ��
		addWindowFocusListener(new WindowAdapter() {
			// ��������ڻ�ȡ���˽�����SearchDialog�Ի�����ڣ������ö�
			public void windowGainedFocus(WindowEvent e) {
				if (JSrcViewer.searcher != null) {
					JSrcViewer.searcher.setAlwaysOnTop(true);
//					isOpposedWindow = true;
				}
			}

			// ���������ʧȥ�˽��㣬�ҽ��������������֮�������������SearchDialog���ö�
			public void windowLostFocus(WindowEvent e) {
				if (e.getOppositeWindow() == null) {
					if (JSrcViewer.searcher != null)
						JSrcViewer.searcher.setAlwaysOnTop(false);
				}
			}
		});

		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		// �������չʾ����
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				treeAndOtherPane.setDividerLocation(0.2);
				infoAndCRViewerPane.setDividerLocation(getHeight() / 10 * 6);
			}
		});
		
//		toolBar.setBounds(0, 0, getContentPane().getWidth(), 30);
//		statusBar.setBounds(0, getContentPane().getHeight() - 25,
//				getContentPane().getWidth(), 25);
		treePanel.requestFocusInWindow();
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu(" �ļ� ");
		menuBar.add(mnFile);

		JMenu mnPro = new JMenu(" ��Ŀ ");
		mnFile.add(mnPro);

		final JMenuItem mntmNewProject = new JMenuItem(MENU_NEW_PROJECT_STR);
		mnPro.add(mntmNewProject);

		final JMenuItem mntmDelProject = new JMenuItem(MENU_DEL_PROJECT_STR);
		mntmDelProject.setEnabled(false);
		mnPro.add(mntmDelProject);


		JMenu mnEdit = new JMenu(" �༭ ");
		
		mntmSearch = new JMenuItem(MENU_SEARCH_STR);
		mntmSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmSearch);

		mntmClose = new JMenuItem(MENU_CLOSE_STR);
		mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmClose);
		
		mntmCloseAll = new JMenuItem(MENU_CLOSE_ALL_STR);
		mnEdit.add(mntmCloseAll);
		
		menuBar.add(mnEdit);

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				System.out.println(command);
				if (command.equals(MENU_CLOSE_STR)) {
					int selectedIndex = srcTabbedPane.getSelectedIndex();
					if (selectedIndex > -1) {
						srcTabbedPane.remove(selectedIndex);
					}
				}
				if (command.equals(MENU_CLOSE_ALL_STR)) {
					if (srcTabbedPane != null) {
						srcTabbedPane.removeAll();
					}
				}
				if (command.equals(MENU_NEW_PROJECT_STR)) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							ProjectBuildDialog dlg = new ProjectBuildDialog(
									treePanel.getManager());
							dlg.setVisible(true);
						}
					});
				}
				if (command.equals(MENU_DEL_PROJECT_STR)) {
					if(Checker.isDeleteDlgAlert()) {
						int res = ConfirmDialog.showDlg("��ʾ", "ɾ����Ŀ����ɾ�������ϵ��ļ����Ƿ�ɾ����Ŀ \"" +
								treePanel.getManager().getSelectedNode().getSimpleName() +"\"��");
						if(res == 1) {
							treePanel.getManager().removeSelectedProject();
						}
					} else {
						treePanel.getManager().removeSelectedProject();
					}
				}
				if (command.equals(MENU_SEARCH_STR)) {// �����˵�
					if (JSrcViewer.searcher != null && srcTabbedPane.getTabCount() > 0) {
						if (!JSrcViewer.searcher.isVisible()) {// �����ĶԻ����Ѿ�����
							JSrcViewer.searcher.setVisible(true);
						}
					} else {// �����Ի��򲻴��ڣ�����Ҫ�½����½���ʱ�����Ҫ����Tab
						if (srcTabbedPane.getTabCount() > 0) {
							JScrollPane pane = (JScrollPane) srcTabbedPane
									.getSelectedComponent();
							JPanel panel = (JPanel) pane.getViewport()
									.getView();
							Component c = panel.getComponent(0);
							if(c instanceof JSrcViewer) {
								JSrcViewer v = (JSrcViewer) c;
								JSrcViewer.searcher = new SearchDialog(v);
							} else {
								JTextArea v = (JTextArea)c;
								JSrcViewer.searcher = new SearchDialog(v);
							}
						}
					}
				}
			}
		};
		mntmClose.addActionListener(listener);
		mntmCloseAll.addActionListener(listener);
		mntmNewProject.addActionListener(listener);
		mntmDelProject.addActionListener(listener);
		mntmSearch.addActionListener(listener);

		// ����ļ��˵�ʱ��ȷ���˵����Ƿ���á�
		mnEdit.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (ProjectTree.isBuilding) {
					mntmNewProject.setEnabled(false);
				} else {
					mntmNewProject.setEnabled(true);
				}
				if (treePanel.getManager().getSelectedNode() != null
						&& treePanel.getManager().getSelectedNode()
								.getNodeKind() == NodeKind.PROJECT_NODE) {
					mntmDelProject.setEnabled(true);
				} else {
					mntmDelProject.setEnabled(false);
				}

				if (srcTabbedPane.getTabCount() > 0) {
					mntmSearch.setEnabled(true);
					mntmClose.setEnabled(true);
					mntmCloseAll.setEnabled(true);
				} else {
					mntmSearch.setEnabled(false);
					mntmClose.setEnabled(false);
					mntmCloseAll.setEnabled(false);
				}
			}
		});

		JMenu mnMetric = new JMenu(MENU_METRIC_STR);
		menuBar.add(mnMetric);

		JMenuItem mntmCalMetric = new JMenuItem(MENU_CALCULATE_METRIC_STR);
		mntmCalMetric.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				MetricBrower metricBrower = new MetricBrower(treePanel.getManager(), "");
//				metricBrower.setVisible(true);
//				metricBrower.setResizable(false);
				
//				MetricFrame frame = new MetricFrame(treePanel.getManager(), srcTabbedPane);
//				frame.setVisible(true);
				
				MetricCalculationDlg dlg = new MetricCalculationDlg(treePanel.getManager(), srcTabbedPane);
				dlg.setVisible(true);
			}
		});
		mnMetric.add(mntmCalMetric);
		
		final JMenuItem mntmRecorderBrowser = new JMenuItem(MENU_RECORDER_BROWSER_STR);
		mntmRecorderBrowser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new RecorderTableFrame(srcTabbedPane);
				frame.setVisible(true);
			}
		});
		mnMetric.add(mntmRecorderBrowser);
		mnMetric.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(!Checker.isRecordFileExisting()) {
					mntmRecorderBrowser.setEnabled(false);
				} else {
					mntmRecorderBrowser.setEnabled(true);
				}
			}
		});

		JMenu mnSetting = new JMenu(" ���� ");
		menuBar.add(mnSetting);
		
		JMenuItem mntmModifyResultDir = new JMenuItem("�޸Ķ�������ļ����·��");
		mnSetting.add(mntmModifyResultDir);
		
		JMenuItem mntmResetting = new JMenuItem("�ָ���ʼ����");
		mnSetting.add(mntmResetting);
		
		ActionListener acListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File("res/.setting");
				SAXReader reader = new SAXReader();
				String command = e.getActionCommand();
				FileInputStream fis = null;
				if(command.equals("�ָ���ʼ����")) {
					try {
						fis = new FileInputStream(file);
						Document document = reader.read(fis);
						Element root = document.getRootElement();
						Iterator<?> it = root.elementIterator();
						while (it.hasNext()) {
							Element ele = (Element) it.next();
							if(ele.getName().equals("alert")) {
								Attribute attribute = ele.attribute("value");
								if (attribute.getStringValue()
										.equals("false")) {
									attribute.setValue("true");// �޸����Խڵ��ֵ
								}
							}
							if(ele.getName().equals("resultpath")) {
								Attribute attribute = ele.attribute("value");
								if (!attribute.getStringValue()
										.equals("result")) {
									attribute.setValue("result");// �޸����Խڵ��ֵ
								}
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
						e1.printStackTrace();
					} finally {
						try {
							fis.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				if(command.equals("�޸Ķ�������ļ����·��")) {
					ModifyResultDirDialog dlg = new ModifyResultDirDialog();
					dlg.setVisible(true);
				}
			}
		};
		
		mntmResetting.addActionListener(acListener);
		mntmModifyResultDir.addActionListener(acListener);
		
		JMenu mnHelp = new JMenu(" ���� ");
		menuBar.add(mnHelp);
		
		JMenuItem mntmMetricInfo = new JMenuItem(MENU_METRIC_INFO_BROWSER_STR);
		mnHelp.add(mntmMetricInfo);
		mntmMetricInfo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new MetricDescriptorDialog().setVisible(true);
			}
		});
	}

	private ProjectTreePanel createProjectTreePane(JTabbedPane tabPane, JScrollPane infoScroll) {
		return new ProjectTreePanel(tabPane, statusBar, infoScroll);
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}
}
