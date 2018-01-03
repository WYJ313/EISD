package graph.callGraph;

import graph.basic.AbstractGraph;
import graph.basic.GraphEdge;
import graph.basic.GraphNode;
import graph.cfg.ControlFlowGraph;
import graph.cfg.ExecutionPoint;
import graph.cfg.creator.CFGCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.CompilationUnit;

import softwareStructure.SoftwareStructManager;
import sourceCodeAST.SourceCodeFileSet;
import nameTable.NameTableASTBridge;
import nameTable.NameTableManager;
import nameTable.creator.NameReferenceCreator;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameReference.MethodReference;
import nameTable.nameReference.NameReference;
import nameTable.nameReference.referenceGroup.NRGClassInstanceCreation;
import nameTable.nameReference.referenceGroup.NameReferenceGroup;
import nameTable.nameScope.CompilationUnitScope;
import nameTable.nameScope.SystemScope;


/**
 * ���ɷ�������ͼ����ԭ����Ļ����ϣ����ӻ����� SoftwareStructManager ���ɷ�������ͼ�ķ���
 * 
 * @author Zhou Xiaocong
 * @update 2016��4��14��
 * @version 2.0
 */
public class CallGraphCreator 
{
	
	/**
	 * ����Դ���봴������ͼ
	 * @param path Դ�����·��
	 * @return ���ش����õĵ���ͼ
	 */
	public static AbstractGraph create(NameTableManager manager, SourceCodeFileSet parser, String id) {
		AbstractGraph graph = new CallGraph(id);
		
		// ��������ͼ����ͱ�
		List<GraphNode> nodes = creatNodes(manager.getSystemScope());
		List<GraphEdge> edges = createEdges(manager, parser);
		// ���õ���ͼ����ͱ�
		graph.setNodes(nodes);
		graph.setEdges(edges);
		//System.out.println(nodes.indexOf(edges.get(0).getStartNode()));
		return graph;
	}
	

	/**
	 * ����һ������ʵ�ֵķ���֮��ķ�������ͼ
	 */
	public static AbstractGraph create(String graphId, SoftwareStructManager manager, DetailedTypeDefinition type, boolean isPolymorphic) {
		List<MethodDefinition> implementedMethodList = manager.getImplementedMethodList(type);
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		for (MethodDefinition method : implementedMethodList) {
			nodeList.add(new CallGraphNode(method));
		}
		return create(graphId, manager, nodeList, isPolymorphic);
	}
	
	/**
	 * ����һ������ʵ�ֵķ���֮��ķ�������ͼ
	 */
	public static AbstractGraph create(String graphId, SoftwareStructManager manager, DetailedTypeDefinition type, boolean isPolymorphic, int maxCallDepth) {
		List<MethodDefinition> implementedMethodList = manager.getImplementedMethodList(type);
		TreeSet<MethodDefinition> methodNodeSet = new TreeSet<MethodDefinition>();
		for (MethodDefinition method : implementedMethodList) {
			methodNodeSet.add(method);
			Set<MethodDefinition> calledMethodSet = null;
			if (isPolymorphic) calledMethodSet = manager.getPolymorphicInvocationMethodSet(method, maxCallDepth);
			else calledMethodSet = manager.getStaticInvocationMethodSet(method, maxCallDepth);
			for (MethodDefinition calledMethod : calledMethodSet) methodNodeSet.add(calledMethod);
		}
		
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		for (MethodDefinition method : methodNodeSet) {
			nodeList.add(new CallGraphNode(method));
		}
		return create(graphId, manager, nodeList, isPolymorphic);
	}

	/**
	 * ����һ�������������������Ϊ maxCallDepth �����з���֮��ĵ���ͼ
	 */
	public static AbstractGraph create(String graphId, SoftwareStructManager manager, MethodDefinition method, boolean isPolymorphic, int maxCallDepth) {
		TreeSet<MethodDefinition> methodNodeSet = new TreeSet<MethodDefinition>();
		methodNodeSet.add(method);
		Set<MethodDefinition> calledMethodSet = null;
		if (isPolymorphic) calledMethodSet = manager.getPolymorphicInvocationMethodSet(method, maxCallDepth);
		else calledMethodSet = manager.getStaticInvocationMethodSet(method, maxCallDepth);
		for (MethodDefinition calledMethod : calledMethodSet) methodNodeSet.add(calledMethod);
		
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		for (MethodDefinition methodNode : methodNodeSet) {
			nodeList.add(new CallGraphNode(methodNode));
		}
		return create(graphId, manager, nodeList, isPolymorphic);
	}
	
