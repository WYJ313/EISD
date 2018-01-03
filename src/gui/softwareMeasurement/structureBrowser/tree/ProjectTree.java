package gui.softwareMeasurement.structureBrowser.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import gui.softwareMeasurement.mainFrame.ExpandableTitle;
import gui.softwareMeasurement.mainFrame.SrcTabbedPane;
import gui.softwareMeasurement.mainFrame.StatusBar;
import gui.softwareMeasurement.structureBrowser.ProjectTreeManager;
import softwareMeasurement.measure.SoftwareMeasureIdentifier;

public class ProjectTree extends JTree implements TitleObserver{
	private static final long serialVersionUID = -5192173550723546223L;
	ProjectTreeManager manager;
	StatusBar statusBar;

	/**չʾ������ģ������Ϣ**/
	private JTable mTable;
	ExpandableTitle title = new ExpandableTitle();
	JScrollPane metricInfo;
	
	private ProjectTreeNode lastNode = null;
	public static boolean canFresh = true;
	public static boolean isBuilding = false;
	JPanel pane = null;
	

	private HashSet<TreePath> flagSet = new HashSet<TreePath>();

	public ProjectTree(ProjectTreeManager manager) {
		super();
		this.manager = manager;
		init();
	}

	public ProjectTree(TreeNode root, ProjectTreeManager manager) {
		super(root);
		this.manager = manager;
		init();
	}

	public ProjectTree(TreeModel model, ProjectTreeManager manager) {
		super(model);
		this.manager = manager;
		init();
	}

