package gui.softwareMeasurement.metricBrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import gui.softwareMeasurement.mainFrame.Log;
import gui.softwareMeasurement.resultBrowser.ResultFileParser;
import gui.softwareMeasurement.resultBrowser.ResultTable;
import gui.softwareMeasurement.resultBrowser.ResultTableFrame;
import gui.softwareMeasurement.structureBrowser.tree.ClassNode;
import gui.softwareMeasurement.structureBrowser.tree.NodeKind;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;
import gui.softwareMeasurement.structureBrowser.tree.VersionNode;
import nameTable.NameTableManager;
import nameTable.creator.NameDefinitionCreator;
import nameTable.creator.NameTableCreator;
import nameTable.filter.DetailedTypeDefinitionFilter;
import nameTable.filter.NameDefinitionLocationFilter;
import nameTable.filter.NameDefinitionNameFilter;
import nameTable.filter.NameTableFilter;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameDefinition.PackageDefinition;
import softwareMeasurement.ClassMeasurement;
import softwareMeasurement.MethodMeasurement;
import softwareMeasurement.PackageMeasurement;
import softwareMeasurement.SystemScopeMeasurement;
import softwareMeasurement.measure.SoftwareMeasure;
import softwareStructure.SoftwareStructManager;
import sourceCodeAST.SourceCodeFileSet;
import sourceCodeAST.SourceCodeLocation;

/**
 * �����������
 * @author Wu zhangsheng
 */
public class MetricCalculator extends Thread {

	String formatDate;
	String resFilePath;
	List<ProjectTreeNode> nodes;
	String[] metrics;
	private boolean isInit = true;
	JTabbedPane srcTabbedPane;
	
	public MetricCalculator(String formatDate,String resFilePath,
			List<ProjectTreeNode> nodes, String[] metrics, JTabbedPane srcTabbedPane) {
		this.formatDate = formatDate;
		this.resFilePath = resFilePath;
		this.nodes = nodes;
		this.metrics = metrics;
		
		this.srcTabbedPane = srcTabbedPane;
	}
	