	/**
	 * ����ָ���ķ�������ͼ�ڵ��б� nodeList ��������Щ�ڵ�֮��ķ������ù�ϵ����� isPolymorphic Ϊ true�� �����ڷ���֮��Ķ�̬���ù�ϵ���ɵ���ͼ��������ڷ���֮��
	 * �ľ�̬���ù�ϵ���ɵ���ͼ��
	 */
	private static CallGraph create(String graphId, SoftwareStructManager manager, List<GraphNode> nodeList, boolean isPolymorphic) {
		CallGraph result = new CallGraph(graphId);

		List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
		
		for (GraphNode node : nodeList) {
			// ���Ǽٶ��� nodeList �еĽڵ㶼�� CallGraphNode
			CallGraphNode callerNode = (CallGraphNode)node;
			MethodDefinition caller = callerNode.getMethodDefinition();
			List<MethodDefinition> calleeList = null;
			if (isPolymorphic) calleeList = manager.getDirectPolymorphicInvocationMethodList(caller);
			else calleeList = manager.getDirectStaticInvocationMethodList(caller);
			
			if (calleeList != null) {
				for (MethodDefinition callee : calleeList) {
					CallGraphNode calleeNode = findMethodInNodeList(callee, nodeList);
					CallGraphEdge edge = new CallGraphEdge(callerNode, calleeNode);
					if (calleeNode != null) edgeList.add(edge);
				}
			}
		}
		
		result.setNodes(nodeList);
		result.setEdges(edgeList);
		return result;
	}
	
	private static CallGraphNode findMethodInNodeList(MethodDefinition method, List<GraphNode> nodeList) {
		for (GraphNode node : nodeList) {
			CallGraphNode callNode = (CallGraphNode)node;
			if (callNode.getMethodDefinition() == method) return callNode;
		}
		return null;
	}
	
