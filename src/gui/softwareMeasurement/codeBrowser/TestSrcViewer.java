package gui.softwareMeasurement.codeBrowser;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class TestSrcViewer {
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame();

		/*
		 * ֻ��Ӵ�ֱ�������Ļ���JTextPane�ŵ�JScrollPane��, ����ˮƽ������,����������ʹ��JTextPane�Զ�����;
		 * 
		 * ���ˮƽ�ʹ�ֱ��������Ҫ��JTextPane�ŵ�JPanel��ٰ�JPanel�ŵ�JScrollPane�
		 * JTextPane���û��ˮƽ�������Ļ�JTextPane���Զ����У�����Ͳ��ᡣ
		 */
		JSrcViewer viewer = new JSrcViewer(
				"D:\\Program Files\\workspace2\\ProgramAnalysisUI_20150925\\src\\softwareStructure\\SoftwareStructManager.java");
		viewer = new JSrcViewer(
				"E:\\�ĵ�\\lucene-3.5\\src\\java\\org\\apache\\lucene\\analysis\\standard\\StandardAnalyzer.java");

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(viewer);
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setRowHeaderView(new LineNumberHeaderView(viewer.lineCount));
		frame.add(scrollPane);
		frame.setSize(500, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
