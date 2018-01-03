package analyzer.nullCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import graph.basic.GraphNode;
import graph.cfg.CFGNode;
import graph.cfg.CFGNodeType;
import graph.cfg.ControlFlowGraph;
import graph.cfg.ExecutionPoint;
import graph.cfg.creator.CFGCreator;
import nameTable.NameTableASTBridge;
import nameTable.NameTableManager;
import nameTable.creator.NameReferenceCreator;
import nameTable.creator.NameTableCreator;
import nameTable.filter.NameDefinitionKindFilter;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameDefinition.NameDefinition;
import nameTable.nameDefinition.NameDefinitionKind;
import nameTable.nameDefinition.TypeDefinition;
import nameTable.nameReference.MethodReference;
import nameTable.nameReference.NameReference;
import nameTable.nameReference.NameReferenceKind;
import nameTable.nameReference.referenceGroup.NameReferenceGroup;
import nameTable.nameReference.referenceGroup.NameReferenceGroupKind;
import nameTable.nameScope.CompilationUnitScope;
import nameTable.visitor.NameDefinitionVisitor;
import sourceCodeAST.CompilationUnitRecorder;
import sourceCodeAST.SourceCodeFileSet;
import sourceCodeAST.SourceCodeLocation;
import util.Debug;

/**
 * @author Zhou Xiaocong
 * @since 2017Äê7ÔÂ16ÈÕ
 * @version 1.0
 *
 */
public class MethodCallStatementCollector {

