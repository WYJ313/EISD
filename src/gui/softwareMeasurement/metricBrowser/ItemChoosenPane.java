package gui.softwareMeasurement.metricBrowser;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import gui.softwareMeasurement.structureBrowser.VersionTableRenderer;
/**
 * ѡ���б�,ʹ��Item��Ϊ�ڲ�����ģ��
 * @author Wu zhangsheng
 */
public class ItemChoosenPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8980057904370779941L;
	private JTable table;
	JScrollPane pane;
	int[] columnWidth;
	SelectionLabel selectAll;
	SelectionLabel inverse;
	List<Item> items;
	String[] columnNames;
	BitSet flagBit;
	boolean showAllSelectedBtn;

	public ItemChoosenPane(List<Item> items, String[] columnNames, boolean showAllSelectedBtn) {
		setLayout(null);
		this.items = items;
		this.columnNames = columnNames;
		this.showAllSelectedBtn = showAllSelectedBtn;
		this.columnWidth = new int[columnNames.length - 1];
		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);   
		init(items);
		
		pane = new JScrollPane(table);

//		final JScrollBar hBar = pane.getHorizontalScrollBar();
//		if(hBar != null) {
//			hBar.addAdjustmentListener(new AdjustmentListener() {
//				
//				@Override
//				public void adjustmentValueChanged(AdjustmentEvent e) {
//					System.out.println("ItemChoosenPane output->"+e.getValue());
//				}
//			});
//		}
		
		add(pane);
		
		flagBit = new BitSet(table.getRowCount());
		
		MouseAdapter adapter = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				table.setColumnSelectionAllowed(false);
				int[] selectedRows = table.getSelectedRows();
				for(int row : selectedRows) {
					table.setValueAt(!flagBit.get(row) , row, 0);
				}
			}
			
			public void mouseDragged(MouseEvent e) {
				for(int row = 0; row < table.getRowCount(); row++) {
					if(table.isRowSelected(row)) {// ����϶�ѡ�е���Ϊ�෴
						table.setValueAt(!flagBit.get(row), row, 0);
					} else {// û��ѡ�е�����ԭ����ֵ
						table.setValueAt(flagBit.get(row), row, 0);
					}
					
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				for(int i = 0; i < table.getRowCount(); i++) {
					if(table.getValueAt(i, 0) != null) {
						if((Boolean)table.getValueAt(i, 0)) {
							flagBit.set(i);
						} else {
							flagBit.clear(i);
						}
					}
				}
			}
			
		};
		table.addMouseListener(adapter);
		table.addMouseMotionListener(adapter);
		if(showAllSelectedBtn) {
			selectAll = new SelectionLabel("ȫѡ");
			inverse = new SelectionLabel("��ѡ");
			add(selectAll);
			add(inverse);
		}
	}

	public void paint(Graphics g){
		super.paint(g);
		if(showAllSelectedBtn) {
			pane.setSize(getWidth(), getHeight() - 30);
		} else {
			pane.setSize(getWidth(), getHeight());
		}
		
		for(int i = 0; i < columnWidth.length; i++) {
			TableColumnModel colModel = table.getColumnModel();
			if(i+1 < colModel.getColumnCount()) {
				TableColumn tableColumn = colModel.getColumn(i+1);
				tableColumn.setMinWidth(columnWidth[i] + 5);
			}
		}
		
		// ������ÿһ�е���С���֮�󣬼�����Щ���֮�ͣ������Ƿ����table�Ŀ��
		// ���������ߵĴ�С�����µ���ÿһ�еĿ�ȡ�
		int sumWidth = 22;
		for(int i = 0; i < columnWidth.length; i++) {
			sumWidth += columnWidth[i] + 5;
		}
		int deltaWidth = getWidth() - sumWidth;
		if(sumWidth > getWidth()) {
			deltaWidth = 0;
		}
		for(int i = 0; i < columnWidth.length; i++) {
			TableColumnModel colModel = table.getColumnModel();
			if(i+1 < colModel.getColumnCount()) {
				TableColumn tableColumn = colModel.getColumn(i+1);
				tableColumn.setPreferredWidth(columnWidth[i]+5+deltaWidth/columnWidth.length);
			}
		}
		if(showAllSelectedBtn) {
			int fontSize = selectAll.getFont().getSize();
			selectAll.setBounds(pane.getX(),
					pane.getY() + pane.getHeight() + 5, fontSize * 4,
					fontSize + 2);
			inverse.setBounds(selectAll.getX() + fontSize * 4 + 5,
					selectAll.getY(), fontSize * 4, fontSize + 2);
		}
		
	}
	
	private void init(List<Item> items) {
		if(items != null && items.size() > 0) {
			DefaultTableModel model = new DefaultTableModel() {
				private static final long serialVersionUID = -8744359760032397339L;

				public boolean isCellEditable(int row, int column) {
					if (column == 0)
						return true;
					return false;
				}
			};
			Boolean[] data = new Boolean[items.size()];
			String[][] stringData = new String[columnNames.length-1][];
			for(int i = 0; i < columnNames.length - 1; i++) {
				stringData[i] = new String[items.size()];
			}
			FontMetrics fm = table.getFontMetrics(getFont());
			for(int index = 0; index < stringData.length; index ++) {
				for (int i = 0; i < items.size(); i++) {
					Item n = items.get(i);// ��i�еĽڵ�n
					data[i] = false;
					// index - �У� i - ��,i�е�index����Ҫչʾ���ַ���
					stringData[index][i] = n.getItemString(index);
					
					int colWidth = fm.stringWidth(stringData[index][i]);
					if(columnWidth[index] < colWidth) {
						columnWidth[index] = colWidth;
					}
				}
			}
			
			model.addColumn(columnNames[0], data);
			for(int i = 1; i < columnNames.length; i++) {
				if(hasData(stringData[i-1])) {
					model.addColumn(columnNames[i], stringData[i-1]);
				}
			}
			table.setModel(model);
			
			TableColumn checkBoxColumn = table.getColumnModel().getColumn(0);
			checkBoxColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
			checkBoxColumn.setCellRenderer(new VersionTableRenderer());
			checkBoxColumn.setMinWidth(22);
			checkBoxColumn.setMaxWidth(22);
			
			// columnWidth[0]�����1�У�table�е���Ҳ��0��
			for(int col = 0; col < columnWidth.length; col ++) {
				int columnNameWidth = fm.stringWidth(table.getColumnName(col + 1)) + getFont().getSize();
				if(columnWidth[col] < columnNameWidth) {
					columnWidth[col] = columnNameWidth;
				}
			}
		} else {
			TableModel model = new AbstractTableModel() {
				private static final long serialVersionUID = -8101609216188903678L;
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if(rowIndex == 0 && columnIndex == 1) {
						return "û������";
					}
					return null;
				}
				
				@Override
				public int getRowCount() {
					return 1;
				}
				@Override
				public int getColumnCount() {
					return 3;
				}
				public String getColumnName(int column) {
					return columnNames[column];
			    }
			};
			table.setModel(model);
			TableColumn checkBoxColumn = table.getColumnModel().getColumn(0);
			checkBoxColumn.setMinWidth(22);
			checkBoxColumn.setMaxWidth(22);
		}
	}
	
	private boolean hasData(String[] datas) {
		for(int i = 0; i < datas.length; i++) {
			if(datas[i] != null && !datas[i].equals("")) {
				return true;
			}
		}
		return false;
	}

	public List<Item> getSelectedItems() {
		ArrayList<Item> res = new ArrayList<Item>();
		for (int i = 0; i < table.getRowCount(); i++) {
			Boolean b = (Boolean)table.getValueAt(i, 0);
			if (b != null && b) {
				Item n = this.items.get(i);
				res.add(n);
			}
		}
		return res;
	}
	
	public int[] getSelectedRows() {
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for (int i = 0; i < table.getRowCount(); i++) {
			Boolean b = (Boolean)table.getValueAt(i, 0);
			if (b != null && b) {
				nums.add(i);
			}
		}
		
		int[] res = new int[nums.size()];
		for(int index = 0; index < res.length; index ++) {
			res[index] = nums.get(index);
		}
		return res;
	}
	
	public void removeRow(int row) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		flagBit.clear(row);
		model.removeRow(row);
	}
	
	public boolean hasSelectedItems() {
		for (int i = 0; i < table.getRowCount(); i++) {
			if(table.getValueAt(i, 0) != null) {
				Boolean b = (Boolean)table.getValueAt(i, 0);
				if(b) return true;
			}
		}
		return false;
	}

	private void selectAll() {
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setValueAt(true, i, 0);
		}
	}

	private void inverseSelect() {
		for (int i = 0; i < table.getRowCount(); i++) {
			Boolean b = (Boolean) table.getValueAt(i, 0);
			table.setValueAt(!b, i, 0);
		}
	}

	public JTable getTable() {
		return table;
	}
	
	public int getPaneMinWidth() {
		int res = 0;
		for(int i=0; i<columnWidth.length; i++) {
			res += columnWidth[i];
		}
		return (res+22);
	}
	
	public int getRowHeight() {
		return table.getRowHeight();
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	class SelectionLabel extends JLabel {
		private static final long serialVersionUID = 4791775800436112175L;

		public SelectionLabel(String text) {
			super(text);
			setFont(new Font("����", Font.BOLD, 13));
			setForeground(new Color(0, 166, 223));
			addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					SelectionLabel.this.setLocation(getX() - 1, getY() - 1);
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				public void mouseExited(MouseEvent e) {
					SelectionLabel.this.setLocation(getX() + 1, getY() + 1);
				}

				public void mouseClicked(MouseEvent e) {
					if ("ȫѡ".equals(getText())) {
						selectAll();
					} else if ("��ѡ".equals(getText())) {
						inverseSelect();
					}
					for(int i = 0; i < table.getRowCount(); i++) {
						if((Boolean)table.getValueAt(i, 0)) {
							flagBit.set(i);
						} else {
							flagBit.clear(i);
						}
					}
				}
			});
		}

	}
	
}