	private void init() {
		setToggleClickCount(100);
		setRootVisible(false);
		setShowsRootHandles(true);
		title.setExpandedObserver(this);
		
		mTable = new JTable(){
			private static final long serialVersionUID = -5370912935032507061L;

			public String getToolTipText(MouseEvent event){
				Point point=event.getPoint();
				int column = columnAtPoint(point);
				int row = rowAtPoint(point);
				Object value = getValueAt(row, column);
				if(value != null){
					String metricId = (String)value ;
					String description = SoftwareMeasureIdentifier.getDescriptionOfMeasure(metricId);
					return description;
				}
				return null;
			}
		};
		mTable.setShowGrid(false);
		
		addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object o = getLastSelectedPathComponent();
				if(o == null) {// ɾ��һ����Ŀ�ڵ�֮�����ֱ�ѡ�еĽڵ�Ϊ�յ������
						// Ҳ����ͬʱ�Դ�����Ϊû����Ŀ������Ĵ���
					pane.setVisible(false);
				}
				if(o instanceof ProjectTreeNode) {
					final ProjectTreeNode n = (ProjectTreeNode)o;
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							getMetricData(n);
							if(pane == null) {
								initInfoPanel();
								pane.setVisible(true);
								metricInfo.setViewportView(pane);
							} else {
								pane.setVisible(true);
								pane.repaint();
							}
						}
					});
				}
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F5 && canFresh) {
					manager.updateTree();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			int x;
			int y;

			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					setSelectionPath(path);
				}
			}

			public void mouseClicked(MouseEvent e) {
				SrcTabbedPane stabbedPane = (SrcTabbedPane) manager
						.getTextTabPane();
				TreePath path = getPathForLocation(x, y);
				if (path != null) {
					Object o = path.getLastPathComponent();
					if (o instanceof ProjectTreeNode) {
						ProjectTreeNode node = (ProjectTreeNode) o;
						lastNode = node;
						if (e.getClickCount() == 2) {
							if (node.NODE_KIND == NodeKind.CLASS_NODE) {
								final ClassNode classNode = (ClassNode) node;
								stabbedPane.showSrc(classNode.filePath);
								System.out.println("�򿪵��ļ���·����" + classNode.filePath);
							} else {
								if (isExpanded(path)) {
									collapsePath(path);
								} else {
									expandPath(path);
								}
								if (node.NODE_KIND == NodeKind.METHOD_NODE
										|| node.NODE_KIND == NodeKind.FIELD_NODE) {
									String locationStr = node.getLocation();
									String row_col = locationStr.substring(0, locationStr.indexOf('@'));
									String rowStr = row_col.substring(0, row_col.indexOf(':'));
									int row = Integer.valueOf(rowStr);
									ClassNode classNode = (ClassNode) node.getParent();
									stabbedPane.showSelectedMethod(row,	node.getSimpleName(), classNode.filePath);
								}
							}
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					setSelectionPath(path);
				}
				ProjectTreePopupMenu popup = new ProjectTreePopupMenu(manager);
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				} else {
					popup.setVisible(false);
				}
			}
			
		});
	}
	/**
	 * ���ݵ�ǰ�ṹ������б�ѡ�еĽڵ������¶����б���Ϣ�����Ҹ���ExpandableTitle�е�
	 * Label,ʹ֮��Ӧ��ȷ�Ľڵ㡣
	 * @param node
	 */
	private void getMetricData(final ProjectTreeNode node) {
		if(node != lastNode && node != null) {
			if(title != null) {
				title.setText(node.getSimpleName() + "�Ķ�����Ϣ(�������ڲ˵��м�����꾡�Ķ�����Ϣ):");
			}
			
			String[] ids = node.getSizeMetricIdentifier();
			String[] values = node.getSizeMetricValue();
			int rowCount = ids.length;
			
			DefaultTableModel model = new DefaultTableModel(ids.length, 2){
				private static final long serialVersionUID = -3675317219397210581L;

				public boolean isCellEditable(int row, int col) {
					return false;
				}
			};
			for(int i = 0; i < rowCount; i++) {
				model.setValueAt(ids[i], i, 0);
				model.setValueAt(values[i], i, 1);
			}

			mTable.setModel(model);
//			TableColumn firstColumn = mTable.getColumnModel().getColumn(0);
//			firstColumn.setPreferredWidth(mTable.getWidth()/3);
//			firstColumn.setMinWidth(mTable.getWidth()/5);
//			firstColumn.setMaxWidth(mTable.getWidth()/2);
		}
	}
	
	/**
	 * ��ExpandableTitle��չʾ������Table�ϲ�Ϊһ��JPanel
	 * @return JPanel�����а�����һ����չ�����У�չ��֮����ʾһ���б�
	 * ����б��а����������Ĺ�ģ������Ϣ
	 */
	private void initInfoPanel() {
		pane = new JPanel();
		pane.setLayout(null);
		int pHeight = 0, pWidth = 0;
		pWidth = metricInfo.getWidth();

		FontMetrics fm = getFontMetrics(getFont());
		
		int titleHeight = fm.getHeight();
		pHeight = titleHeight *(mTable.getRowCount()+1) + titleHeight + 8;
		pane.setPreferredSize(new Dimension(pWidth, pHeight));
		pane.setBackground(Color.white);
		// ExpandableTitle�Ŀ������������ᵼ�µ��е�JLabel����ѹ���ɼ������֮��
		// ������������text������п����ǿ�����ù�С(��������в���30���߸�СЩ��
		// ���ܳ��ֿ�����label�е�����)
		title.setBounds(0, 0, pWidth, titleHeight + 6);
		// ��Table�ĸ߶Ȳ���ʱ��Ҳ�ᵼ�²�����Ϣ�޷�����(�磺�߶������в�+1���ڱ���������ʾ����֮��10�����ݣ���ʵ����12��)
		mTable.setBounds(20, title.getHeight(), metricInfo.getWidth(), titleHeight * (mTable.getRowCount()+1));
		
		if(!title.isExpanded()) {
			mTable.setVisible(false);
		} else {
			mTable.setVisible(true);
		}
		pane.add(title);
		pane.add(mTable);
	}

	public void deleteNode(ProjectTreeNode node) {
		node.removeFromParent();// ɾ����Ŀ
		String projectId = node.getSimpleName();
		File file = new File(".info/" + projectId);
		if (file.exists()) {
			file.delete();
		}
	}
	

	/**
	 * ���ݴ���Ľڵ��ҵ��ڵ����ڵİ汾�ڵ�
	 * 
	 * @param node
	 * @return
	 */
/*	private ProjectTreeNode findVersionNode(ProjectTreeNode node) {
		// �ҵ��˸����֮�϶�û�ҵ���˵��û�д���汾��Ϣ
		if (node == null)
			return null;
		// �ҵ�����Ŀ�ڵ㶼û�ҵ��汾�ڵ㣬Ҳ˵��û�д���汾��Ϣ
		if (node.NODE_KIND == NodeKind.PROJECT_NODE)
			return null;

		if (node.NODE_KIND == NodeKind.VERSION_NODE) {
			return node;
		} else {
			return findVersionNode((ProjectTreeNode) node.getParent());
		}
	}
*/
	/**
	 * ���ݴ���Ľڵ��ҵ������ڵ���Ŀ�ڵ�
	 * 
	 * @param node
	 * @return
	 */