	public void run() {
		try {
			calculate();
			File file = new File(resFilePath);
			ResultFileParser parser = new ResultFileParser(file);
			ResultTable table = new ResultTable(parser);
			table.setTextArea(srcTabbedPane);

			JFrame frame = new ResultTableFrame(table);
			frame.setTitle(file.getName());
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// �û���ѡ���ʱ����Ѿ�ȷ���˼����NameDefinition������
	// ��һ�μ��������ֻ�ܼ���ͬ�����ȵ�ʵ��
	public void calculate() throws FileNotFoundException {
		
		File file = new File(resFilePath);
		PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
		
		List<SoftwareMeasure> measures = new ArrayList<SoftwareMeasure>();
		for(String metric : metrics) {
			measures.add(new SoftwareMeasure(metric));
		}

		// lastVersionNode������¼�ϴμ���ʱ�İ汾�ڵ㣬��Ҫ��������ʵ���Ǵ�
		// ��ͬһ���汾��ʱֻ��Ҫ����һ�����ֱ�
		ProjectTreeNode lastVersionNode = null;
		SourceCodeFileSet parser = null;
		NameTableCreator creator = null;
		NameTableManager nameTableManager = null;
		SoftwareStructManager structManager = null;
		NodeKind kind = null;
		List<SoftwareMeasure> resultMeasures = null;
		String nodesStr="";
		
		for(int index = 0; index < nodes.size(); index ++) {
			ProjectTreeNode n = nodes.get(index);

			if(index < 2) {
				nodesStr += n.getSimpleName() + ";";
			}
			
			switch(n.getNodeKind()) {
			case PROJECT_NODE:// ѡ��ϵͳ�Ļ�����������а汾�ֱ����
				Log.consoleLog("System metric calculating...");
				@SuppressWarnings("unchecked") 
				Enumeration<ProjectTreeNode> versionNodes = n.children();
				resultMeasures = new ArrayList<SoftwareMeasure>();
				for(SoftwareMeasure m : measures) {
					SoftwareMeasure sMeasure = new SoftwareMeasure(m.getIdentifier());
					resultMeasures.add(sMeasure);
				}
				int versCount = 0;
				while(versionNodes.hasMoreElements()) {
					versCount ++;
					ProjectTreeNode version = versionNodes.nextElement();
					Log.consoleLog("calculating " + n.getSimpleName());
					parser = new SourceCodeFileSet(version.getLocation());
					creator = new NameDefinitionCreator(parser);
					nameTableManager = creator.createNameTableManager();
					structManager = new SoftwareStructManager(nameTableManager);
					SystemScopeMeasurement sysMeasurement = new SystemScopeMeasurement(nameTableManager.getSystemScope(),structManager);
					List<SoftwareMeasure> versMeasures = sysMeasurement.getMeasureList(measures);
					// �������ϵͳ�ڵ�ʱ�������԰汾Ϊ����·���������ֽڵ㣬��˴˴����ж��ѭ��
					for(int i = 0; i < resultMeasures.size(); i++) {
						SoftwareMeasure res = resultMeasures.get(i);
						SoftwareMeasure ver = versMeasures.get(i);
						if(ver.isUsable()) {
							if(res.isUsable()) {
								res.setValue(res.getValue() + ver.getValue());
							} else {
								res.setUnusable();
								res.setValue(ver.getValue());
							}
						}
					}
				}
				for(SoftwareMeasure m : resultMeasures) {
					if(m.isUsable()) {
						m.setValue(m.getValue()/versCount);
					}
				}
				kind = NodeKind.PROJECT_NODE;
				break;
			case VERSION_NODE:// ѡ��汾����ֱ�Ӽ���
				Log.consoleLog("Version metric calculating...");
				parser = new SourceCodeFileSet(n.getLocation());
				creator = new NameDefinitionCreator(parser);
				nameTableManager = creator.createNameTableManager();
				structManager = new SoftwareStructManager(nameTableManager);
				SystemScopeMeasurement sysMeasurement = new SystemScopeMeasurement(nameTableManager.getSystemScope(),structManager);
				resultMeasures = sysMeasurement.getMeasureList(measures);
				kind = NodeKind.VERSION_NODE;
				break;
			case PACKAGE_NODE:// ѡ���
				Log.consoleLog("Package metric calculating...");
				// �԰��ĸ��ڵ�(�汾)���������ֱ�����ȡPackageDefinition
				VersionNode versionNode = (VersionNode)n.getParent();
				if(!versionNode.equals(lastVersionNode)) {
					lastVersionNode = versionNode;
					parser = new SourceCodeFileSet(versionNode.getLocation());
					creator = new NameDefinitionCreator(parser);
					nameTableManager = creator.createNameTableManager();
					structManager = new SoftwareStructManager(nameTableManager);
				}
				Log.consoleLog("calculating " + n.getSimpleName());
				PackageDefinition pack = nameTableManager.findPackageByName(n.getSimpleName());
				PackageMeasurement packMeasure = new PackageMeasurement(pack, structManager);
				resultMeasures = packMeasure.getMeasureList(measures);
				kind = NodeKind.PACKAGE_NODE;
				break;
			case CLASS_NODE:
				Log.consoleLog("Class metric calculating...");
				// �ð汾���������ֱ�����ȡDetailedTypeDefinition
				VersionNode vNode = (VersionNode)(n.getParent().getParent());
				if(!vNode.equals(lastVersionNode)) {
					lastVersionNode = vNode;
					parser = new SourceCodeFileSet(vNode.getLocation());
					creator = new NameDefinitionCreator(parser);
					nameTableManager = creator.createNameTableManager();
					structManager = new SoftwareStructManager(nameTableManager);
				}
				Log.consoleLog("calculating " + n.getSimpleName());

				NameTableFilter filter = new DetailedTypeDefinitionFilter(
						new NameDefinitionNameFilter(
								new NameDefinitionLocationFilter(
										SourceCodeLocation.getLocation(n.getLocation())), n.getSimpleName()));
				DetailedTypeDefinition type = (DetailedTypeDefinition)nameTableManager.findDefinitionByFilter(filter);

				ClassMeasurement classMeasure = new ClassMeasurement(type, structManager);
				resultMeasures = classMeasure.getMeasureList(measures);
				kind = NodeKind.CLASS_NODE;
				break;
			case METHOD_NODE:
				Log.consoleLog("Method metric calculating...");
				// �ð汾���������ֱ�������MethodDefinition
				VersionNode verNode = (VersionNode)(n.getParent().getParent().getParent());
				if(!verNode.equals(lastVersionNode)) {
					lastVersionNode = verNode;
					parser = new SourceCodeFileSet(verNode.getLocation());
					creator = new NameDefinitionCreator(parser);
					nameTableManager = creator.createNameTableManager();
					structManager = new SoftwareStructManager(nameTableManager);
				}
				Log.consoleLog("calculating " + n.getSimpleName());

				filter = new DetailedTypeDefinitionFilter(
						new NameDefinitionNameFilter(
								new NameDefinitionLocationFilter(
										SourceCodeLocation.getLocation(n.getLocation())), n.getSimpleName()));
				DetailedTypeDefinition typeOfMethod = (DetailedTypeDefinition)nameTableManager.findDefinitionByFilter(filter);

				List<MethodDefinition> methods = typeOfMethod.getMethodList();
				for(MethodDefinition m : methods) {
					if(m.getLocation().equals(n.getLocation())) {
						MethodMeasurement methodMeasure = new MethodMeasurement(m, structManager);
						resultMeasures = methodMeasure.getMeasureList(measures);
						break;
					}
				}
				kind = NodeKind.METHOD_NODE;
				break;
			default:
				break;
			}
			saveResult(pw, n, resultMeasures);
		}
		if(nodes.size() == 3) {
			ProjectTreeNode n = nodes.get(2);
			nodesStr += n.getSimpleName();
		}
		if(nodes.size() > 3) {
			ProjectTreeNode n = nodes.get(nodes.size() - 1);
			nodesStr += "...";
			nodesStr += n.getSimpleName()+";";
		}
		Recorder r = new Recorder(formatDate, nodesStr, kind, metrics, resFilePath);
		r.save();
		
		pw.close();
	}

	private void saveResult(PrintWriter pw, ProjectTreeNode n,
			List<SoftwareMeasure> resultMeasures) {
		if(isInit) {// ��ʼд���������֮����Ϊ��ͷ����Ϣչʾ����
			isInit = false;
			pw.write("·��\t");
			switch(n.getNodeKind()) {// д�뱻������ʵ������
			case PROJECT_NODE:
				pw.write("ϵͳ\t");
				break;
			case VERSION_NODE:
				pw.write("�汾\t");
				break;
			case PACKAGE_NODE:
				pw.write("��\t");
				break;
			case CLASS_NODE:
				pw.write("��\t");
				break;
			case METHOD_NODE:
				pw.write("����\t");
				break;
			default:
				break;
			}
			// д�����Ķ�������
			for(int i = 0; i < metrics.length - 1; i++) {
				pw.write(metrics[i] + "\t");
			}
			pw.write(metrics[metrics.length - 1] + "\r\n");
		}
		
		// �ڵ��Ӧ�ļ���ʵ��·��
		if(n.getNodeKind() == NodeKind.CLASS_NODE) {
			String path = ((ClassNode)n).getFilePath();
			System.out.println(path);
			pw.write(path+"\t");
		} else {
			pw.write(n.getLocation()+"\t");
		}
		
		// д�뱻������ʵ���������
		pw.write(n.getQualifiedName()+"\t");
		// д���������Ķ���ֵ
		for(int i = 0; i < resultMeasures.size() - 1; i++) {
			SoftwareMeasure m = resultMeasures.get(i);
			pw.write(m.getValue() + "\t");
		}
		pw.write(resultMeasures.get(resultMeasures.size()-1).getValue() + "\r\n");
		pw.flush();
	}
}
