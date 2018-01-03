package gui.softwareMeasurement.resultBrowser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableCellRenderer;

public class ResultTableCellRenderer extends DefaultTableCellRenderer implements MouseInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5292826765779913212L;
	JTable table;
	int row = -1;
	int col = -1;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		this.table = table;
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		this.setForeground(Color.black);
		// �����ǰ��Ҫ��Ⱦ���ĵ�Ԫ���������¼����ڵĵ�Ԫ��
		if (row == this.row && column == this.col) {
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
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

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
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


}
