package gui.softwareMeasurement.resultBrowser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableCellRenderer;

public class RecorderTableCellRenderer extends DefaultTableCellRenderer implements
		MouseInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5707989757771341937L;
	JTabbedPane tabbedPane;
	// ����¼����ڵ���
	private int row = -1;
	// ����¼����ڵ���
	private int col = -1;
	// ��ǰ������Table
	private JTable table = null;
	
	
	public RecorderTableCellRenderer(JTabbedPane pane) {
		tabbedPane = pane;
	}
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// �ָ�Ĭ��״̬
		this.table = table;
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		this.setForeground(Color.black);
		table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.setText((String)value);
		// �����ǰ��Ҫ��Ⱦ���ĵ�Ԫ���������¼����ڵĵ�Ԫ��
		if (row == this.row && column == this.col) {
			// ����������ʾ�����ӵ���
			if (column == 5) {
				// �ı�ǰ��ɫ(������ɫ)
				this.setForeground(Color.blue);
				// �ı������״
				table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//				this.setText("<html><u>" + value + "</u></html>");
			}
			setBackground(table.getSelectionBackground());
		} else if (isSelected) {
			// �����Ԫ��ѡ��,��ı�ǰ��ɫ�ͱ���ɫ
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			// ��������»ָ�Ĭ�ϱ���ɫ
			setBackground(Color.white);
		}
		return this;
	}

	/**
	 * ����Ƴ��¼�
	 * 
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {
		if (table != null) {
			int oldRow = row;
			int oldCol = col;
			// ����Ƴ�Ŀ�����,�ָ��������ݵ�Ĭ��ֵ
			row = -1;
			col = -1;
			// ��֮ǰ������������Чʱ�ػ��������
			if (oldRow != -1 && oldCol != -1) {
				Rectangle rect = table.getCellRect(oldRow, oldCol, false);
				table.repaint(rect);
			}
		}
	}

	/**
	 * ����϶��¼�
	 * 
	 * @param e
	 */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	/**
	 * ����ƶ��¼�
	 * 
	 * @param e
	 */
	public void mouseMoved(MouseEvent e) {
		if (table != null) {
			Point p = e.getPoint();
			int oldRow = row;
			int oldCol = col;
			row = table.rowAtPoint(p);
			col = table.columnAtPoint(p);
			// �ػ�ԭ��������
			if (oldRow != -1 && oldCol != -1) {
				Rectangle rect = table.getCellRect(oldRow, oldCol, false);
				table.repaint(rect);
			}
			// �ػ��µ�����
			if (row != -1 && col != -1) {
				Rectangle rect = table.getCellRect(row, col, false);
				table.repaint(rect);
			}
		}
	}

	/**
	 * ��굥���¼�
	 * 
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 1) {
			// ��ȡ�¼����ڵ�����������Ϣ
			Point p = e.getPoint();
			int c = table.columnAtPoint(p);
			if (c != 5) {// ��5���ǽ���ļ�����·��
				return;// ����ʱ������ǵ�����5�оͲ�������
			}
			int r = table.rowAtPoint(p);
			showSelectedRowResultFile(r);
		} else if(e.getClickCount() == 2){// ˫��ʱ��������һ�ж��򿪸��ж�Ӧ�Ľ���ļ�
			Point p = e.getPoint();
			int r = table.rowAtPoint(p);
			showSelectedRowResultFile(r);
		}
	}

	private void showSelectedRowResultFile(int row) {
		String filePath = (String)table.getValueAt(row, 5);
		File file = new File(filePath);
		if(!file.exists()) {
			JOptionPane.showMessageDialog(null, "�ļ�" + filePath + "�����ڣ�");
		}
		
		if(file.isFile()) {
			ResultFileParser parser = new ResultFileParser(file);
			ResultTable table = new ResultTable(parser);
			table.setTextArea(tabbedPane);
			
			JFrame frame = new ResultTableFrame(table);
			frame.setTitle(file.getName());
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
		}
	}
	
	/**
	 * ��갴���¼�
	 * 
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * ����ͷ��¼�
	 * 
	 * @param e
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * �������¼�
	 * 
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * ���Է���
	 * 
	 * @param args
	 */
//	public static void main(String[] args) {
//		
//		File file = new File("result/record.txt");
//		// �����������ģ��
//		TableModel model = new RecordParser(file);
//		// ����������
//		JTable table = new JTable(model);
//		// ������Ԫ����Ⱦ��������¼�������
//		RecorderTableCellRenderer renderer = new RecorderTableCellRenderer(null);
//		// ע����Ⱦ��
//		table.setDefaultRenderer(Object.class, renderer);
//		// ע�������
//		table.addMouseListener(renderer);
//		table.addMouseMotionListener(renderer);
//
//		// Ϊ������Ӱ���������
//		JScrollPane sp = new JScrollPane(table);
//		// �������ڳ���
//		JFrame f = new JFrame("JTable ��Ԫ�����Ӳ���");
//		f.getContentPane().add(sp, BorderLayout.CENTER);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		f.setSize(800, 600);
//		f.setLocationRelativeTo(null);
//		// ��ʾ����
//		f.setVisible(true);
//	}
}