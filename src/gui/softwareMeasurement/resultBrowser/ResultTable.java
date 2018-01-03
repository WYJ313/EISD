package gui.softwareMeasurement.resultBrowser;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.BitSet;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import gui.softwareMeasurement.mainFrame.SrcTabbedPane;

public class ResultTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4850499774553128668L;
	private JTabbedPane tabbedPane;
	static Searcher searcher;
	static int tableCount = 0;

	final static String MENU_DES_STATISTICS_STR = "������ͳ��";
	
	final static String MENU_METRIC_CHART_STR = "ͳ��ͼ��";
	final static String MENU_BAR_CHART_STR = "����ͼ";
	final static String MENU_HIST_CHART_STR = "ֱ��ͼ";
	final static String MENU_BOX_CHART_STR = "��ͼ";
	final static String MENU_LINE_CHART_STR = "����ͼ";

	final static String MENU_NUMERICAL_CHARAC_STR = "��ֵ����";
	
	final static String MENU_VARIANCE_STR = "����"; 
	
	final static String MENU_OPEN_SRC_STR = "�鿴Դ����";
	
	final static String MENU_SEARCH_STR = "����";

	public ResultTable(TableModel model) {
		super(model);
		tableCount ++;
		ResultTableCellRenderer renderer = new ResultTableCellRenderer();
		setDefaultRenderer(Object.class, renderer);
		
		addMouseListener(renderer);
		addMouseMotionListener(renderer);

		int colNum = model.getColumnCount();
		for(int i = 0; i < colNum; i++) {
			TableColumn columnObj = getColumnModel().getColumn(i);
			columnObj.setMinWidth(80);
		}
		
		setColumnSelectionAllowed(true);
		
		final JTableHeader header = getTableHeader();
		header.setResizingAllowed(true);

		header.setReorderingAllowed(false);
		header.addMouseListener(new MouseAdapter() {
			/** lastColumn��������¼����shift֮ǰ���������һ�� **/
			int lastColumn = 0;
			boolean shiftMask = false;
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int column = header.columnAtPoint(e.getPoint());
					if (column > 0) {
						if (e.isShiftDown()) {
							clearSelection();
							addColumnSelectionInterval(lastColumn, column);
							addRowSelectionInterval(0, getRowCount() - 1);
							shiftMask = true;
						} else {
							shiftMask = false;
							if (e.isControlDown()) {// ������ctrl��
								if(!isColumnSelected(column)) {// ��ǰ��û��ѡ�У���ȫѡ��ǰ����
									addColumnSelectionInterval(column, column);
									addRowSelectionInterval(0, getRowCount() - 1);
								} else {// ��ǰ�������ѡ�У����Ƴ���ǰ��
									removeColumnSelectionInterval(column, column);
								}
							} else {
								clearSelection();
								if(!isColumnSelected(column)) {// ��ǰ��û��ѡ�У���ȫѡ��ǰ����
									addColumnSelectionInterval(column, column);
									addRowSelectionInterval(0, getRowCount() - 1);
								}
							}
						}
						if(!shiftMask)// û�а���shift��ʱ�򣬸ı�lastColumn��ֵ
							lastColumn = column;
					}
				}
			}
		});

		MouseAdapter adapter = createMouseAdapter();
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
		
	}
	
	private MouseAdapter createMouseAdapter() {
		MouseAdapter adapter = new MouseAdapter() {
			int originRow = -1;// ������¼��������ϵ���ק������ʱ���ڵ���һ��
			/** shiftOriginRow ������¼shift���µ�ʱ��������������һ�� **/
			int shiftOriginRow = -1;
			boolean shiftMask = false;
			boolean isButton1Down;
			/** ����ÿ��ѡ�е����е�ĳһ��ʱ,isRowSelected(row)��Ȼ�õ�����true,���ʹ�����BitSet����¼֮ǰ��ѡ�е��� **/
			BitSet lastState = new BitSet();

			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isButton1Down = true;
				}
				int col = columnAtPoint(e.getPoint());
				originRow = rowAtPoint(e.getPoint());
				if(isButton1Down && col == 0) {// �Ҽ��ڵ����а���
					if (e.isShiftDown()) {// ������shift,��ѡ������֮���������
						clearSelection();
						lastState.clear();
						if(shiftOriginRow > -1) {
							int endRow = rowAtPoint(e.getPoint());
							addRowSelectionInterval(shiftOriginRow, endRow);
							addColumnSelectionInterval(0, getColumnCount() - 1);
							// ��ѡ�е��м�¼��BitSet��
							int min = shiftOriginRow > endRow ? endRow : shiftOriginRow;
							int max = shiftOriginRow > endRow ? shiftOriginRow : endRow;
							for(int i = min; i <= max; i++) {
								lastState.set(i);
							}
						}
						shiftMask = true;
					} else {
						shiftMask = false;
						if(e.isControlDown()) {
							int row = rowAtPoint(e.getPoint());
							if(lastState.get(row)) {// ����ctrlʱ�����֮ǰ�Ѿ�ѡ��������У���ɾ��
													// ���Ƴ�������BitSet�ж�Ӧ�ı�־
								removeRowSelectionInterval(row, row);
								lastState.clear(row);
							} else {
								addRowSelectionInterval(row, row);
								addColumnSelectionInterval(0, getColumnCount() - 1);
								lastState.set(row);
							}
							
						} else {// ���shiftû�а��£���ctrlҲû�а���
							selectRow(e);
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {// ��Ӧ�Ҽ��������˵�
					// ��ѡ�е��к���
					int[] selectedRows = getSelectedRows();
					int[] selectedCols = getSelectedColumns();
					if (selectedRows != null && selectedRows.length > 0
							&& selectedCols != null && selectedCols.length > 0) {
						showMenu(e);
					}
				}
				if(!shiftMask) {
					shiftOriginRow = rowAtPoint(e.getPoint());
				}
			}

			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int[] rows = getSelectedRows();
					ResultFileParser parser = (ResultFileParser) getModel();
					for (int row : rows) {
						String filePath = parser.getFilePath(row);
						File file = new File(filePath);
						if (file.isFile()) {
							showSrc(filePath);
						} else if(file.isDirectory()){
							JOptionPane.showMessageDialog(null, file.getName() + 
									"��һ���ļ��У�", "��ʾ", JOptionPane.YES_OPTION);
						} else {
							if(getColumnName(0).equals("��")) {
								JOptionPane.showMessageDialog(null, getValueAt(row, 0)+
										"���ļ��У��޷��򿪣�");
							} else {
								JOptionPane.showMessageDialog(null, "�ļ�" + file.getName() + 
										"�����ڣ�", "��ʾ", JOptionPane.YES_OPTION);
							}
						}
					}
				}
			}
			
			public void mouseDragged(MouseEvent e) {
				if(isButton1Down) {
					selectRow(e);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					isButton1Down = false;
				}
			}
			
			private void selectRow(MouseEvent e) {
				int col = columnAtPoint(e.getPoint());
				if (col == 0) {// ������ǵ�����
					if(!e.isControlDown()) {
						clearSelection();
						lastState.clear();
					}
					if (originRow > -1) {
						int endRow = rowAtPoint(e.getPoint());
						if(endRow > -1 && endRow <= getRowCount() - 1) {
							addRowSelectionInterval(originRow, endRow);
							addColumnSelectionInterval(0, getColumnCount() - 1);
							int min = originRow > endRow ? endRow : originRow;
							int max = originRow > endRow ? originRow : endRow;
							for(int i = min; i <= max; i++) {
								lastState.set(i);
							}
						}
					} 
				}
			}
		};
		return adapter;
	}

	private void showMenu(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenu mnDes = new JMenu(MENU_DES_STATISTICS_STR);
		JMenu mnMetricChart = new JMenu(MENU_METRIC_CHART_STR);
		JMenuItem mntmBarChart = new JMenuItem(MENU_BAR_CHART_STR);
		JMenuItem mntmHistChart = new JMenuItem(MENU_HIST_CHART_STR);
		JMenuItem mntmBoxChart = new JMenuItem(MENU_BOX_CHART_STR);
		JMenuItem mntmLineChart = new JMenuItem(MENU_LINE_CHART_STR);

		mnDes.add(mnMetricChart);
		mnMetricChart.add(mntmBarChart);
		mnMetricChart.add(mntmHistChart);
		mnMetricChart.add(mntmBoxChart);
		mnMetricChart.add(mntmLineChart);

		JMenu mnNumCha = new JMenu(MENU_NUMERICAL_CHARAC_STR);
		JMenuItem mntmQ1 = new JMenuItem("���ķ�λ��");
		mnNumCha.add(mntmQ1);
		JMenuItem mntmQ2 = new JMenuItem("��λ��");
		mnNumCha.add(mntmQ2);
		JMenuItem mntmQ3 = new JMenuItem("���ķ�λ��");
		mnNumCha.add(mntmQ3);
		JMenuItem mntmMean = new JMenuItem("��ֵ");
		mnNumCha.add(mntmMean);
		JMenuItem mntmVari =  new JMenuItem(ResultTable.MENU_VARIANCE_STR);
		mnNumCha.add(mntmVari);
		JMenuItem mntmSw = new JMenuItem("ƫ��");
		mnNumCha.add(mntmSw);
		JMenuItem mntmKw = new JMenuItem("���");
		mnNumCha.add(mntmKw);
		JMenuItem mntmCV = new JMenuItem("����ϵ��");
		mnNumCha.add(mntmCV);
		JMenuItem mntmGini = new JMenuItem("����ָ��");
		mnNumCha.add(mntmGini);
		
		JMenuItem mntmOpenFile = new JMenuItem(MENU_OPEN_SRC_STR);
		
		mnDes.add(mnNumCha);
		if(getSelectedRowCount() == 1) {
			mnDes.add(mntmOpenFile);
		}
		
		
//		ResultFileParser parser = (ResultFileParser)getModel();
//		int[] rows = getSelectedRows();
//		for (int row : rows) {
//			String filePath = parser.getFilePath(row);
//			File file = new File(filePath);
//			if (!file.isFile()) {
//				mntmOpenFile.setEnabled(false);
//				break;
//			}
//		}
		menu.add(mnDes);

		ActionListener listener = new TableMenuListener(this);

		mntmBarChart.addActionListener(listener);
		mntmHistChart.addActionListener(listener);
		mntmBoxChart.addActionListener(listener);
		mntmOpenFile.addActionListener(listener);
		mntmLineChart.addActionListener(listener);
		
		mntmQ1.addActionListener(listener);
		mntmQ2.addActionListener(listener);
		mntmQ3.addActionListener(listener);
		mntmVari.addActionListener(listener);
		mntmMean.addActionListener(listener);
		mntmSw.addActionListener(listener);
		mntmKw.addActionListener(listener);
		mntmCV.addActionListener(listener);
		mntmGini.addActionListener(listener);

		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	void showSrc(String filePath) {
		((SrcTabbedPane) tabbedPane).showSrc(filePath);
	}

	public String getToolTipText(MouseEvent e) {
		Point point = e.getPoint();
		int column = columnAtPoint(point);
		int row = rowAtPoint(point);
		String s = (String) getValueAt(row, column);
		return s;
	}

	public void setTextArea(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

//	public boolean isCellSelected(int row, int col) {
//		if (col == 0)
//			return false;
//		return super.isCellSelected(row, col);
//	}

	public void dispose() {
		tableCount--;
		for(MouseListener l : getTableHeader().getMouseListeners()) {
			getTableHeader().removeMouseListener(l);
			l = null;
		}
		for(MouseListener l : getMouseListeners()) {
			removeMouseListener(l);
			l = null;
		}
	}
}
