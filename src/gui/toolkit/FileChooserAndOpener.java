package gui.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooserAndOpener {
	File file = null;
	String fileContents = null;
	String fileContentsWithLineNumber = null;
	
	JFrame parent = null;
	 
	public FileChooserAndOpener(JFrame parent) {
		this.parent = parent;
	}
	
	/**
	 * ��ʾ�ļ�ѡ��Ի��򣬲����û�Ҫ�򿪵��ļ���ȱʡ����´� Java Դ�����ļ�
	 * @return ���ѡ��ɹ����� true �����򷵻� false
	 */
	public boolean chooseFileName() {
		JFileChooser chooser = new JFileChooser();

		// ֻ�� .java �ļ�
		chooser.setFileFilter(new FileNameExtensionFilter("Java Դ�����ļ�...", "java"));
		// ����һ�δ򿪵��ļ���ʼѡ������ϴ�û��ѡ����ʱfile == null�����ȱʡĿ¼��ʼѡ��
		if (file != null) chooser.setCurrentDirectory(file);
		else chooser.setCurrentDirectory(new File("."));
		
		int result = chooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile(); 
			return true;
		} else return false;
	}
	
	/** 
	 * ѡ���ļ��ɹ�֮��װ���ļ����ݡ�
	 * @return ���û��ѡ���ļ���������װ������з��� I/O ���󷵻� false ��װ��ɹ����� true
	 */
	public boolean loadFile() {
		if (file == null) return false;
		
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(parent, "���ڶ�ȡ�ļ� [" + file.getName() + "]", fileIn);
			
			final Scanner in = new Scanner(progressIn);
			StringBuffer buffer = new StringBuffer(); 
			StringBuffer bufferWithLine = new StringBuffer();
			int lineCounter = 0;
			while (in.hasNextLine()) {
				lineCounter++;
				String line = in.nextLine();
				buffer.append(line + "\n");
				bufferWithLine.append(lineCounter + " " + line + "\n");
			}
			fileContents = buffer.toString();
			fileContentsWithLineNumber = bufferWithLine.toString();
			in.close();
			return true;
		} catch (IOException exc) {
			return false;
		}
	}
	
	/** 
	 * �����Ѿ�ѡ����ļ���
	 * @return ���û��ѡ���ļ��򷵻� null
	 */
	public String getFileName() {
		if (file == null) return null;
		else return file.getName();
	}

	/**
	 * ���ذ����ļ�ȫ�����ݵ��ַ���
	 * @return ����ļ�û��װ�سɹ��򷵻� null
	 */
	public String getFileContents() {
		return fileContents;
	}
	
	/**
	 * ���غ����кŵ��ļ�����
	 */
	public String getFileContentsWithLineNumber() {
		return fileContentsWithLineNumber;
	}
}
