package gui.softwareMeasurement.structureBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import gui.softwareMeasurement.structureBrowser.tree.ClassNode;
import gui.softwareMeasurement.structureBrowser.tree.FieldNode;
import gui.softwareMeasurement.structureBrowser.tree.MethodNode;
import gui.softwareMeasurement.structureBrowser.tree.NodeKind;
import gui.softwareMeasurement.structureBrowser.tree.PackageNode;
import gui.softwareMeasurement.structureBrowser.tree.ProjectNode;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTree;
import gui.softwareMeasurement.structureBrowser.tree.ProjectTreeNode;
import gui.softwareMeasurement.structureBrowser.tree.VersionNode;
import nameTable.NameTableManager;
import nameTable.creator.NameDefinitionCreator;
import nameTable.creator.NameTableCreator;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.FieldDefinition;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameDefinition.NameDefinitionKind;
import nameTable.nameDefinition.PackageDefinition;
import nameTable.nameDefinition.VariableDefinition;
import nameTable.nameScope.SystemScope;
import softwareMeasurement.ClassMeasurement;
import softwareMeasurement.MethodMeasurement;
import softwareMeasurement.PackageMeasurement;
import softwareMeasurement.SystemScopeMeasurement;
import softwareMeasurement.measure.SoftwareMeasure;
import softwareMeasurement.measure.SoftwareMeasureIdentifier;
import softwareStructure.SoftwareStructManager;
import sourceCodeAST.SourceCodeFileSet;

public class BuildWorker extends SwingWorker<Boolean, Void> implements ButtonActionListener {

	ProjectTreeManager manager;
	SoftwareStructManager structManager;
	List<File> versions;
	ProjectInformation project;
	int currentNum = 0;
	int packProgress = 0;
	ProgressDialog dlg;
	boolean cancel = false;
	