	public static void main(String[] args) {
		String rootPath = "E:\\";

		
		String[] paths = {"C:\\QualitasPacking\\recent\\eclipse_SDK\\eclipse_SDK-4.3\\", "C:\\QualitasPacking\\recent\\jfreechart\\jfreechart-1.0.13\\", 
							rootPath + "ZxcWork\\JAnalyzer\\src\\", rootPath + "ZxcTools\\EclipseSource\\org\\", rootPath + "ZxcTemp\\JAnalyzer\\src\\",
							rootPath + "ZxcWork\\ToolKit\\src\\sourceCodeAsTestCase\\CNExample.java", rootPath + "ZxcDeveloping\\OOPAndJavaExamples\\automata\\src\\", 
							rootPath + "ZxcProject\\AspectViz\\ZxcWork\\SortAnimator4\\", rootPath + "ZxcTools\\JDKSource\\", 
							rootPath + "ZxcCourse\\JavaProgramming\\JHotDraw5.2\\sources\\", rootPath + "ZxcWork\\FaultLocalization\\src\\", 
							rootPath + "ZxcTools\\ArgoUml\\", rootPath + "ZxcTools\\jEdit_5_1_0\\", 
							rootPath + "ZxcTools\\lucene_2_0_0\\", rootPath + "ZxcTools\\struts_2_0_1\\",
							rootPath + "ZxcTools\\apache_ant_1_9_3\\src\\", rootPath + "ZxcTools\\apache_ant_1_9_3\\src\\main\\org\\apache\\tools\\ant\\",
		};
		
		String path = paths[2];
		String result = rootPath + "ZxcWork\\ProgramAnalysis\\data\\result.txt";

		PrintWriter writer = new PrintWriter(System.out);
		PrintWriter output = new PrintWriter(System.out);
		
		try {
			writer = new PrintWriter(new FileOutputStream(new File(result)));
		} catch (Exception exc) {
			exc.printStackTrace();
			writer.close();
			return;
		}
		
		try {
			String info = rootPath + "ZxcWork\\ProgramAnalysis\\data\\debug.txt";
			output = new PrintWriter(new FileOutputStream(new File(info)));
			Debug.setWriter(output);
			Debug.setScreenOn();
		} catch (Exception exc) {
			exc.printStackTrace();
			writer.close();
			output.close();
			return;
		}
		
		try {
			Debug.setStart("Begin collection....");
			collectAllMethodCallStatementByScanningMethods(path, writer);
			Debug.time("After collection...");
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		writer.close();
		output.close();
	}

	public static NameTableManager createNameTableManager(String path) {
		SourceCodeFileSet parser = new SourceCodeFileSet(path);
		NameTableCreator creator = new NameTableCreator(parser);
		String[] fileNameArray = {"C:\\ZxcWork\\ToolKit\\data\\javalang.txt", "C:\\ZxcWork\\ToolKit\\data\\javautil.txt", "C:\\ZxcWork\\ToolKit\\data\\javaio.txt", }; 

		Debug.setStart("Begin creating system, path = " + path);
		NameTableManager manager = creator.createNameTableManager(new PrintWriter(System.out), fileNameArray);
		if (creator.hasError()) {
			System.out.println("There are " + creator.getErrorUnitNumber() + " error unit files:");
			creator.printErrorUnitList(new PrintWriter(System.out));
			System.out.println();
		}
		Debug.time("End creating.....");
		Debug.flush();
		
		return manager;
	}
	
	public static void collectAllMethodCallStatementByScanningMethods(String path, PrintWriter writer) {
		NameTableManager manager = createNameTableManager(path);

		NameDefinitionVisitor visitor = new NameDefinitionVisitor(new NameDefinitionKindFilter(NameDefinitionKind.NDK_METHOD));
		manager.accept(visitor);
		List<NameDefinition> methodList = visitor.getResult();
		
		MethodReturnValueAnalyzer returnValueAnalyzer = new MethodReturnValueAnalyzer(manager);
		returnValueAnalyzer.analyze();
		
		int counter = 0;
		int methodCounter = 0;
		StringBuilder message = new StringBuilder("No\tFile\tCaller\tCallRef\tCallee\tLeftValue\tReturnNull\tStatus\tCalleeLocation\tCallRefLocation\tExpression");
		writer.println(message.toString());
		
		for (NameDefinition nameDefinition : methodList) {
//			if (!nameDefinition.getFullQualifiedName().contains("findOutlierLines")) continue;
			
			methodCounter++;
			MethodDefinition method = (MethodDefinition)nameDefinition;
			if (method.isAutoGenerated()) continue; 
			TypeDefinition enclosingType = method.getEnclosingType();
			if (!enclosingType.isDetailedType()) continue;
			DetailedTypeDefinition type = (DetailedTypeDefinition)enclosingType;
			if (type.isAnonymous()) continue;
			
			List<MethodCallExpressionInformation> infoList = collectMethodCallExpressionInformation(manager, method);
			
			for (MethodCallExpressionInformation info : infoList) {
				counter++;
				Debug.println(counter + " Method " + methodCounter + " " + method.getFullQualifiedName() + " call " + info.reference.getName());
				boolean returnNull = false;
				if (info.callee != null) {
//					if (returnValueAnalyzer.isReturnPrimitiveValue(info.callee)) continue;
					if (info.callee.isConstructor()) continue;
					if (returnValueAnalyzer.isPossibleReturnNull(info.callee)) returnNull = true;
				}
					
				message = new StringBuilder();
				message.append(counter + "\t" + info.reference.getLocation().getFileUnitName() + "\t" + method.getSimpleName() + "\t" + info.reference.getName());
				if (info.callee != null) message.append("\t" + info.callee.getSimpleName());
				else message.append("\tUnknown");
				if (info.leftValue != null) message.append("\t" + info.leftValue.getFullQualifiedName());
				else message.append("\tNone");
				if (returnNull) message.append("\tTRUE");
				else message.append("\tFALSE");

				if (returnNull && info.haveChecked) message.append("\tNoramlCheck");
				else if (returnNull && !info.haveChecked) {
					if (info.isUsed) message.append("\tWarnning");
					else message.append("\tUnused");
				} else if (!returnNull && info.haveChecked) message.append("\tRedundant");
				else message.append("\tNormalUnCheck");

				if (info.callee != null) message.append("\t" + info.callee.getLocation().getUniqueId());
				else message.append("\t~~");
				message.append("\t" + info.reference.getLocation().getUniqueId());
//				message.append("\t" + info.reference.getLocation().getUniqueId() + "\t" + info.expression);
				writer.println(message);
			}
		}
		writer.flush();
	}
	
	public static List<MethodCallExpressionInformation> collectMethodCallExpressionInformation(NameTableManager manager, MethodDefinition method) {
		List<MethodCallExpressionInformation> result = new ArrayList<MethodCallExpressionInformation>();

		CompilationUnitScope unitScope = manager.getEnclosingCompilationUnitScope(method);
		String unitFileName = unitScope.getUnitName();
		CompilationUnit astRoot = manager.getSouceCodeFileSet().findSourceCodeFileASTRootByFileUnitName(unitFileName);
		CompilationUnitRecorder unitRecorder = new CompilationUnitRecorder(unitFileName, astRoot);
		SourceCodeFileSet sourceCodeFileSet = manager.getSouceCodeFileSet();
		
		// Create a ControFlowGraph object
		ControlFlowGraph graph = CFGCreator.create(manager, unitRecorder, method);
		if (graph == null) {
			sourceCodeFileSet.releaseAST(unitFileName);
			sourceCodeFileSet.releaseFileContent(unitFileName);
			return result;
		}
		
		List<GraphNode> nodeList = graph.getAllNodes();
		if (nodeList == null) {
			sourceCodeFileSet.releaseAST(unitFileName);
			sourceCodeFileSet.releaseFileContent(unitFileName);
			return result;
		}
		
//		MethodNullCheckCollector.setNullCheckReferenceRecorder(graph);
//		ReachNameAnalyzer.reachNameAnalysis(manager, unitRecorder, method, graph);
//		DominateNodeAnalyzer.dominateNodeAnalysis(graph, method);
//		MethodNullCheckCollector.NullCheckReferenceAnalysis(manager, unitRecorder, method, graph);
		
		NameTableASTBridge bridge = new NameTableASTBridge(manager);
		NameReferenceCreator referenceCreator = new NameReferenceCreator(manager, true);
		
		for (GraphNode node : nodeList) {
			CFGNode cfgNode = (CFGNode)node;
			if (cfgNode.getCFGNodeType() != CFGNodeType.N_EXECUTION_POINT) continue;
			ExecutionPoint exePoint = (ExecutionPoint)cfgNode;
			if (exePoint.isVirtual()) continue;
			
			ASTNode astNode = exePoint.getAstNode();
			if (astNode == null) continue;

			List<NameReference> referenceList = referenceCreator.createReferencesForASTNode(unitFileName, astNode);
			List<NameDefinition> definitionList = bridge.getAllDefinitionsInASTNode(unitFileName, astNode);
			if (referenceList != null) {
				// Collect method call expression in this AST node.
				for (NameReference reference : referenceList) {
					NameDefinition leftValue = null;
					if (astNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION || 
							astNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT || 
									astNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT) {
						// Extract left variable which is in variable declaration and there are method call expression in its initialize expression  
						leftValue = matchLeftValueForReference(definitionList, reference);
					}
					List<MethodCallExpressionInformation> infoList = collectMethodCallExpressionInformationInReference(leftValue, reference);
					for (MethodCallExpressionInformation info : infoList) result.add(info);
				}
				
				// Collect check null predicates in this AST node, and find if a left value in infoList is checked in this AST Node
				if (exePoint.isPredicate()) {
					for (NameReference reference : referenceList) extractCheckNullPredicatesInReference(result, reference);
				}
				
				// collect left value use information in this AST node. If the left value is not used, then it is not need to check it.
				for (NameReference reference : referenceList) extractLeftValueUsingInReference(result, reference);
			}
		}
		
		sourceCodeFileSet.releaseAST(unitFileName);
		sourceCodeFileSet.releaseFileContent(unitFileName);
		return result;
	}

	public static void extractCheckNullPredicatesInReference(List<MethodCallExpressionInformation> infoList, NameReference reference) {
		if (!reference.isGroupReference()) return;
		
		NameReferenceGroup group = (NameReferenceGroup)reference;
		List<NameReference> sublist = group.getSubReferenceList();
		if (group.getGroupKind() == NameReferenceGroupKind.NRGK_INFIX_EXPRESSION) {
			String operator = group.getOperator();
			if (operator.equals(NameReferenceGroup.OPERATOR_EQUALS) || operator.equals(NameReferenceGroup.OPERATOR_NOT_EQUALS)) {
				NameReference firstOperand = sublist.get(0);
				NameReference secondOperand = sublist.get(1);
				
				NameReference checkReference = null;
				if (firstOperand.isNullReference()) {
					checkReference = secondOperand;
				} else if (secondOperand.isNullReference()) {
					checkReference = firstOperand;
				}
				if (checkReference != null) {
					// Not that all reference are be resolved before call this method
					NameDefinition checkDefinition = extractLeftValueInReference(checkReference);
					SourceCodeLocation checkLocation = checkReference.getLocation();
					
					if (checkDefinition != null) {
						for (MethodCallExpressionInformation info : infoList) {
							if (info.leftValue == checkDefinition) {
								SourceCodeLocation callLocation = info.reference.getLocation();
								if (checkLocation.compareTo(callLocation) > 0) {
									info.haveChecked = true;
									return;
								}
							}
						}
					}
				}
			}
		}
		if (sublist != null) {
			for (NameReference subreference : sublist) extractCheckNullPredicatesInReference(infoList, subreference);
		}
	}

	public static void extractLeftValueUsingInReference(List<MethodCallExpressionInformation> infoList, NameReference reference) {
		if (!reference.isGroupReference()) return;
		
		NameReferenceGroup group = (NameReferenceGroup)reference;
		List<NameReference> sublist = group.getSubReferenceList();
		if (group.getGroupKind() == NameReferenceGroupKind.NRGK_FIELD_ACCESS) {
			NameReference objectExpression = sublist.get(0);
			List<NameReference> objectReferenceList = objectExpression.getReferencesAtLeaf();
			for (NameReference objectReference : objectReferenceList) {
				SourceCodeLocation objectUseLocation = objectReference.getLocation();
				NameDefinition objectDefinition = objectReference.getDefinition();
				if (objectDefinition != null) {
					for (MethodCallExpressionInformation info : infoList) {
						if (info.leftValue == objectDefinition) {
							SourceCodeLocation callLocation = info.reference.getLocation();
							if (objectUseLocation.compareTo(callLocation) > 0) {
								info.isUsed = true;
								return;
							}
						}
					}
				}
			}
		} else if (group.getGroupKind() == NameReferenceGroupKind.NRGK_METHOD_INVOCATION) {
			NameReference objectExpression = sublist.get(0);
			if (objectExpression.getReferenceKind() != NameReferenceKind.NRK_METHOD) {
				List<NameReference> objectReferenceList = objectExpression.getReferencesAtLeaf();
				for (NameReference objectReference : objectReferenceList) {
					SourceCodeLocation objectUseLocation = objectReference.getLocation();
					NameDefinition objectDefinition = objectReference.getDefinition();
					if (objectDefinition != null) {
						for (MethodCallExpressionInformation info : infoList) {
							if (info.leftValue == objectDefinition) {
								SourceCodeLocation callLocation = info.reference.getLocation();
								if (objectUseLocation.compareTo(callLocation) > 0) {
									info.isUsed = true;
									return;
								}
							}
						}
					}
				}
			}
			return;
		}
		if (sublist != null) {
			for (NameReference subreference : sublist) extractLeftValueUsingInReference(infoList, subreference);
		}
	}
	
	public static NameDefinition matchLeftValueForReference(List<NameDefinition> definitionList, NameReference reference) {
		if (definitionList == null) return null;
		for (int index = 0; index < definitionList.size(); index++) {
			NameDefinition current = definitionList.get(index);
			SourceCodeLocation currentLocation = current.getLocation();
			SourceCodeLocation referenceLocation = reference.getLocation();
			if (index < definitionList.size()-1) {
				NameDefinition next = definitionList.get(index+1);
				SourceCodeLocation nextLocation = next.getLocation();
				if (referenceLocation.isBetween(currentLocation, nextLocation)) return current;
			} else {
				if (referenceLocation.compareTo(currentLocation) >= 0) return current;
			}
		}
		
		return null;
	}
	
	public static List<MethodCallExpressionInformation> collectMethodCallExpressionInformationInReference(NameDefinition leftValue, NameReference reference) {
		List<MethodCallExpressionInformation> result = new ArrayList<MethodCallExpressionInformation>(); 
		if (!reference.isGroupReference()) return result;
		
		NameReferenceGroup group = (NameReferenceGroup)reference;
		NameReferenceGroupKind groupKind = group.getGroupKind();
		
		group.resolveBinding();
		List<NameReference> referenceAtLeafList = null;
		
		if(groupKind == NameReferenceGroupKind.NRGK_ASSIGNMENT) {
			List<NameReference> sublist = group.getSubReferenceList();
			if (sublist == null) return result;
			
			NameReference leftReference = sublist.get(0);
			NameReference rightReference = sublist.get(1);
			leftValue = extractLeftValueInReference(leftReference);
			
			referenceAtLeafList = rightReference.getReferencesAtLeaf();
		} else {
			referenceAtLeafList = group.getReferencesAtLeaf();
		}
		if (referenceAtLeafList != null && leftValue != null) {
			for (NameReference referenceAtLeaf : referenceAtLeafList) {
				if (referenceAtLeaf.getReferenceKind() == NameReferenceKind.NRK_METHOD) {
					MethodReference methodReference = (MethodReference)referenceAtLeaf;
					List<MethodDefinition> alternativeCalleeList = methodReference.getAlternativeList();
					if (alternativeCalleeList != null) {
						String expression = getFirstLine(group.getName());
						for (MethodDefinition callee : alternativeCalleeList) {
							MethodCallExpressionInformation info = new MethodCallExpressionInformation();
							info.callee = callee;
							info.expression = expression;
							info.leftValue = leftValue;
							info.reference = methodReference;
							result.add(info);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static NameDefinition extractLeftValueInReference(NameReference reference) {
		if (!reference.isGroupReference()) return reference.getDefinition();
		NameReferenceGroup group = (NameReferenceGroup)reference;
		NameReferenceGroupKind groupKind = group.getGroupKind();
		List<NameReference> sublist = group.getSubReferenceList();
		
		if (groupKind == NameReferenceGroupKind.NRGK_ARRAY_ACCESS) {
			return sublist.get(0).getDefinition();
		} else if (groupKind == NameReferenceGroupKind.NRGK_FIELD_ACCESS) {
			return sublist.get(1).getDefinition();
		} else if (groupKind == NameReferenceGroupKind.NRGK_METHOD_INVOCATION ||
				groupKind == NameReferenceGroupKind.NRGK_SUPER_METHOD_INVOCATION) {
			for (NameReference subreference : sublist) {
				if (subreference.getReferenceKind() == NameReferenceKind.NRK_METHOD) {
					return subreference.getDefinition();
				}
			}
		} else if (groupKind == NameReferenceGroupKind.NRGK_SUPER_FIELD_ACCESS) {
			NameReference firstReference = sublist.get(0);
			if (firstReference.getReferenceKind() == NameReferenceKind.NRK_TYPE) 
				return sublist.get(1).getDefinition();
			else return firstReference.getDefinition();
		} else if (groupKind == NameReferenceGroupKind.NRGK_THIS_EXPRESSION) {
			return sublist.get(0).getDefinition();
		} else if (groupKind == NameReferenceGroupKind.NRGK_QUALIFIED_NAME) {
			return group.getDefinition();
		}
		return null;
	}
	
	public static String getFirstLine(String multiLine) {
		int index = multiLine.indexOf('\n');
		if (index < 0) return multiLine;
		else return multiLine.substring(0, index);
	}
}

class MethodCallExpressionInformation {
	String expression = null;
	MethodReference reference = null;
	MethodDefinition callee = null;
	NameDefinition leftValue = null;
	boolean haveChecked = false;
	boolean isUsed = false;
}
