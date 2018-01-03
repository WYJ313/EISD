package analyzer.nullCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import analyzer.dataTable.DataTableManager;
import graph.basic.GraphNode;
import graph.cfg.ControlFlowGraph;
import graph.cfg.ExecutionPoint;
import graph.cfg.analyzer.ReachNameDefinition;
import graph.cfg.analyzer.DominateNodeAnalyzer;
import graph.cfg.analyzer.ReachNameAnalyzer;
import graph.cfg.creator.CFGCreator;
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
import nameTable.nameReference.TypeReference;
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
 * @since 2017Äê8ÔÂ13ÈÕ
 * @version 1.0
 *
 */
public class ObjectReferenceCollector {

	public ObjectReferenceCollector() {
	}

	public static void main(String[] args) {
		String rootPath = "C:\\";

		
		String[] paths = {"C:\\QualitasPacking\\recent\\eclipse_SDK\\eclipse_SDK-4.3\\", "C:\\QualitasPacking\\recent\\jfreechart\\jfreechart-1.0.13\\", 
							rootPath + "ZxcWork\\JAnalyzer\\src\\", rootPath + "ZxcTools\\EclipseSource\\org\\", rootPath + "ZxcTemp\\JAnalyzer\\src\\",
							rootPath + "ZxcWork\\ToolKit\\src\\sourceCodeAsTestCase\\TestGenericType.java", rootPath + "ZxcDeveloping\\OOPAndJavaExamples\\automata\\src\\", 
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
			String info = rootPath + "ZxcWork\\ProgramAnalysis\\data\\debug.txt";
			output = new PrintWriter(new FileOutputStream(new File(info)));
			Debug.setScreenOn();
		} catch (Exception exc) {
			exc.printStackTrace();
			output.close();
			return;
		}
		
		try {
			writer = new PrintWriter(new FileOutputStream(new File(result)));
			Debug.setStart("Begin collection....");
			ObjectReferenceCollector collector = new ObjectReferenceCollector();
//			collector.collectAllObjectReferenceExpressions(path, writer);
			collector.printObjectReferenceKind(path, writer);
			Debug.time("After collection...");
			writer.close();
			
//			Debug.setWriter(output);
//			Debug.setStart("Begin check consistence....");
//			checkObjectReferenceNullCheckConsistence(result);
//			Debug.time("After check consistence....");
			output.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void checkObjectReferenceNullCheckConsistence(String file) throws IOException {
		DataTableManager manager = new DataTableManager("result");
		manager.read(file, true);
		
		int lineNumber = manager.getLineNumber();
		int index = 0;
		
		while (index < lineNumber) {
			String className = manager.getCellValueAsString(index, "Class");
			String methodName = manager.getCellValueAsString(index, "Method");
			
			String reference = manager.getCellValueAsString(index, "ObjRef");
			int locationIndex = reference.indexOf(']');
			String referenceName = reference.substring(locationIndex);
			String definition = manager.getCellValueAsString(index, "BoundDef");
			
			String isChecked = manager.getCellValueAsString(index, "IsChecked");
			
			int startIndex = index;
			int checkIndex = index + 1;
			
			System.out.println("Check " + index);
			
			boolean same = true;
			while (checkIndex < lineNumber) {
				String checkClassName = manager.getCellValueAsString(checkIndex, "Class");
				String checkMethodName = manager.getCellValueAsString(checkIndex, "Method");
				if (checkClassName.equals(className) && checkMethodName.equals(methodName)) {
					
					String checkDefinition = manager.getCellValueAsString(checkIndex, "BoundDef");
					if (!checkDefinition.equals(definition)) break;
					
					String checkReference = manager.getCellValueAsString(checkIndex, "ObjRef");
					locationIndex = checkReference.indexOf(']');
					String checkReferenceName = checkReference.substring(locationIndex);

					if (checkDefinition.equals("[~]~")) {
						if (!checkReferenceName.equals(referenceName)) break;;
					}

					String isLeftValue = manager.getCellValueAsString(checkIndex, "IsLeftValue");
					if (isLeftValue.equals("TRUE")) {
						checkIndex = checkIndex + 1;
						continue;
					}
					
					String checkIsChecked = manager.getCellValueAsString(checkIndex, "IsChecked");
					
					if (!checkIsChecked.equals(isChecked)) {
						same = false;
						break;
					}
				} else break;
				checkIndex = checkIndex + 1;
			}
			
			if (!same) {
				Debug.println("Class " + className + ", Method " + methodName + ", bounded definition " + definition);
				StringBuilder checkedDereference = new StringBuilder();
				StringBuilder uncheckedDereference = new StringBuilder();
				StringBuilder checkedNondereference = new StringBuilder();
				StringBuilder uncheckedNondereference = new StringBuilder();
				
				checkIndex = startIndex;
				while (checkIndex < lineNumber) {
					String checkClassName = manager.getCellValueAsString(checkIndex, "Class");
					String checkMethodName = manager.getCellValueAsString(checkIndex, "Method");
					if (checkClassName.equals(className) && checkMethodName.equals(methodName)) {
						
						String checkDefinition = manager.getCellValueAsString(checkIndex, "BoundDef");
						if (!checkDefinition.equals(definition)) break;
						
						String checkReference = manager.getCellValueAsString(checkIndex, "ObjRef");
						locationIndex = checkReference.indexOf(']');
						String checkReferenceName = checkReference.substring(locationIndex);

						if (checkDefinition.equals("[~]~")) {
							if (!checkReferenceName.equals(referenceName)) break;;
						}
						
						String checkIsChecked = manager.getCellValueAsString(checkIndex, "IsChecked");
						String checkIsDereference = manager.getCellValueAsString(checkIndex, "IsDereference");
						
						String isLeftValue = manager.getCellValueAsString(checkIndex, "IsLeftValue");
						if (isLeftValue.equals("TRUE")) {
							checkIndex = checkIndex + 1;
							continue;
						}
						
						if (checkIsChecked.equals("TRUE") && checkIsDereference.equals("TRUE")) checkedDereference.append("\t\t" + checkReference + "\r\n");
						else if (checkIsChecked.equals("FALSE") && checkIsDereference.equals("TRUE")) uncheckedDereference.append("\t\t" + checkReference + "\r\n");
						else if (checkIsChecked.equals("TRUE") && checkIsDereference.equals("FALSE")) checkedNondereference.append("\t\t" + checkReference + "\r\n");
						else if (checkIsChecked.equals("FALSE") && checkIsDereference.equals("FALSE")) uncheckedNondereference.append("\t\t" + checkReference + "\r\n");
					} else break;
					checkIndex = checkIndex + 1;
				}
				
				if (checkedDereference.length() > 0) {
					Debug.println("\tChecked Dereference: ");
					Debug.println(checkedDereference.toString());
				}
				if (uncheckedDereference.length() > 0) {
					Debug.println("\tUnchecked Dereference: ");
					Debug.println(uncheckedDereference.toString());
				}
				if (checkedNondereference.length() > 0) {
					Debug.println("\tChecked Non-Dereference: ");
					Debug.println(checkedNondereference.toString());
				}
				if (uncheckedNondereference.length() > 0) {
					Debug.println("\tUnchecked Non-Dereference: ");
					Debug.println(uncheckedNondereference.toString());
				}
			}
			index = checkIndex;
		}
	}
	
	public static void checkObjectReferenceNullCheckConsistence2(String file) throws IOException {
		DataTableManager manager = new DataTableManager("result");
		manager.read(file, true);
		
		Debug.println("Result\tClass\tMethod\tReference\tCheckReferece\tReferenceCheck\tCheckReferenceCheck\tReferenceDeference\tCheckReferenceDeference");
		int lineNumber = manager.getLineNumber();
		for (int index = 0; index < lineNumber; index++) {
			String className = manager.getCellValueAsString(index, "Class");
			String methodName = manager.getCellValueAsString(index, "Method");
			
			String reference = manager.getCellValueAsString(index, "ObjRef");
			int locationIndex = reference.indexOf(']');
			String referenceName = reference.substring(locationIndex);
			String definition = manager.getCellValueAsString(index, "BoundDef");
			
			String isChecked = manager.getCellValueAsString(index, "IsChecked");
			String isDereference = manager.getCellValueAsString(index, "IsDereference");
			
			boolean hasCheckedMethod = false;
			System.out.println("Check " + index);
			
			for (int checkIndex = index+1; checkIndex < lineNumber; checkIndex++) {
				String checkClassName = manager.getCellValueAsString(checkIndex, "Class");
				String checkMethodName = manager.getCellValueAsString(checkIndex, "Method");
				if (checkClassName.equals(className) && checkMethodName.equals(methodName)) {
					hasCheckedMethod = true;
					
					String checkDefinition = manager.getCellValueAsString(checkIndex, "BoundDef");
					if (!checkDefinition.equals(definition)) continue;
					
					String checkReference = manager.getCellValueAsString(checkIndex, "ObjRef");
					locationIndex = checkReference.indexOf(']');
					String checkReferenceName = checkReference.substring(locationIndex);

					if (checkDefinition.equals("[~]~")) {
						if (!checkReferenceName.equals(referenceName)) continue;
					}
					
					String checkIsChecked = manager.getCellValueAsString(checkIndex, "IsChecked");
					String checkIsDereference = manager.getCellValueAsString(checkIndex, "IsDereference");
					
					if (!checkIsChecked.equals(isChecked)) {
						if (isDereference.equals("TRUE") && checkIsDereference.equals("TRUE")) {
							Debug.println("Error\t" + className + "\t" + methodName + "\t" + reference + "\t" + checkReference + "\t" + isChecked + "\t" + checkIsChecked + "\t" + isDereference + "\t" + checkIsDereference);
						} else {
							Debug.println("Warning\t" + className + "\t" + methodName + "\t" + reference + "\t" + checkReference + "\t" + isChecked + "\t" + checkIsChecked + "\t" + isDereference + "\t" + checkIsDereference);
						}
					}
					
				} else if (hasCheckedMethod) {
					break;
				}
			}
		}
	}
	
	
	public void printObjectReferenceKind(String path, PrintWriter writer) {
		NameTableManager manager = NameTableManager.createNameTableManager(path);
		SourceCodeFileSet sourceCodeFileSet = manager.getSouceCodeFileSet();

		NameDefinitionVisitor visitor = new NameDefinitionVisitor(new NameDefinitionKindFilter(NameDefinitionKind.NDK_METHOD));
		manager.accept(visitor);
		List<NameDefinition> methodList = visitor.getResult();

		int counter = 0;
		int methodCounter = 0;
		StringBuilder message = new StringBuilder("No\tClass\tMethod\tObjRef\tBoundDef\tIsResolved\tRefKind\tValueTypeRef\tValueTypeDef\tBoundDefType");
		writer.println(message.toString());
		
		NameReferenceCreator referenceCreator = new NameReferenceCreator(manager, true);
		
		for (NameDefinition nameDefinition : methodList) {
//			if (!nameDefinition.getFullQualifiedName().contains("ContextInjectionFactory.make")) continue;
			
			methodCounter++;
			MethodDefinition method = (MethodDefinition)nameDefinition;
			if (method.isAutoGenerated()) continue; 
			TypeDefinition enclosingType = method.getEnclosingType();
			if (!enclosingType.isDetailedType()) continue;
			DetailedTypeDefinition type = (DetailedTypeDefinition)enclosingType;
			if (type.isAnonymous()) continue;

			Debug.println("Method " + methodCounter + ": " + method.getFullQualifiedName());
			
			CompilationUnitScope unitScope = manager.getEnclosingCompilationUnitScope(method);
			String unitFileName = unitScope.getUnitName();
			CompilationUnit astRoot = manager.getSouceCodeFileSet().findSourceCodeFileASTRootByFileUnitName(unitFileName);
			CompilationUnitRecorder unitRecorder = new CompilationUnitRecorder(unitFileName, astRoot);
			
			// Create a ControFlowGraph object
			ControlFlowGraph currentCFG = CFGCreator.create(manager, unitRecorder, method);
			if (currentCFG == null) {
				sourceCodeFileSet.releaseAST(unitFileName);
				sourceCodeFileSet.releaseFileContent(unitFileName);
				continue;
			}
			
			List<GraphNode> nodeList = currentCFG.getAllNodes();
			for (GraphNode graphNode : nodeList) {
				ExecutionPoint node = (ExecutionPoint)graphNode;
				if (node.isVirtual()) continue;
				
				ASTNode astNode = node.getAstNode();
				if (astNode == null) continue;
				List<NameReference> referenceList = referenceCreator.createReferencesForASTNode(unitFileName, astNode);
				if (referenceList == null) continue;
				
				for (NameReference reference : referenceList) {
					reference.resolveBinding();
					boolean isDereference = false;
					// Those expressions in enhanced for statement predicate are regarded as dereference if they are object references 
					if (node.isEnhancedForPredicate()) isDereference = true;
					List<ObjectReferenceRecorder> objectReferenceList = getObjectReferenceListInExpression(reference, isDereference);
					
					for (ObjectReferenceRecorder objectReferenceRecorder : objectReferenceList) {
						NameReference objectReference = objectReferenceRecorder.reference;
						String referenceName = objectReference.toSimpleString();
						String referenceLocation = objectReference.getLocation().toString();
						
						String valueType = "~";
						TypeReference valueTypeReference = objectReference.getResultTypeReference();
						if (valueTypeReference != null) {
							valueType = valueTypeReference.getName();
							if (valueTypeReference.isArrayType()) {
								int dimension = valueTypeReference.getDimension();
								for (int i = 0; i < dimension; i++) valueType += "[]";
							}
						}
						String valueTypeDef = "~";
						TypeDefinition valueTypeDefinition = objectReference.getResultTypeDefinition();
						if (valueTypeDefinition != null) {
							valueTypeDef = valueTypeDefinition.getSimpleName();
						}
						
						String boundDefinition = "~";
						String boundDefinitionType = "~";
						String boundDefinitionLocation = "~";
						String referenceKind = objectReference.getReferenceKind().toString();
						
						if (objectReference.getReferenceKind() == NameReferenceKind.NRK_GROUP) {
							NameReferenceGroup group = (NameReferenceGroup)objectReference;
							boundDefinitionType = group.getGroupKind().toString();
						} else {
							NameDefinition definition = objectReference.getDefinition();
							if (definition != null) {
								boundDefinition = definition.getSimpleName();
								SourceCodeLocation location = definition.getLocation();
								if (location != null) boundDefinitionLocation = location.toString();
								boundDefinitionType = definition.getDefinitionKind().toString();
							}
						}
						counter++;
						writer.println(counter + "\t" + type.getSimpleName() + "\t[" + method.getLocation() + "]" + method.getSimpleName() + "\t[" + referenceLocation + "]" + referenceName + "\t[" + boundDefinitionLocation + "]" + boundDefinition + "\t" + objectReference.isResolved() + "\t" + referenceKind + "\t" + valueType + "\t" + valueTypeDef + "\t" + boundDefinitionType);
					}
				}
			}
			sourceCodeFileSet.releaseAST(unitFileName);
			sourceCodeFileSet.releaseFileContent(unitFileName);
		}
		writer.flush();
	}
	
	public void collectAllObjectReferenceExpressions(String path, PrintWriter writer) {
		NameTableManager manager = NameTableManager.createNameTableManager(path);
		SourceCodeFileSet sourceCodeFileSet = manager.getSouceCodeFileSet();

		NameDefinitionVisitor visitor = new NameDefinitionVisitor(new NameDefinitionKindFilter(NameDefinitionKind.NDK_METHOD));
		manager.accept(visitor);
		List<NameDefinition> methodList = visitor.getResult();

		int counter = 0;
		int methodCounter = 0;
		StringBuilder message = new StringBuilder("No\tClass\tMethod\tObjRef\tIsChecked\tIsDereference\tIsLeftValue\tBoundDef\tIsAssigned\tFirstAssignedValue\tOtherAssignedValue\tIsPropagated\tFirstRootValue\tOtherRootValue\tIsResolved\tRefKind\tValueTypeRef\tValueTypeDef\tBoundDefType");
		writer.println(message.toString());
		
		NameReferenceCreator referenceCreator = new NameReferenceCreator(manager, true);
		
		for (NameDefinition nameDefinition : methodList) {
//			if (!nameDefinition.getFullQualifiedName().contains("ContextInjectionFactory.make")) continue;
			
			methodCounter++;
			MethodDefinition method = (MethodDefinition)nameDefinition;
			if (method.isAutoGenerated()) continue; 
			TypeDefinition enclosingType = method.getEnclosingType();
			if (!enclosingType.isDetailedType()) continue;
			DetailedTypeDefinition type = (DetailedTypeDefinition)enclosingType;
			if (type.isAnonymous()) continue;

			Debug.println("Method " + methodCounter + ": " + method.getFullQualifiedName());
			
			CompilationUnitScope unitScope = manager.getEnclosingCompilationUnitScope(method);
			String unitFileName = unitScope.getUnitName();
			CompilationUnit astRoot = manager.getSouceCodeFileSet().findSourceCodeFileASTRootByFileUnitName(unitFileName);
			CompilationUnitRecorder unitRecorder = new CompilationUnitRecorder(unitFileName, astRoot);
			
			// Create a ControFlowGraph object
			ControlFlowGraph currentCFG = CFGCreator.create(manager, unitRecorder, method);
			if (currentCFG == null) {
				sourceCodeFileSet.releaseAST(unitFileName);
				sourceCodeFileSet.releaseFileContent(unitFileName);
				continue;
			}
			MethodNullCheckCollector.setNullCheckReferenceRecorder(currentCFG);
			ReachNameAnalyzer.reachNameAnalysis(manager, unitRecorder, method, currentCFG);
			DominateNodeAnalyzer.dominateNodeAnalysis(currentCFG, method);
			MethodNullCheckCollector.NullCheckReferenceAnalysis(manager, unitRecorder, method, currentCFG);
			
			List<GraphNode> nodeList = currentCFG.getAllNodes();
			for (GraphNode graphNode : nodeList) {
				ExecutionPoint node = (ExecutionPoint)graphNode;
				if (node.isVirtual()) continue;
				
				ASTNode astNode = node.getAstNode();
				if (astNode == null) continue;
				List<NameReference> referenceList = referenceCreator.createReferencesForASTNode(unitFileName, astNode);
				if (referenceList == null) continue;
				
				for (NameReference reference : referenceList) {
					reference.resolveBinding();
					boolean isDereference = false;
					// Those expressions in enhanced for statement predicate are regarded as dereference if they are object references 
					if (node.isEnhancedForPredicate()) isDereference = true;
					List<ObjectReferenceRecorder> objectReferenceList = getObjectReferenceListInExpression(reference, isDereference);
					
					for (ObjectReferenceRecorder objectReferenceRecorder : objectReferenceList) {
						NameReference objectReference = objectReferenceRecorder.reference;
						String referenceName = objectReference.toSimpleString();
						String referenceLocation = objectReference.getLocation().toString();
						
						String valueType = "~";
						TypeReference valueTypeReference = objectReference.getResultTypeReference();
						if (valueTypeReference != null) {
							valueType = valueTypeReference.getName();
							if (valueTypeReference.isArrayType()) {
								int dimension = valueTypeReference.getDimension();
								for (int i = 0; i < dimension; i++) valueType += "[]";
							}
						}
						String valueTypeDef = "~";
						TypeDefinition valueTypeDefinition = objectReference.getResultTypeDefinition();
						if (valueTypeDefinition != null) {
							valueTypeDef = valueTypeDefinition.getSimpleName();
						}
						
						String boundDefinition = "~";
						String boundDefinitionType = "~";
						String boundDefinitionLocation = "~";
						String referenceKind = objectReference.getReferenceKind().toString();
						
						String firstValueString = "~~";
						String otherValueString = "~~";
						String firstRootValue = "~~";
						String otherRootValue = "~~";
						boolean assigned = false;
						boolean propagated = false;
						boolean isChecked = false;

						if (objectReference.getReferenceKind() == NameReferenceKind.NRK_GROUP) {
							NameReferenceGroup group = (NameReferenceGroup)objectReference;
							boundDefinitionType = group.getGroupKind().toString();
						} else {
							NameDefinition definition = objectReference.getDefinition();
							if (definition != null) {
								boundDefinition = definition.getSimpleName();
								SourceCodeLocation location = definition.getLocation();
								if (location != null) boundDefinitionLocation = location.toString();
								boundDefinitionType = definition.getDefinitionKind().toString();

								List<ReachNameDefinition> definedNameList = ReachNameAnalyzer.getReachNameDefinitionList(currentCFG, node, objectReference);
								for (ReachNameDefinition definedName : definedNameList) {
									assigned = true;
									NameReference value = definedName.getValue();
									if (value != null) {
										if (firstValueString.equals("~~")) {
											firstValueString = "[" + value.getLocation() + "]" + value.toSimpleString();
										} else if (otherValueString.equals("~~")) {
											otherValueString = "[" + value.getLocation() + "]" + value.toSimpleString();
										} else {
											otherValueString = otherValueString + ";~" + "[" + value.getLocation() + "]" + value.toSimpleString();
										}
									}
								}
								
								List<ReachNameDefinition> rootDefinedNameList = ReachNameAnalyzer.getRootReachNameDefinitionList(currentCFG, node, objectReference);
								if (definedNameList.containsAll(rootDefinedNameList)) propagated = false;
								else propagated = true;
								
								for (ReachNameDefinition rootDefinedName : rootDefinedNameList) {
									NameReference value = rootDefinedName.getValue();
									if (value != null) {
										if (firstRootValue.equals("~~")) {
											firstRootValue = "[" + value.getLocation() + "]" + value.toSimpleString();
										} else if (otherRootValue.equals("~~")) {
											otherRootValue = "[" + value.getLocation() + "]" + value.toSimpleString();
										} else {
											otherRootValue = otherRootValue + ";~" + "[" + value.getLocation() + "]" + value.toSimpleString();
										}
									}
								}
							}
							isChecked = isCheckedObjectReferenceInNode(node, objectReference);
						}
						counter++;
						writer.println(counter + "\t" + type.getSimpleName() + "\t[" + method.getLocation() + "]" + method.getSimpleName() + "\t[" + referenceLocation + "]" + referenceName + "\t" + isChecked + "\t" + objectReferenceRecorder.isDereference + "\t" + objectReference.isLeftValue() + "\t[" + boundDefinitionLocation + "]" + boundDefinition + "\t" + assigned + "\t" + firstValueString + "\t" + otherValueString + "\t" + propagated + "\t" + firstRootValue + "\t" + otherRootValue + "\t" + objectReference.isResolved() + "\t" + referenceKind + "\t" + valueType + "\t" + valueTypeDef + "\t" + boundDefinitionType);
					}
				}
			}
			sourceCodeFileSet.releaseAST(unitFileName);
			sourceCodeFileSet.releaseFileContent(unitFileName);
		}
		writer.flush();
	}
	
	public boolean isCheckedObjectReferenceInNode(ExecutionPoint currentNode, NameReference reference) {
		NullCheckReferenceRecorder recorder = (NullCheckReferenceRecorder)currentNode.getFlowInfoRecorder();
		List<GraphNode> dominateNodeList = recorder.getDominateNodeList();
		NameDefinition definition = reference.getDefinition();
		List<ReachNameDefinition> definedNameList = recorder.getReachNameList();

		boolean onlyCreateInstance = true;
		for (ReachNameDefinition definedName : definedNameList) {
			if (definedName.getName() == definition) {
				NameReference valueReference = definedName.getValue();
				if (valueReference != null && valueReference.isGroupReference()) {
					NameReferenceGroup group = (NameReferenceGroup)valueReference;
					NameReferenceGroupKind groupKind = group.getGroupKind(); 
					if (groupKind != NameReferenceGroupKind.NRGK_ARRAY_CREATION && 
							groupKind != NameReferenceGroupKind.NRGK_CLASS_INSTANCE_CREATION) onlyCreateInstance = false;
					
				}
			}
		}
		if (onlyCreateInstance) return true;	// The only reach name definition is to create instance for this reference!
		
		for (GraphNode dominateGraphNode : dominateNodeList) {
			ExecutionPoint dominateNode = (ExecutionPoint)dominateGraphNode;
			NullCheckReferenceRecorder dominateNodeRecorder = (NullCheckReferenceRecorder)dominateNode.getFlowInfoRecorder();
			// Note that the reference should be resolved before calling this method!
			List<NameReference> checkedReferenceList = dominateNodeRecorder.getCheckedReferneceList();
			for (NameReference checkedReference : checkedReferenceList) {
				if (definition != checkedReference.getDefinition()) continue;
				if (definition == null) {
					if (reference.getName().equals(checkedReference.getName())) return true;
				}
				List<ReachNameDefinition> checkedDefinedNameList = dominateNodeRecorder.getReachNameList();
				boolean containsAll = true;
				for (ReachNameDefinition definedName : definedNameList) {
					if (definedName.getName() == definition) {
						NameReference valueReference = definedName.getValue();
						boolean isCreateInstance = false;
						if (valueReference != null && valueReference.isGroupReference()) {
							NameReferenceGroup group = (NameReferenceGroup)valueReference;
							NameReferenceGroupKind groupKind = group.getGroupKind(); 
							if (groupKind == NameReferenceGroupKind.NRGK_ARRAY_CREATION || 
									groupKind == NameReferenceGroupKind.NRGK_CLASS_INSTANCE_CREATION) isCreateInstance = true;
							
						}
						if (!isCreateInstance && !checkedDefinedNameList.contains(definedName)) {
							containsAll = false;
							break;
						}
					}
				}
				if (containsAll) return true;
			}
		}
		return false;
	}
	
	public List<ObjectReferenceRecorder> getObjectReferenceListInExpression(NameReference reference, boolean isDereference) {
		List<ObjectReferenceRecorder> result = new ArrayList<ObjectReferenceRecorder>();

		List<ObjectReferenceRecorder> subreferenceList = getSubAccessPathReferenceList(reference);
		if (subreferenceList != null) {
			for (ObjectReferenceRecorder subreferenceRecorder : subreferenceList) {
				if (subreferenceRecorder.reference == null) {
					System.out.println("Null sub reference in " + reference + ", kind : " + reference.getReferenceKind());
				} else {
					List<ObjectReferenceRecorder> subresult = getObjectReferenceListInExpression(subreferenceRecorder.reference, subreferenceRecorder.isDereference);
					result.addAll(subresult);
				}
			}
		}

		if (isObjectReference(reference)) {
//			System.out.println("Object reference: [" + reference.toSimpleString() + "]");
			result.add(new ObjectReferenceRecorder(reference, isDereference));
		} // else System.out.println("Non-object reference: [" + reference.toSimpleString() + "]");
		
		return result;
	}
	
	public List<ObjectReferenceRecorder> getSubAccessPathReferenceList(NameReference reference) {
		List<ObjectReferenceRecorder> result = new ArrayList<ObjectReferenceRecorder>();
		if (!reference.isGroupReference()) {
			List<NameReference> subreferenceList = reference.getSubReferenceList();
			for (NameReference subreference : subreferenceList) result.add(new ObjectReferenceRecorder(subreference, false));
			return result;
		}
		
		NameReferenceGroup group = (NameReferenceGroup)reference;
		NameReferenceGroupKind kind = group.getGroupKind();
		List<NameReference> subreferenceList = group.getSubReferenceList();
		
		if (subreferenceList == null) return null;
		
		if (kind == NameReferenceGroupKind.NRGK_FIELD_ACCESS) {
			for (NameReference subreference : subreferenceList) {
				NameReferenceKind subkind = subreference.getReferenceKind();
				if (subkind != NameReferenceKind.NRK_FIELD && subkind != NameReferenceKind.NRK_TYPE && 
						subkind != NameReferenceKind.NRK_LITERAL) result.add(new ObjectReferenceRecorder(subreference, true));
			}
		} else if (kind == NameReferenceGroupKind.NRGK_METHOD_INVOCATION) {
			for (NameReference subreference : subreferenceList) {
				NameReferenceKind subkind = subreference.getReferenceKind();
				if (subkind == NameReferenceKind.NRK_LITERAL || subkind == NameReferenceKind.NRK_TYPE) continue;
				if (subkind == NameReferenceKind.NRK_METHOD) {
					MethodReference methodReference = (MethodReference)subreference;
					List<NameReference> subreferenceInMethodList = methodReference.getSubReferenceList();
					for (NameReference referenceInMethod : subreferenceInMethodList) {
						subkind = referenceInMethod.getReferenceKind();
						if (subkind == NameReferenceKind.NRK_LITERAL || subkind == NameReferenceKind.NRK_TYPE) continue;
						result.add(new ObjectReferenceRecorder(referenceInMethod, false));
					}
				} else result.add(new ObjectReferenceRecorder(subreference, true));
			}
		} else if (kind == NameReferenceGroupKind.NRGK_QUALIFIED_NAME) {
			result.add(new ObjectReferenceRecorder(subreferenceList.get(0), true));
		} else {
			for (NameReference subreference : subreferenceList) result.add(new ObjectReferenceRecorder(subreference, false));
		}
		
		return result;
	}
	
	public boolean isObjectReference(NameReference reference) {
		if (!reference.isResolved()) {
//			System.out.println("\tCan not resolve " + reference.toSimpleString());
			return false;
		}
		NameReferenceKind kind = reference.getReferenceKind();
		if (kind == NameReferenceKind.NRK_LITERAL || kind == NameReferenceKind.NRK_PACKAGE ||
				kind == NameReferenceKind.NRK_TYPE || kind == NameReferenceKind.NRK_UNKNOWN) {
//			System.out.println("\tReference kind is " + kind + ", " + reference.toSimpleString());
			return false;
		}
		NameDefinition definition = reference.getDefinition();
		if (kind == NameReferenceKind.NRK_GROUP) {
			NameReferenceGroup group = (NameReferenceGroup)reference;
			NameReferenceGroupKind groupKind = group.getGroupKind();
			
			if (groupKind != NameReferenceGroupKind.NRGK_ARRAY_ACCESS && groupKind != NameReferenceGroupKind.NRGK_ARRAY_CREATION &&
					groupKind != NameReferenceGroupKind.NRGK_CLASS_INSTANCE_CREATION && groupKind != NameReferenceGroupKind.NRGK_CONDITIONAL &&
					groupKind != NameReferenceGroupKind.NRGK_FIELD_ACCESS && groupKind != NameReferenceGroupKind.NRGK_METHOD_INVOCATION &&
					groupKind != NameReferenceGroupKind.NRGK_QUALIFIED_NAME) {
//				System.out.println("\tGroup reference kind is " + groupKind + ", " + reference.toSimpleString());
				return false;
			}
			if (!definition.isTypeDefinition()) {
//				System.out.println("\tGroup reference definition is not type " + reference.toSimpleString() + ", and definition is " + definition + " and it kind " + definition.getDefinitionKind());
				return false;
			}
			TypeDefinition typeDefinition = (TypeDefinition)definition;
			if (!typeDefinition.isPrimitive()) return true;

//			System.out.println("\tGroup reference is primitive type " + reference.toSimpleString());
			return false;
		}
		TypeReference typeReference = definition.getDeclareTypeReference();
		if (typeReference != null) {
			if (!typeReference.isReferToPrimitiveType()) return true;
		}
//		System.out.println("\tType reference is null or is not primitive " + reference.toSimpleString());
		return false;
	}
}

class ObjectReferenceRecorder {
	NameReference reference = null;
	boolean isDereference = false;

	public ObjectReferenceRecorder(NameReference reference, boolean isDereference) {
		this.reference = reference;
		this.isDereference = isDereference;
	}
}