	/* �����½���Ŀ���߼��ذ汾��ʱ������������Ŀ */
	// ��������versions������Ϊ�գ��ҵ��б�Ȼ��������һ���汾�ļ���
	public BuildWorker(ProjectTreeManager manager, List<File> versions, ProjectInformation proInfo) {
		this.manager = manager;
		this.versions = versions;
		this.project = proInfo;
		
		ProjectTree.canFresh = false;
		ProjectTree.isBuilding = true;
		dlg = new ProgressDialog(this);
	}
	
	
	@Override
	protected Boolean doInBackground() throws Exception {
//		progressBar.setValue(0);
		dlg.setVerMinimum(0);
		dlg.setVerMaximum(versions.size());
		dlg.setPackMinimum(0);
		dlg.setVisible(true);
		loadProject(project, versions);
		return Boolean.TRUE;
	}
	
	
	protected void done() {
		try {
			if(get()) {
//				progressBar.setVisible(false);
//				infoLabel.setVisible(false);
				manager.getTree().updateUI();
				ProjectTree.canFresh = true;
				ProjectTree.isBuilding = false;
				dlg.setVisible(false);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * ����������Ŀʱ����ذ汾������װ�ذ汾��ʱ������µİ汾��versions���ǽ�������Ŀ�а����İ汾
	 * �ڼ��ذ汾ʱ������汾���ڵ���Ŀ�Ѿ����ڣ���ֱ�Ӽ��ص��Ǹ���Ŀ��
	 * @param proInfo
	 * @param versions
	 */
	public void loadProject(ProjectInformation proInfo, List<File> versions) {
		// �����Ŀ�Ѿ����ڣ��򽫰汾���ص�����Ŀ֮��
		ProjectTreeNode n = manager.getProjectNodeByProjectName(proInfo.getProjectName());
		if( n != null) {
			dlg.setTitle("�����У����Ժ�...");
			loadVersions(versions, n);
		} else {// ��Ŀ�����ڣ��½���Ŀ�����ذ汾
			dlg.setTitle("������Ŀ" + proInfo.getProjectName() +"�����Ժ�...");
			// ��ʼ��Metrics�е����ݶ�ΪN.A
			n = new ProjectNode(NodeKind.PROJECT_NODE,
					proInfo.getProjectName() + "@" + proInfo.getLinkedDirPath(), 
					getMetricsString(getAvailableSoftwareSizeMeasureList()));
			
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) manager.getTree().getModel()
					.getRoot();
			root.add(n);
			loadVersions(versions, n);
			if(!cancel) {
				ProjectTreeManager.projectIds.add(proInfo.getProjectName());
			} else {// ȡ����ʱ�򣬽��ڵ��Ƴ�
				n.removeFromParent();
				System.gc();
			}
		}
		if(!cancel) {
			((ProjectTree) manager.getTree()).save(proInfo.getProjectName());// ������֮��������Ϣ���浽�ļ���
		}
	}
	
	
	private void loadVersions(List<File> versions, ProjectTreeNode parentNode) {
		if (versions != null && !versions.isEmpty()) {
			List<ProjectTreeNode> addedNodes = new ArrayList<ProjectTreeNode>();
			int max = dlg.getVerMaximum();
			dlg.setVerProgressString("0/" + max);
			for (File f : versions) {
				if(!cancel) {// ��¼�����¼���İ汾���Ա��û�ȡ������ʱ�Ƴ���Щ�ڵ�
					ProjectTreeNode vNode = loadVersion(f, parentNode);
					addedNodes.add(vNode);
					dlg.setVerProgress(++ currentNum);
					dlg.setVerProgressString(currentNum + "/" + max);
				}
			}
			if(cancel) {
				for(ProjectTreeNode n : addedNodes) {
					n.removeFromParent();
				}
				System.gc();
			}
		}
	}
	
	
	private ProjectTreeNode loadVersion(File versionDir, ProjectTreeNode parent) {
		dlg.setVerNote("����汾" + versionDir.getName());
		dlg.setIndeterminate(true);
		dlg.setPackProgressString("");
		dlg.setPackNote("ɨ���ļ��У����Ժ�...");
		SourceCodeFileSet parser = new SourceCodeFileSet(versionDir.getAbsolutePath());
		NameTableCreator creator = new NameDefinitionCreator(parser);
		NameTableManager nameTableManager = creator.createNameTableManager();
		
		// �����汾�Ĺ�ģ��������
		structManager = new SoftwareStructManager(nameTableManager);
		SystemScope rootScope = nameTableManager.getSystemScope();
		SystemScopeMeasurement sysMeasurement = new SystemScopeMeasurement(rootScope, structManager);
		
		List<SoftwareMeasure> sizes = getAvailableSoftwareSizeMeasureList();
		
		List<SoftwareMeasure> sizeMeasurements = sysMeasurement.getMeasureList(sizes);
		String metrics = getMetricsString(sizeMeasurements);
		
		dlg.setIndeterminate(false);
		// ��ȡ���ļ��������Թ�������ʹ��
		int progress = nameTableManager.getTotalNumberOfDefinitions(NameDefinitionKind.NDK_TYPE);
		dlg.setPackMaximum(progress);
		
		
		String label = versionDir.getName() + "@"
				+ versionDir.getAbsolutePath();
		VersionNode vNode = new VersionNode(NodeKind.VERSION_NODE, label, metrics);
		parent.add(vNode);
		modifyProjectMetricString(parent, vNode);
		
		
		List<PackageDefinition> packs = nameTableManager.getAllPackageDefinitions();
		loadPackages(packs, vNode);
		
		// �汾�������֮������
		packProgress = 0;
		dlg.setPackProgress(0);
		return vNode;
	}

	private void loadPackages(List<PackageDefinition> packs,
			ProjectTreeNode parent) {
		if (packs != null) {
			ProjectTreeNode packNode;
			for (PackageDefinition pack : packs) {
				if(!cancel) {
	//				infoLabel.setText("  �����"+pack.getSimpleName());
					// ������Ĺ�ģ����ֵ
					dlg.setPackNote("�����"+pack.getSimpleName());
					PackageMeasurement packageMeasure = new PackageMeasurement(pack, structManager);
					List<SoftwareMeasure> sizeMeasures = packageMeasure.getMeasureList(getAvailableSoftwareSizeMeasureList());
					String metrics = getMetricsString(sizeMeasures);
					
					packNode = new PackageNode(NodeKind.PACKAGE_NODE,
							pack.getSimpleName() + "@", metrics);
					parent.add(packNode);
					List<DetailedTypeDefinition> types = pack.getAllDetailedTypeDefinitions();
					loadClasses(types, packNode);
				}
			}
		}
	}


	private void loadClasses(List<DetailedTypeDefinition> types, ProjectTreeNode parent) {
		if (types != null) {
			String classFilePath = null;
			ProjectTreeNode classNode = null;
			List<FieldDefinition> fields = null;
			List<MethodDefinition> methods = null;
			int max = dlg.getPackMaximum();
			for (DetailedTypeDefinition type : types) {
				if(!cancel) { 
	//				infoLabel.setText("  ������"+type.getSimpleName());
	//				progressBar.setValue(progressBar.getValue()+1);
					dlg.setPackNote("������"+type.getSimpleName());
					dlg.setPackProgressString(packProgress + "/" + max);
					dlg.setPackProgress(++ packProgress);
					NameTableManager nameTableManager = structManager.getNameTableManager();
					classFilePath = nameTableManager.getCorrespondingFilePath(type);
					if (new File(classFilePath).exists()) {
						//������Ĺ�ģ����ֵ
						ClassMeasurement classMeasure = new ClassMeasurement(type, structManager);
						List<SoftwareMeasure> sizeMeasure = classMeasure.getMeasureList(getAvailableSoftwareSizeMeasureList());
						String metrics = getMetricsString(sizeMeasure);
						
						classNode = new ClassNode(NodeKind.CLASS_NODE,
								type.getSimpleName() + "@"
										+ type.getLocation().getUniqueId(),
								classFilePath, metrics);
						parent.add(classNode);
						fields = type.getFieldList();
						loadFields(fields, classNode);
	
						methods = type.getMethodList();
						loadMethods(methods, classNode);
					}
				}
			}

		}
	}

	private void loadFields(List<FieldDefinition> fields, ProjectTreeNode parent) {
		if (fields != null) {
			ProjectTreeNode fieldNode = null;
			for (FieldDefinition field : fields) {
				String type = field.getType().getName();
				fieldNode = new FieldNode(NodeKind.FIELD_NODE,
						field.getSimpleName() + "@"
								+ field.getLocation().getUniqueId(), type, 
								getMetricsString(getAvailableSoftwareSizeMeasureList()));
				parent.add(fieldNode);
			}
		}
	}

	private void loadMethods(List<MethodDefinition> methods,
			ProjectTreeNode parent) {
		if (methods != null) {
			ProjectTreeNode methodNode = null;
			for (MethodDefinition method : methods) {
				List<VariableDefinition> vars = method.getParameterList();
				// ���㷽���Ĺ�ģ����ֵ
				MethodMeasurement methodMeasure = new MethodMeasurement(method, structManager);
				List<SoftwareMeasure> sizeMeasures = methodMeasure.getMeasureList(getAvailableSoftwareSizeMeasureList());
				String metrics = getMetricsString(sizeMeasures);
				
				if (vars != null && vars.size() > 0) {
					String[] paramsType = new String[vars.size()];
					for (int i = 0; i < paramsType.length; i++) {
						paramsType[i] = vars.get(i).getType().getName();
					}
					
					methodNode = new MethodNode(NodeKind.METHOD_NODE,
							method.getSimpleName() + "@"
									+ method.getLocation().getUniqueId(),
							paramsType, metrics);
				} else {
					methodNode = new MethodNode(NodeKind.METHOD_NODE,
							method.getSimpleName() + "@"
									+ method.getLocation().getUniqueId(), null, metrics);
				}
				parent.add(methodNode);
			}
		}
	}
	
	// ��size Metric��Ϣ����
	private void modifyProjectMetricString(ProjectTreeNode n, ProjectTreeNode versionNode) {
//		@SuppressWarnings("unchecked")
//		Enumeration<ProjectNode> childNodes = n.children();
		String[] proValues = n.getSizeMetricValue();// �洢Metricֵ
		String[] values = versionNode.getSizeMetricValue();
		for(int i = 0; i < values.length; i++){
			if(proValues[i].equals("N.A")) {// ��ʼ��ProjectNode���Metricֵ
				proValues[i] = values[i];
			} else {
				if(!values[i].equals("N.A")) {// ���߶��г�ʼֵ
					double proValue = Double.valueOf(proValues[i]);
					double value = Double.valueOf(values[i]);
					proValues[i] = proValue + value + "";
				}
			}
		}
		String[] proMetricIds = n.getSizeMetricIdentifier();
		String[] proMetrics = new String[proMetricIds.length];
		for(int i = 0; i<proValues.length; i++) {
			proMetrics[i] = proMetricIds[i] + "=" + proValues[i];
		}
		String result="";
		int index = 0;
		for(; index < proMetrics.length - 1; index++) {
			result += proMetrics[index] + "@";
		}
		result += proMetrics[index];
		((ProjectNode)n).setSizeMetrics(result);
	}
	
	private String getMetricsString(List<SoftwareMeasure> sizeMeasurements) {
		String metrics = "";
		int i=0;
		SoftwareMeasure measurement;
		for(;i < sizeMeasurements.size() - 1; i++) {
			measurement = sizeMeasurements.get(i);
			if(measurement.isUsable()) {
				metrics += measurement.getIdentifier() + "=" + measurement.getValue()+"@";
			} else {
				metrics +=  measurement.getIdentifier() + "=N.A@";
			}
		}
		measurement = sizeMeasurements.get(i);
		if(measurement.isUsable()) {
			metrics  += measurement.getIdentifier()+"="+measurement.getValue();
		} else {
			metrics +=  measurement.getIdentifier() + "=N.A";
		}
		return metrics;
	}
	
	private List<SoftwareMeasure> getAvailableSoftwareSizeMeasureList() {
		SoftwareMeasure[] measures = {
				new SoftwareMeasure(SoftwareMeasureIdentifier.FILE),
				new SoftwareMeasure(SoftwareMeasureIdentifier.PKG),
				new SoftwareMeasure(SoftwareMeasureIdentifier.CLS),
				new SoftwareMeasure(SoftwareMeasureIdentifier.INTF),
				new SoftwareMeasure(SoftwareMeasureIdentifier.ENUM),

//				new SoftwareMeasure(SoftwareMeasureIdentifier.ELOC),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.BLOC),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.CLOC),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.NLOC),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.LOC),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.LOPT),
				
//				new SoftwareMeasure(SoftwareMeasureIdentifier.ALLMTHD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.MTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IHMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.NEWMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.OVMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IMPMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.ALLFLD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IHFLD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.PARS),
				new SoftwareMeasure(SoftwareMeasureIdentifier.LOCV),
				new SoftwareMeasure(SoftwareMeasureIdentifier.STMN),

				new SoftwareMeasure(SoftwareMeasureIdentifier.BYTE),
				new SoftwareMeasure(SoftwareMeasureIdentifier.WORD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.CHAR),
		};
		List<SoftwareMeasure> measureList = new ArrayList<SoftwareMeasure>();
		for (int index = 0; index < measures.length; index++) measureList.add(measures[index]);
		return measureList;
	}


	@Override
	public void cancelButtonIsDown() {
		ProjectTree.canFresh = true;
		ProjectTree.isBuilding = false;
		cancel = true;
	}
}
