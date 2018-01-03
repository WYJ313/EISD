package gui.softwareMeasurement.mainFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import gui.softwareMeasurement.metricBrowser.MetricCalculationDlg;
import gui.softwareMeasurement.resultBrowser.RecorderTableFrame;
import gui.softwareMeasurement.structureBrowser.ProjectBuildDialog;

public class MainToolBar extends JToolBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6196910031739702954L;

	public MainToolBar(final JsMetricFrame frame) {
		
		ImageIcon newPro_Icon = new ImageIcon("res/img/new_project.png");
		final JButton newProBtn = new JButton(newPro_Icon);
		newProBtn.setContentAreaFilled(false);// ����ʾ��ť�ı߿�(Ч����ֻ��һ��ͼƬ)
		newProBtn.setToolTipText("�½���Ŀ");
		add(newProBtn);
		
		ImageIcon cal_Icon = new ImageIcon("res/img/cal_metric.png");
		final JButton calculateMetricBtn = new JButton(cal_Icon);
		calculateMetricBtn.setContentAreaFilled(false);
		calculateMetricBtn.setToolTipText("�������ֵ");
		add(calculateMetricBtn);
		
		ImageIcon res_Icon = new ImageIcon("res/img/result_browser.png");
		final JButton resultBrowserBtn = new JButton(res_Icon);
		resultBrowserBtn.setContentAreaFilled(false);
		resultBrowserBtn.setToolTipText("����������");
		add(resultBrowserBtn);
		
		MouseAdapter adapter = new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent e) {
				Object o = e.getSource();
				JButton button = (JButton)o;
				button.setContentAreaFilled(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				Object o = e.getSource();
				JButton button = (JButton)o;
				button.setContentAreaFilled(true);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Object o = e.getSource();
				if(o.equals(newProBtn)) {
					ProjectBuildDialog dlg = new ProjectBuildDialog(frame.treePanel.getManager());
					dlg.setVisible(true);
				}
				if(o.equals(calculateMetricBtn)) {
//					MetricBrower metricBrower = new MetricBrower(frame.treePanel.getManager(), "");
//					metricBrower.setVisible(true);
//					metricBrower.setResizable(false);
					
//					MetricFrame mframe = new MetricFrame(frame.treePanel.getManager(), frame.srcTabbedPane);
//					mframe.setVisible(true);
					
					MetricCalculationDlg dlg = new MetricCalculationDlg(frame.treePanel.getManager(), frame.srcTabbedPane);
					dlg.setVisible(true);
				}
				if(o.equals(resultBrowserBtn)) {
//					setCursor(new Cursor(Cursor.WAIT_CURSOR));
//					ResultTreePane resPanel = new ResultTreePane(frame.srcTabbedPane, frame.statusBar);
//					JFrame frame = new JFrame("��������");
//					frame.add(resPanel);
//					frame.setSize(200, 300);
//					frame.setVisible(true);
//					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JFrame rframe = new RecorderTableFrame(frame.srcTabbedPane);
					rframe.setVisible(true);
				}
 			}
		};
		
		newProBtn.addMouseListener(adapter);
		calculateMetricBtn.addMouseListener(adapter);
		resultBrowserBtn.addMouseListener(adapter);
	}

}
