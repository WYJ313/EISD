package gui.softwareMeasurement.resultBrowser;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

public class ResultTableFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1561356112035113691L;
	ResultTable table;
	JScrollPane scrollPane;
	boolean isInit = true;

	public ResultTableFrame(ResultTable table) {
		this.table = table;
		initMenu();
		table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				ResultTableFrame.this.table.dispose();
				ResultTableFrame.this.table = null;
				
				if(ResultTable.tableCount == 0 && ResultTable.searcher != null) {
					ResultTable.searcher.setVisible(false);
				}
				System.gc();
			}

		});
		
		addWindowFocusListener(new WindowAdapter() {
			public void windowLostFocus(WindowEvent e) {
				Window window = e.getOppositeWindow();
				if(window != null && window.equals(ResultTable.searcher)) {
					if(ResultTable.searcher != null) {
						ResultTable.searcher.setSearchViewer(ResultTableFrame.this.table);
					}
				}
			}
		});
		
		// �������ݼ������ĺ��ʴ�С
		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		
		int colNum = table.getColumnCount();
		int tableWidth = (int) table.getMinimumSize().getWidth() + colNum * 50;
		int rowCount = table.getRowCount();
		int rowHeight = table.getRowHeight();
		
		int tableHeight = (rowCount+2) * rowHeight;
		if(tableHeight > 350) {
			tableHeight = 350;
		}
		
		if(screenWidth > tableWidth) {
			this.setSize(tableWidth + 30, 100 + tableHeight);
		} else {
			this.setSize(tableWidth/4 * 3, 100 + tableHeight);
		}
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		final JMenu mnDes = new JMenu(ResultTable.MENU_DES_STATISTICS_STR);

		JMenu mnMetricChart = new JMenu(ResultTable.MENU_METRIC_CHART_STR);
		mnDes.add(mnMetricChart);
		menuBar.add(mnDes);

		JMenu mnNumCha = new JMenu(ResultTable.MENU_NUMERICAL_CHARAC_STR);
		final JMenuItem mntmQ1 = new JMenuItem("���ķ�λ��");
		mnNumCha.add(mntmQ1);
		final JMenuItem mntmQ2 = new JMenuItem("��λ��");
		mnNumCha.add(mntmQ2);
		final JMenuItem mntmQ3 = new JMenuItem("���ķ�λ��");
		mnNumCha.add(mntmQ3);
		final JMenuItem mntmMean = new JMenuItem("��ֵ");
		mnNumCha.add(mntmMean);
		final JMenuItem mntmVari =  new JMenuItem(ResultTable.MENU_VARIANCE_STR);
		mnNumCha.add(mntmVari);
		final JMenuItem mntmSw = new JMenuItem("ƫ��");
		mnNumCha.add(mntmSw);
		final JMenuItem mntmKw = new JMenuItem("���");
		mnNumCha.add(mntmKw);
		final JMenuItem mntmCV = new JMenuItem("����ϵ��");
		mnNumCha.add(mntmCV);
		final JMenuItem mntmGini = new JMenuItem("����ָ��");
		mnNumCha.add(mntmGini);
		
		mnDes.add(mnNumCha);
		
		final JMenuItem mntmOpenFile = new JMenuItem(ResultTable.MENU_OPEN_SRC_STR);
		final JMenuItem mntmBarChart = new JMenuItem(
				ResultTable.MENU_BAR_CHART_STR);
		final JMenuItem mntmHistChart = new JMenuItem(
				ResultTable.MENU_HIST_CHART_STR);
		final JMenuItem mntmBoxChart = new JMenuItem(
				ResultTable.MENU_BOX_CHART_STR);
		final JMenuItem mntmLineChart = new JMenuItem(
				ResultTable.MENU_LINE_CHART_STR);

		mnMetricChart.add(mntmBarChart);
		mnMetricChart.add(mntmHistChart);
		mnMetricChart.add(mntmBoxChart);
		mnMetricChart.add(mntmLineChart);

		mnDes.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (table.getSelectedColumn() > 0
						|| table.getSelectedRowCount() > 0) {
					mntmBarChart.setEnabled(true);
					mntmHistChart.setEnabled(true);
					mntmBoxChart.setEnabled(true);
					mntmLineChart.setEnabled(true);
					mntmQ1.setEnabled(true);
					mntmQ2.setEnabled(true);
					mntmQ3.setEnabled(true);
					mntmMean.setEnabled(true);
					mntmVari.setEnabled(true);
					mntmSw.setEnabled(true);
					mntmKw.setEnabled(true);
					mntmCV.setEnabled(true);
					mntmGini.setEnabled(true);
				} else {
					mntmBarChart.setEnabled(false);
					mntmHistChart.setEnabled(false);
					mntmBoxChart.setEnabled(false);
					mntmLineChart.setEnabled(false);
					mntmQ1.setEnabled(false);
					mntmQ2.setEnabled(false);
					mntmQ3.setEnabled(false);
					mntmMean.setEnabled(false);
					mntmVari.setEnabled(false);
					mntmSw.setEnabled(false);
					mntmKw.setEnabled(false);
					mntmCV.setEnabled(false);
					mntmGini.setEnabled(false);
				}
			}
		});
		
		JMenu mnSearch = new JMenu(ResultTable.MENU_SEARCH_STR);
		JMenuItem mntmSearchContext = new JMenuItem("������Ԫ��");
		mnSearch.add(mntmSearchContext);
		menuBar.add(mnSearch);

		TableMenuListener listener = new TableMenuListener(table);
		mntmOpenFile.addActionListener(listener);
		mntmBarChart.addActionListener(listener);
		mntmHistChart.addActionListener(listener);
		mntmBoxChart.addActionListener(listener);
		mntmLineChart.addActionListener(listener);
		mntmSearchContext.addActionListener(listener);

		mntmQ1.addActionListener(listener);
		mntmQ2.addActionListener(listener);
		mntmQ3.addActionListener(listener);
		mntmVari.addActionListener(listener);
		mntmMean.addActionListener(listener);
		mntmSw.addActionListener(listener);
		mntmKw.addActionListener(listener);
		mntmCV.addActionListener(listener);
		mntmGini.addActionListener(listener);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if(table.getSelectedRowCount() == 1) {
					mnDes.add(mntmOpenFile);
				} else {
					mnDes.remove(mntmOpenFile);
				}
			}
		});
		
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		TableColumnModel columnModel = table.getColumnModel();
		int tableColumnWidthSum = 0;
		
		if(isInit) {// �е���С���ֻ��Ҫ��ʼ��һ�μ���
			isInit = false;
			FontMetrics fm = table.getFontMetrics(getFont());
			int currentMax = 0;
			for(int col = 0; col < table.getColumnCount(); col++) {
				String colName = table.getColumnName(col);
				currentMax = fm.stringWidth(colName) + getFont().getSize();
				for(int row = 0; row < table.getRowCount(); row++) {
					Object o = table.getValueAt(row, col);// �ҵ�ĳһ�е����������
					if(o instanceof String) {
						int width = fm.stringWidth((String)o) + 5;
						if(currentMax < width) {
							currentMax = width;
						}
					}
				}
				// ��������ؿ�ȵ���һ����Ϊ�����е���С���
				columnModel.getColumn(col).setMinWidth(currentMax);
			}
		}
		
		for(int col = 0; col < table.getColumnCount(); col++) {
			tableColumnWidthSum += columnModel.getColumn(col).getMinWidth();
		}
		int sHeight = getContentPane().getHeight();
		int sWidth = getContentPane().getWidth();
		scrollPane.setSize(sWidth, sHeight);
		int deltaWidth = (sWidth - tableColumnWidthSum)/table.getColumnCount();
		if(tableColumnWidthSum > sWidth) {// �����е��ܿ�Ȼ�����scrollPane�Ŀ�ȵĻ����ͽ�ʣ��
			// �Ŀ�ȷֲ���ÿһ��
			deltaWidth = 0;
		}
		for(int col = 0; col < table.getColumnCount(); col++) {
			int minWidth = columnModel.getColumn(col).getMinWidth();
			columnModel.getColumn(col).setPreferredWidth(minWidth + deltaWidth);
		}
		
	}

}
