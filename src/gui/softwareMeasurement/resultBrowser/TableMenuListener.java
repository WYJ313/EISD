package gui.softwareMeasurement.resultBrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import gui.softwareMeasurement.mainFrame.Log;
import gui.softwareMeasurement.resultBrowser.chart.BarChart;
import gui.softwareMeasurement.resultBrowser.chart.BoxChart;
import gui.softwareMeasurement.resultBrowser.chart.ClassMeasureData;
import gui.softwareMeasurement.resultBrowser.chart.HistChart;
import gui.softwareMeasurement.resultBrowser.chart.LineChart;
import gui.softwareMeasurement.resultBrowser.chart.MeasureDistribution;
import gui.softwareMeasurement.resultBrowser.chart.MeasurementValue;

public class TableMenuListener implements ActionListener {
	
	
	ResultTable table;
	
	public TableMenuListener(ResultTable table) {
		this.table = table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		final int[] rows = table.getSelectedRows();
		final int[] cols = table.getSelectedColumns();
		ResultFileParser parser = (ResultFileParser) table.getModel();
		if (command.equals(ResultTable.MENU_OPEN_SRC_STR)) {
			for (int row : rows) {
				String filePath = parser.getFilePath(row);
				File file = new File(filePath);
				if (file.isFile()) {
					table.showSrc(filePath);
				} else if(file.isDirectory()){
					JOptionPane.showMessageDialog(null, file.getName() + 
							"��һ���ļ��У�", "��ʾ", JOptionPane.YES_OPTION);
				} else {
					if(table.getColumnName(0).equals("��")) {
						JOptionPane.showMessageDialog(null, table.getValueAt(row, 0)+
								"���ļ��У��޷��򿪣�");
					} else {
						JOptionPane.showMessageDialog(null, "�ļ�" + file.getName() + 
								"�����ڣ�", "��ʾ", JOptionPane.YES_OPTION);
					}
				}
			}
		}
		if (command.equals(ResultTable.MENU_BAR_CHART_STR)) {
			new Thread() {
				public void run() {
					List<ClassMeasureData> datas = encapsulateData(
							rows, cols);
					BarChart chart = new BarChart("chart", datas);
					chart.setVisible(true);
				}
			}.start();

		}
		if (command.equals(ResultTable.MENU_HIST_CHART_STR)) {
			new Thread() {
				public void run() {
					List<MeasureDistribution> datas = getAllMeasureDistributions(
							rows, cols);
					for(MeasureDistribution data : datas) {
						HistChart chart = new HistChart("chart", data);
						chart.setVisible(true);
					}
				}
			}.start();
		}
		if (command.equals(ResultTable.MENU_BOX_CHART_STR)) {
			new Thread() {
				public void run() {
					List<MeasureDistribution> datas = getAllMeasureDistributions(
							rows, cols);
					BoxChart chart = new BoxChart("chart", datas);
					chart.setVisible(true);
				}
			}.start();
		}
		if (command.equals(ResultTable.MENU_LINE_CHART_STR)) {
			new Thread() {
				public void run() {
					List<ClassMeasureData> datas = encapsulateData(
							rows, cols);
					LineChart chart = new LineChart("chart", datas);
					chart.setVisible(true);
				}
			}.start();
		}
		if (command.equals(ResultTable.MENU_VARIANCE_STR)) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "����\r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.variance(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("���ķ�λ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "���ķ�λ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.littleQuartile(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("��λ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "��λ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.median(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("���ķ�λ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "���ķ�λ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.bigQuartile(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("��ֵ")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "��ֵ \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.mean(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("ƫ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "ƫ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.skewness(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("���")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "��� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.kurtosis(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("����ϵ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "����ϵ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.CV(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("����ָ��")) {
			int[] selectedCols = table.getSelectedColumns();
			String output = "����ָ�� \r\n  ";
			for(int col : selectedCols) {
				if(col > 0) {
					List<Double> datas = getSelectedNumber(col);
					output += table.getColumnName(col);
					output += ": ";
					output += MetricMath.gini(datas);
					output += ";  ";
				}
			}
			String op = output.replace(";", "\r\n");
			op = "  " + op;
			JOptionPane.showMessageDialog(null, op);
			Log.consoleLog(output);
		}
		if (command.equals("������Ԫ��")) {
			if(ResultTable.searcher == null) {
				ResultTable.searcher = new Searcher(table);
			} else {
				ResultTable.searcher.setSearchViewer(table);
			}
			ResultTable.searcher.setVisible(true);
		}
	}
	
	/**
	 * ��ȡ�����ָ���еı�ѡ�е�����
	 * @param col
	 * @return
	 */
	private List<Double> getSelectedNumber(int col) {
		List<Double> datas = new ArrayList<Double>();
		for(int i : table.getSelectedRows()) {
			Object o = table.getValueAt(i, col);
			String s = (String)o;
			if(!s.equals("N.A")) {
				double value = Double.valueOf(s);
				datas.add(value);
			}
		}
		return datas;
	}

	private List<ClassMeasureData> encapsulateData(int[] selectedRows,
			int[] selectedCols) {
		ArrayList<ClassMeasureData> datas = new ArrayList<ClassMeasureData>();
		for (int row : selectedRows) {
			ClassMeasureData data = new ClassMeasureData((String) table.getValueAt(
					row, 0));
			for (int col : selectedCols) {
				if (col > 0) {// ����������������ǰ���Ѿ�ȡ��
					String value = (String) table.getValueAt(row, col);
					data.addMeasure(new MeasurementValue(table.getColumnName(col),
							Double.parseDouble(value)));
				}
			}
			datas.add(data);
		}
		return datas;
	}

	private List<MeasureDistribution> getAllMeasureDistributions(
			int[] selectedRows, int[] selectedCols) {
		List<MeasureDistribution> res = new ArrayList<MeasureDistribution>();
		for (int col : selectedCols) {
			if (col > 0) {// ��һ��Ϊ����
				ArrayList<Double> values = new ArrayList<Double>();
				for (int row : selectedRows) {
					String value = (String) table.getValueAt(row, col);
					if (row > 0) {// ��һ��Ϊ������
						values.add(Double.parseDouble(value));
					}
				}
				// ��ŵ��Ƕ�������ֲ����
				MeasureDistribution distribution = new MeasureDistribution(
						table.getColumnName(col), values);
				res.add(distribution);
			}
		}

		return res;
	}
}
