package gui.softwareMeasurement.resultBrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.table.AbstractTableModel;

public class ResultFileParser extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7876348714135440640L;
	int rowCount = 0;
	int colCount = 0;
	File file;
	String[] columnNames;
	String[][] value;
	List<String> filePaths = new ArrayList<String>();
	

	public ResultFileParser(File file) {
		this.file = file;
		// ɨ�����Σ���һ�εõ����ݵ����������ҳ�ʼ���ļ�·�����ڶ���������ݵ�String������ȥ��
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				rowCount++;
				String line = scanner.nextLine();
				if (colCount == 0) {
					columnNames = line.split("\t");
					colCount = columnNames.length;
					--colCount;// ��һ�����ļ�·��������һ��
				} else {
					filePaths.add(line.split("\t")[0]);
				}
			}
			--rowCount;// ��һ���Ǳ�ͷ����������һ��
			scanner.close();
			
			value = new String[rowCount][colCount];
			scanner = new Scanner(file);
			String line = null;
			int rowIndex = 0;
			scanner.nextLine();//��һ������
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				String[] lineValue = line.split("\t");
				for(int i = 0; i < colCount; i++) {
					// ÿһ�еĵ�һ���зŵ����ļ�·��������Ҫչʾ�����Ҳ����
					value[rowIndex][i] = lineValue[i+1];
				}
				rowIndex ++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return colCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return value[rowIndex][columnIndex];
	}

	public String getColumnName(int column) {
		return columnNames[column + 1];
	}

	public String getFilePath(int row) {
		if (filePaths != null) {
			return filePaths.get(row);
		}
		return null;
	}

}
