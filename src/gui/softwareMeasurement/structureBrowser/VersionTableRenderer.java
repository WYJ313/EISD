package gui.softwareMeasurement.structureBrowser;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class VersionTableRenderer extends JCheckBox implements
		TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5411002714349310087L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setSelected((Boolean) value);
		return this;
	}

}
