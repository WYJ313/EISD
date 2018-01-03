package gui.softwareMeasurement.structureBrowser;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.table.TableModel;


public class VersionChoosenPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1291215952124589571L;
	private JTable table;
	JScrollPane pane;
	int secondColWidth = 0;
	int thirdColWidth = 0;
	SelectionLabel selectAll;
	SelectionLabel inverse;
	BitSet flagBit;

	public VersionChoosenPane(List<File> versions) {
		setLayout(null);
		table = new JTable();
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);        
		init(versions);
		
		pane = new JScrollPane(table);

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
		
		
		selectAll = new SelectionLabel("ȫѡ");
		inverse = new SelectionLabel("��ѡ");
		
		
		add(selectAll);
		add(inverse);
	}

	public void paint(Graphics g){
		super.paint(g);
		pane.setSize(getWidth(), getHeight() - 30);
		
		TableColumn secondColumn = table.getColumnModel().getColumn(1);
		secondColumn.setMinWidth(secondColWidth + 5);
		
		TableColumn thirdColumn = table.getColumnModel().getColumn(2);
		thirdColumn.setMinWidth(thirdColWidth + 5);
		if(22 + secondColWidth + thirdColWidth + 10 < getWidth()) {
			int deltaWidth = getWidth() - (22 + secondColWidth + thirdColWidth + 10);
			secondColumn.setMinWidth(secondColWidth + 5 + deltaWidth / 2);
			thirdColumn.setMinWidth(thirdColWidth + 5 + deltaWidth / 2);
		}
		int fontSize = selectAll.getFont().getSize();
		selectAll.setBounds(pane.getX(),
				pane.getY() + pane.getHeight()
						+ selectAll.getHeight(), fontSize * 4,
				fontSize + 2);
		inverse.setBounds(selectAll.getX() + fontSize * 4 + 5,
				selectAll.getY(), fontSize * 4, fontSize + 2);
	}
	
	private void init(List<File> versions) {
		if(versions != null) {
			DefaultTableModel model = new DefaultTableModel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 8468697960902577432L;

				public boolean isCellEditable(int row, int column) {
					if (column == 0)
						return true;
					return false;
				}
			};
			Boolean[] data = new Boolean[versions.size()];
			String[] names = new String[versions.size()];
			String[] paths = new String[versions.size()];
			FontMetrics fm = table.getFontMetrics(getFont());
			for (int i = 0; i < versions.size(); i++) {
				data[i] = false;
				names[i] = versions.get(i).getName();
				paths[i] = versions.get(i).getAbsolutePath();
				
				int nameColWidth = fm.stringWidth(names[i]);
				if(secondColWidth < nameColWidth) {
					secondColWidth = nameColWidth;
				}
				
				int pathColWidth = fm.stringWidth(paths[i]);
				if(thirdColWidth < pathColWidth) {
					thirdColWidth = pathColWidth;
				}
			}
			model.addColumn(" ", data);
			model.addColumn("�汾", names);
			model.addColumn("·��", paths);
			table.setModel(model);
			TableColumn checkBoxColumn = table.getColumnModel().getColumn(0);
			checkBoxColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
			checkBoxColumn.setCellRenderer(new VersionTableRenderer());
			checkBoxColumn.setMinWidth(22);
			checkBoxColumn.setMaxWidth(22);
		} else {
			TableModel model = new AbstractTableModel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 6409710861159159444L;
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if(rowIndex == 0 && columnIndex == 1) {
						return "δ�ҵ��汾";
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
					if(column == 0) {
						return " ";
					}
					if(column == 1) {
						return "�汾";
					}
					if(column == 2) {
						return "·��";
					}
					return super.getColumnName(column);
			    }
			};
			table.setModel(model);
			TableColumn checkBoxColumn = table.getColumnModel().getColumn(0);
			checkBoxColumn.setMinWidth(22);
			checkBoxColumn.setMaxWidth(22);
		}
	}

	public void setTableColumnName(int column) {
//		JTableHeader header = table.getTableHeader();
	}
	
	public List<File> getSelectedVersion() {
		ArrayList<File> versions = new ArrayList<File>();
		for (int i = 0; i < table.getRowCount(); i++) {
			Boolean b = (Boolean)table.getValueAt(i, 0);
			if (b != null && b) {
				String filePath = (String) table.getValueAt(i, 2);
				File file = new File(filePath);
				versions.add(file);
			}
		}
		return versions;
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

	class SelectionLabel extends JLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4597335041048419860L;

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