	/**
	 * ��������ͼ�Ķ��㣺�Ȼ�����е�DetailedTypeDefinition��Ȼ���������еķ�������Ϊ����
	 * @param scope ϵͳ�ռ�
	 * @return �������ж��㹹�ɵ��б�
	 */
	private static List<GraphNode> creatNodes(SystemScope scope) 
	{		
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		
		List<DetailedTypeDefinition> types = scope.getAllDetailedTypeDefinitions();
		for(DetailedTypeDefinition type : types) {
			List<MethodDefinition> methods = type.getMethodList();
			if(methods != null) {
				for(MethodDefinition method : methods) {
					// ���˵����췽��
					if (!method.getSimpleName().equals(type.getSimpleName())) {
						CallGraphNode node = new CallGraphNode(method);
						nodes.add(node);
					}
					if (method.getSimpleName().equals(type.getSimpleName())) {
						//System.out.println(method.getFullQualifiedName());
					}
				}
			}
		}
		
		/*for(GraphNode n : nodes)
		{
			System.out.println(n.getId());
			
		}*/
		return nodes;
	}
	/**
	 * ��������ͼ�ıߣ�ͨ��ɨ�����п�����ͼ�еĿ�ִ�е���д���
	 * @param manager ���ֱ�
	 * @param parser ������Դ��������ɳ����﷨��
	 * @return �������б߹��ɵ��б�
	 */
	private static List<GraphEdge> createEdges(NameTableManager manager, SourceCodeFileSet parser) 
	{		
		List<GraphEdge> edges = new ArrayList<GraphEdge>();
		
		List<CompilationUnitScope> units = manager.getAllCompilationUnitScopes();
		
		if (units == null || units.size() <= 0)  {
			return null;
		}
		System.out.println("Number of CompilationUnit: " + units.size());
		String systemPath = manager.getSystemPath();
		for (CompilationUnitScope unit : units) {
			String unitFileName = unit.getUnitName();
			
			CompilationUnit astRoot = parser.findSourceCodeFileASTRootByFileUnitName(unitFileName);
			if (astRoot == null) {
				throw new AssertionError("Can not find the compilation unit for the file: " + (systemPath + unitFileName));
			}
			
			CFGCreator cfgCreator = new CFGCreator(unitFileName, astRoot);
			List<ControlFlowGraph> cfgs = cfgCreator.create();		
			if (cfgs == null) return null;

			for (ControlFlowGraph cfg : cfgs) {
				// �����ǹ��췽��
				if(cfg.getMethod().isConstructor()) {
					//System.out.println("isConstructor");
					continue;
				}
				// ��ȡ���÷����ڵ㣬��������
				NameTableASTBridge bridge = new NameTableASTBridge(manager);
				MethodDefinition methodDefinition = bridge.findDefinitionForMethodDeclaration(unitFileName, cfg.getMethod()); 
				GraphNode caller = new CallGraphNode(methodDefinition);
				List<GraphNode> possibleCallees = null;
				
				List<GraphNode> nodes = cfg.getAllNodes();
				if (nodes != null) {
					for (GraphNode node : nodes) {
						ExecutionPoint point = (ExecutionPoint)node;
						if (point.isVirtual()) continue;
						else {
							NameReferenceCreator referenceCreator = new NameReferenceCreator(manager);
							List<NameReference> referenceList = referenceCreator.createReferencesForASTNode(unitFileName, point.getAstNode());
							for (NameReference reference : referenceList) {
								reference.resolveBinding();
								// ��ȡһ�����õ���ܵı����÷����ڵ㣬�����ܵı�������
								possibleCallees = getPossibleCallees(reference);
								
								if(possibleCallees != null) {
									for (GraphNode possibleCallee : possibleCallees) {
										GraphEdge edge = new CallGraphEdge((CallGraphNode)caller, (CallGraphNode)possibleCallee);
										if(!edges.contains(edge)) {
											edges.add(edge);
										}
									}
								}
							}
						}
					}
				}
			}
			parser.releaseFileContent(unitFileName);
			parser.releaseAST(unitFileName);
		}
		return edges;
	}

	/**
	 * �������������Ի��һ�����û��������п����漰�ı����÷���
	 * @param reference ���������
	 * @return ���ؿ����漰�ı����÷������б�
	 */
	private static List<GraphNode> getPossibleCallees(NameReference reference) 
	{
		List<GraphNode> possibleCallees = new ArrayList<GraphNode>();
		List<MethodReference> methodReferences = new ArrayList<MethodReference>();
		
		if (reference.isGroupReference()) {
			List<NameReference> referencesInGroup = ((NameReferenceGroup)reference).getReferencesAtLeaf();
			for (NameReference ref : referencesInGroup) {
				if (ref.isMethodReference()) {
					methodReferences.add((MethodReference)ref);
				}
			}
		} else if (reference.isMethodReference()) {
			methodReferences.add((MethodReference)reference);
		} else return null;
		
		for(MethodReference methodRef : methodReferences) {
			List<MethodDefinition> methodList = methodRef.getAlternativeList();
			if (methodList != null && methodList.size() >= 1) {
				for (MethodDefinition method : methodList) {
					possibleCallees.add(new CallGraphNode(method));
				}
			}
		}
		return possibleCallees;
	}
	
	/**
	 * Return all Class Instance Creation References in the group
	 */
	public static List<NameReference> getAllClassInstanceCreationReferences(NameReferenceGroup nameReferenceGroup) 
	{
		List<NameReference> result = new ArrayList<NameReference>();
		if(nameReferenceGroup instanceof NRGClassInstanceCreation) {
			result.add(nameReferenceGroup);
		}
		
		List<NameReference> subreferences = nameReferenceGroup.getSubReferenceList();
		if (subreferences == null) {
			return result;
		}
		
		for (NameReference reference : subreferences) {
			if (reference.isGroupReference()) {
				NameReferenceGroup group = (NameReferenceGroup)reference;
				List<NameReference> referencesInGroup = getAllClassInstanceCreationReferences(group);
				result.addAll(referencesInGroup);
			} else continue;
		}
		return result;
	}
}

