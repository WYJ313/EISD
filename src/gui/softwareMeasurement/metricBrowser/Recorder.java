package gui.softwareMeasurement.metricBrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import gui.softwareMeasurement.structureBrowser.tree.NodeKind;

/**
 * һ��Recorder��Ӧ��ʷ��¼�е�һ��
 * @author Wu zhangsheng
 */
public class Recorder {
	
	String formatDate;
	String formatMetrics;
	String nodesStr;
	NodeKind kind;
	String resultFilePath;
	
	public Recorder(String formatDate, String nodesStr, NodeKind kind,
			String[] metrics, String resultFilePath) {
		this.formatDate = formatDate;
		this.formatMetrics="";
		for(String m : metrics) {
			this.formatMetrics += m+";" ;
		}
		this.nodesStr = nodesStr;
		this.kind = kind;
		this.resultFilePath = resultFilePath;
	}
	

	public void save() {
		File file = new File("result/record.txt");
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
			pw.write(formatDate + "\t");
			pw.write(nodesStr+"\t");
			switch(kind) {
			case PROJECT_NODE:
				pw.write("ϵͳ"+"\t");
				break;
			case VERSION_NODE:
				pw.write("�汾"+"\t");
				break;
			case PACKAGE_NODE:
				pw.write("��"+"\t");
				break;
			case CLASS_NODE:
				pw.write("��"+"\t");
				break;
			case METHOD_NODE:
				pw.write("����"+"\t");
				break;
			default:
				pw.write("\t");
				break;
			}
			pw.write(formatMetrics+"\t");
			pw.write(resultFilePath+"\r\n");
			pw.flush();
			
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