/*	private ProjectTreeNode findProjectNode(ProjectTreeNode node) {
		if (node.NODE_KIND == NodeKind.PROJECT_NODE) {
			return node;
		} else {
			return findProjectNode((ProjectTreeNode) node.getParent());
		}
	}
*/
	/**
	 * ����xml�ļ��е�������Ϣ
	 */
	public void load() {
		SAXReader reader = new SAXReader();
		Document document;
		File infoDir = new File(".info");
		File[] infoFiles = infoDir.listFiles();
		ArrayList<Element> projectEles = new ArrayList<Element>();
		FileInputStream fis = null;
		if (infoFiles != null) {
			for (File file : infoFiles) {
				try {
					// ��Ҫ��read(file)����ȡdocument,�����쳣�׳�ʱ����ס�ļ������ļ��޷�ɾ��
					fis = new FileInputStream(file);
					document = reader.read(fis);
					Element root = document.getRootElement();
					Element projectEle = root.element("project");// ��Ŀ�ڵ���ر�ǩ
					projectEles.add(projectEle);
				} catch (DocumentException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "��Ŀ" + file.getName()
							+ "����Ϣ�𻵣����ᶪʧ��Ŀ��Ϣ��");
					try {
						// �ر�����ɾ���ļ�
						fis.close();
						file.delete();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		if (canFresh) {
			LoadTreeWorker worker = new LoadTreeWorker(this, projectEles);
			worker.execute();// ������Ŀ
		}
	}

	/**
	 * ��ProjectTree�Ľڵ���Ϣ��xml�ļ���������
	 */
	public synchronized void save(String projectId) {
		try {
			File proFile = new File(".info/" + projectId);
			if (!proFile.isFile()) {
				proFile.createNewFile();
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf8");

			FileOutputStream fos = new FileOutputStream(proFile);
			XMLWriter xmlWriter = new XMLWriter(fos, format);

			ProjectTreeNode proNode = manager
					.getProjectNodeByProjectName(projectId);
			Document document = DocumentHelper.createDocument();
			Element proRoot = document.addElement("root");
			createProjectEle(proNode, proRoot);

			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���Ľڵ���Ϣ����Ϊxml��ǩ
	 * 
	 * @param projectNodes
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private void createProjectEle(ProjectTreeNode projectNode, Element root) {
		boolean hasVersion = false;
		Element project = root.addElement("project");
		project.addAttribute("label", projectNode.label);
		project.addAttribute("metrics", projectNode.sizeMetrics);
		if (projectNode.children() != null
				&& projectNode.children().hasMoreElements()) {
			// �����Ŀ�ڵ������а汾�ڵ㣬���ñ�־
			ProjectTreeNode n = (ProjectTreeNode) projectNode.children()
					.nextElement();
			if (n.NODE_KIND == NodeKind.VERSION_NODE) {
				hasVersion = true;
			}
		}
		if (hasVersion)// �а汾�ڵ㣬����
			createVersionEle(projectNode.children(), project);
		else
			// û�а汾�ڵ㣬��ֱ�Ӽ�����Ŀ�µ�����
			createPackageEle(projectNode.children(), project);
	}

	/**
	 * �����İ汾�ڵ����Ϊxml��ǩ
	 * 
	 * @param versionNodes
	 * @param projectEle
	 */
	@SuppressWarnings("unchecked")
	private void createVersionEle(Enumeration<ProjectTreeNode> versionNodes,
			Element projectEle) {
		while (versionNodes.hasMoreElements()) {
			Element version = projectEle.addElement("version");
			VersionNode node = (VersionNode) versionNodes.nextElement();
			version.addAttribute("label", node.label);
			version.addAttribute("metrics", node.sizeMetrics);
			createPackageEle(node.children(), version);
		}
	}

	/**
	 * �����İ��ڵ����Ϊxml��ǩ
	 * 
	 * @param packageNodes
	 * @param versionEle
	 */
	@SuppressWarnings("unchecked")
	private void createPackageEle(Enumeration<ProjectTreeNode> packageNodes,
			Element versionEle) {
		while (packageNodes.hasMoreElements()) {
			Element packageEle = versionEle.addElement("package");
			PackageNode node = (PackageNode) packageNodes.nextElement();
			packageEle.addAttribute("label", node.label);
			packageEle.addAttribute("metrics", node.sizeMetrics);
			createClassEle(node.children(), packageEle);
		}
	}

	/**
	 * ��������ڵ����Ϊxml��ǩ
	 * 
	 * @param classNodes
	 * @param packageEle
	 */
	@SuppressWarnings("unchecked")
	private void createClassEle(Enumeration<ProjectTreeNode> classNodes,
			Element packageEle) {
		while (classNodes.hasMoreElements()) {
			Element classEle = packageEle.addElement("class");
			ClassNode node = (ClassNode) classNodes.nextElement();
			classEle.addAttribute("label", node.label);
			classEle.addAttribute("path", node.filePath);
			classEle.addAttribute("metrics", node.sizeMetrics);
			createFieldAndMethodEle(node.children(), classEle);
		}
	}

	/**
	 * ���������Խڵ��Լ������ڵ����Ϊxml��ǩ
	 * 
	 * @param fieldAndMethoNodes
	 * @param classEle
	 */
	private void createFieldAndMethodEle(
			Enumeration<ProjectTreeNode> fieldAndMethoNodes, Element classEle) {
		while (fieldAndMethoNodes.hasMoreElements()) {
			ProjectTreeNode node = fieldAndMethoNodes.nextElement();
			if (node.NODE_KIND == NodeKind.FIELD_NODE) {
				Element fieldEle = classEle.addElement("field");
				FieldNode fieldNode = (FieldNode) node;
				fieldEle.addAttribute("label", fieldNode.label);
				fieldEle.addAttribute("type", fieldNode.type);
				fieldEle.addAttribute("metrics", fieldNode.sizeMetrics);
			}
			if (node.NODE_KIND == NodeKind.METHOD_NODE) {
				Element methodEle = classEle.addElement("method");
				MethodNode methodNode = (MethodNode) node;
				methodEle.addAttribute("label", methodNode.label);
				if (methodNode.params != null) {
					for (String paramStr : methodNode.params) {
						Element param = methodEle.addElement("param");
						param.addAttribute("type", paramStr);
					}
				}
				methodEle.addAttribute("metrics", node.sizeMetrics);
			}
		}
	}

	public void setStatusBar(StatusBar bar) {
		this.statusBar = bar;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}
	
	public JTable getMetricTable() {
		return mTable;
	}

	/**
	 * ExpandableTitleչ��������ᴥ���������
	 */
	@Override
	public void update(boolean isExpanded) {
		if(isExpanded) {
			mTable.setVisible(true);
		} else {
			mTable.setVisible(false);
		}
	}
	
	public void findAndSelectNodeByNodeName(String name) {
		Object root = getModel().getRoot();
		TreePath treePath = new TreePath(root);
		treePath = findInPath(treePath, name);
		if (treePath != null) {
			if(!flagSet.contains(treePath)) {
				flagSet.add(treePath);
				setSelectionPath(treePath);
				scrollPathToVisible(treePath);
			}
		} else {
			if(flagSet.isEmpty()) {
				JOptionPane.showMessageDialog(null, "δ������������ݣ�");
			} else {
				flagSet.clear();
			}
		}
	}
	
	private TreePath findInPath(TreePath treePath, String text) {
		Object object = treePath.getLastPathComponent();
		if (object == null) {
			return null;
		}

		String str = text.toLowerCase();
		String value = object.toString().toLowerCase();
		if(object instanceof ProjectTreeNode) {
			ProjectTreeNode node = (ProjectTreeNode)object;
			int index = -1;
			if(node.getNodeKind() == NodeKind.FIELD_NODE) {
				// �ֶε�չʾ�ַ���������   ������:����  �ĸ�ʽչʾ
				// ����ʱ������������
				index = value.indexOf(':');
			}
			if(node.getNodeKind() == NodeKind.METHOD_NODE) {
				// ������չʾ�ַ���������   ������(���������б�)  �ĸ�ʽչʾ
				// ����ʱ�����������
				index = value.indexOf('(');
			}
			if(index != -1) {
				value = value.substring(0, index);
			}
		}
		if (value.contains(str) && !flagSet.contains(treePath)) {
			return treePath;
		} else {
			TreeModel model = getModel();
			int n = model.getChildCount(object);
			for (int i = 0; i < n; i++) {
				Object child = model.getChild(object, i);
				TreePath path = treePath.pathByAddingChild(child);
				path = findInPath(path, str);
				if (path != null && !flagSet.contains(path)) {
					return path;
				}
			}
			return null;
		}
	}

	public void setMetricArea(JScrollPane infoScroll) {
		metricInfo = infoScroll;
		metricInfo.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				int pHeight = 0, pWidth = 0;
				pWidth = metricInfo.getWidth();
				FontMetrics fm = getFontMetrics(getFont());
				int titleHeight = fm.getHeight();
				pHeight = titleHeight *(mTable.getRowCount()+1) + titleHeight + 8;
				if(pane != null) {
					pane.setPreferredSize(new Dimension(pWidth, pHeight));
				}
				// ExpandableTitle�Ŀ������������ᵼ�µ��е�JLabel����ѹ���ɼ������֮��
				// ������������text������п����ǿ�����ù�С(��������в���30���߸�СЩ��
				// ���ܳ��ֿ�����label�е�����)
				if(title != null) {
					title.setBounds(0, 0, pWidth, titleHeight + 6);
					if(mTable != null) {
						// ��Table�ĸ߶Ȳ���ʱ��Ҳ�ᵼ�²�����Ϣ�޷�����(�磺�߶������в�+1���ڱ���������ʾ����֮��10�����ݣ���ʵ����12��)
						mTable.setBounds(20, title.getHeight(), metricInfo.getWidth(), titleHeight * (mTable.getRowCount()+1));
					}
				}
			}
			
		});
	}
	
	public JScrollPane getMetricArea() {
		return metricInfo;
	}

}
