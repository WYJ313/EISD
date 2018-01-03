package gui.toolkit;

import java.awt.*;
import javax.swing.*;
 
public class MainFrame {
	// ��ȡ��ʾ���Ŀ�Ⱥ͸߶ȣ�����Ϊ�������ԣ�ʹ���߿ɾݴ˼��㻭���λ��
	public static final int screenWidth = 
			(int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final int screenHeight = 
			(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	// �����������ȱʡ��Ⱥ�ȱʡλ��
	private static int width = screenWidth / 3;
	private static int height = screenHeight / 4;
	private static int startX = screenWidth / 3;
	private static int startY = screenHeight / 3;
	private static JFrame frame;
	private static JPanel contentPane; 

	// ʹ��˽�еĹ��췽���ɷ�ֹʹ���ߴ���MainFrame�������ǹ�����ĳ�������
	private MainFrame() { }
	// ��ʹ�ù��췽������ʹ��init()������ʼ�����κ�ʹ����MainFrame�ĳ�������ȵ���init()����
	public static void init(String title) {
		frame = new JFrame(title); 
		frame.setLocation(new Point(startX, startY));
		contentPane = (JPanel)frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void init(String title, int w, int h, int x, int y) {
		width = w;  height = h;  startX = x;  startY = y;
		init(title);
	}
	// ��ʼ���������û���Ĺ۸�
	public static void init(String title, int w, int h, int x, int y, String lookAndFeel){
		try {
			if (lookAndFeel.equalsIgnoreCase("windows"))
				UIManager.setLookAndFeel(
						"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			else if (lookAndFeel.equalsIgnoreCase("system")) 
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			else if (lookAndFeel.equalsIgnoreCase("motif"))
				UIManager.setLookAndFeel(
						"com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			else UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {}
		width = w;  height = h;  startX = x;  startY = y;
		init(title);
	}
	// ʹ����ɼ����Ӷ���������GUI
	public static void start() { frame.pack();  frame.setVisible(true); }
	// ��ȡ��������ݴ���ʹ���߿����˴��������������GUI���
	public static JPanel getContentPane() { return contentPane; }
	// ��ȡ����ʹ�öԻ���Ͳ˵��ĳ���Ҫֱ�ӻ��ڻ�����
	public static JFrame getMainFrame() { return frame; }
}
