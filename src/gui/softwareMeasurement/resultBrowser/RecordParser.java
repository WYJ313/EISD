package gui.softwareMeasurement.resultBrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gui.softwareMeasurement.metricBrowser.Item;

public class RecordParser {
	
	String[][] value;
	int rowCount = 0;
	List<Item> recorderItems;
	
	public RecordParser(File recordFile) {
		try {
			Scanner scanner = new Scanner(recordFile);
			while (scanner.hasNextLine()) {
				rowCount++;
				scanner.nextLine();
			}
			scanner.close();
			
			value = new String[rowCount][5];
			scanner = new Scanner(recordFile);
			String line = null;
			int rowIndex = 0;
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				String[] lineValue = line.split("\t");
				for(int i = 0; i < lineValue.length; i++) {
					// ÿһ�еĵ�һ���зŵ����ļ�·��������Ҫչʾ�����Ҳ����
					value[rowIndex][i] = lineValue[i];
				}
				rowIndex ++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		recorderItems = new ArrayList<Item>();
		for(int i = 0; i < value.length; i++) {
			RecorderItem item = new RecorderItem(value[i]);
			recorderItems.add(item);
		}
	}

	public List<Item> getRecorderItems() {
		return recorderItems;
	}
}
