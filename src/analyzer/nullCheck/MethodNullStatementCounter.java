package analyzer.nullCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import graph.basic.GraphNode;
import graph.cfg.CFGNode;
import graph.cfg.CFGNodeType;
import graph.cfg.ControlFlowGraph;
import graph.cfg.ExecutionPoint;
import graph.cfg.ExecutionPointLabel;
import graph.cfg.ExecutionPointType;
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
import nameTable.nameReference.NameReference;
import nameTable.nameReference.NameReferenceLabel;
import nameTable.nameReference.TypeReference;
import nameTable.nameScope.CompilationUnitScope;
import nameTable.visitor.NameDefinitionVisitor;
import sourceCodeAST.SourceCodeFileSet;
import sourceCodeAST.SourceCodeLocation;
import util.Debug;

/**
 * @author Zhou Xiaocong
 * @since 2017Äê7ÔÂ15ÈÕ
 * @version 1.0
 *
 */
public class MethodNullStatementCounter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String rootPath = "C:\\";

		
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
			return;
		}
		
		try {
			Debug.setStart("Begin collection....");
//			collectAllMethodsNullStatementByScanningUnit(path, writer);
			collectAllMethodsNullStatementByScanningMethod(path, writer);
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
	
	public static void collectAllMethodsNullStatementByScanningMethod(String path, PrintWriter writer) {
		NameTableManager manager = createNameTableManager(path);

		NameDefinitionVisitor visitor = new NameDefinitionVisitor(new NameDefinitionKindFilter(NameDefinitionKind.NDK_METHOD));
		manager.accept(visitor);
		List<NameDefinition> methodList = visitor.getResult();
		
		NameTableASTBridge bridge = new NameTableASTBridge(manager);
		SourceCodeFileSet sourceCodeFileSet = manager.getSouceCodeFileSet();
		
		int counter = 0;
		int nodecl = 0;
		String message = "No\tUnitFile\tClass\tMethod\tLocation\tReturnTypeRef\tReturnTypeDef\tIsPrimitive\tIsConstructor\tReturnStatements\tReturnNulls\tPredicate\tNullChecker";
		writer.println(message);
		for (NameDefinition nameDefinition : methodList) {
			MethodDefinition method = (MethodDefinition)nameDefinition;
			if (method.isAutoGenerated()) continue; 
			TypeDefinition enclosingType = method.getEnclosingType();
			DetailedTypeDefinition type = (DetailedTypeDefinition)enclosingType; 
			if (type.isAnonymous()) continue;
			
			SourceCodeLocation location = method.getLocation();
			String unitFileName = location.getFileUnitName();
			String className = type.getSimpleName();
			String methodName = method.getSimpleName() + "()";
			String rowCol = "(" + location.getLineNumber() + "," + location.getColumn() + ")";
			TypeReference returnType = method.getReturnType(); 
			String returnTypeDef = "";
			String returnTypeRef = "";
			if (returnType != null) {
				returnTypeRef = returnType.getName();
				if (returnType.resolveBinding()) {
					returnTypeDef = returnType.getDefinition().getFullQualifiedName();
				}
			}
			String isPrimitive = "False";
			if (NameReferenceLabel.isPrimitiveTypeName(returnTypeRef)) {
				isPrimitive = "True";
			}
			String isConstructor = "False";
			if (method.isConstructor()) {
				isConstructor = "True";
			}
			
			counter = counter+1;
			Debug.println("Method " + counter + ": " + className + "." + methodName + ", " + isPrimitive);

			CompilationUnit root = sourceCodeFileSet.findSourceCodeFileASTRootByFileUnitName(unitFileName);	
			MethodDeclaration methodDecl = bridge.findASTNodeForMethodDefinition(method);
			if (methodDecl == null) {
				Debug.println("Can not find method declaration for method definition " + method.getFullQualifiedName() + " at " + rowCol);
				writer.println(counter + "\t" + unitFileName + "\t" + className + "\t" + methodName + "\t" + rowCol + "\t" + returnTypeRef + "\t" + returnTypeDef + "\t" + isPrimitive + "\t" + isConstructor +"\tNA\tNA\tNA\tNA");
				nodecl++;
			} else {
				NullStatementCounter result = countNullStatementForMethod(manager, unitFileName, root, className, methodDecl);
				writer.println(counter + "\t" + unitFileName + "\t" + className + "\t" + methodName + "\t" + rowCol + "\t" + returnTypeRef + "\t" + returnTypeDef + "\t" + isPrimitive + "\t" + isConstructor +"\t" + result.returnStatement + "\t" + result.returnNull + "\t" + result.predicate + "\t" + result.predicateNull);
			}
			sourceCodeFileSet.releaseAST(unitFileName);
			sourceCodeFileSet.releaseFileContent(unitFileName);
		}
		Debug.println("There are " + nodecl + " methods for which we can not find declaration.");
	}
	
	public static void collectAllMethodsNullStatementByScanningUnit(String path, PrintWriter writer) {
		NameTableManager manager = createNameTableManager(path);

		String message = "No\tUnitFile\tClass\tMethod\tLocation\tReturnTypeRef\tReturnTypeDef\tIsPrimitive\tIsConstructor\tReturnStatements\tReturnNulls\tPredicate\tNullChecker";
		writer.println(message);
		
		int counter = 0;
		List<CompilationUnitScope> unitList = manager.getAllCompilationUnitScopes();
		if (unitList == null) return;
		for (CompilationUnitScope unit : unitList) {
			String unitFileName = unit.getUnitName();
			CompilationUnit root = manager.getSouceCodeFileSet().findSourceCodeFileASTRootByFileUnitName(unitFileName);			
			@SuppressWarnings("unchecked")
			List<AbstractTypeDeclaration> typeDeclarationList = root.types();
			if (typeDeclarationList == null) continue;
			
			for (AbstractTypeDeclaration abstractTypeDeclaration : typeDeclarationList) {
				if (abstractTypeDeclaration.getNodeType() != ASTNode.TYPE_DECLARATION) continue;
				TypeDeclaration typeDecl = (TypeDeclaration)abstractTypeDeclaration;
				
				DetailedTypeDefinition typeDef = findDetailedTypeDefinition(unit, root, unitFileName, typeDecl);
				String className = typeDef.getSimpleName();
				
				MethodDeclaration[] methodDeclarationArray = typeDecl.getMethods();
				if (methodDeclarationArray == null) continue;
				
				for (MethodDeclaration methodDecl : methodDeclarationArray) {
					MethodDefinition method = findMethodDefinition(typeDef, root, unitFileName, methodDecl);
					
					SourceCodeLocation location = method.getLocation();
					String methodName = method.getSimpleName() + "()";
					String rowCol = "(" + location.getLineNumber() + "," + location.getColumn() + ")";
					TypeReference returnType = method.getReturnType(); 
					String returnTypeDef = "";
					String returnTypeRef = "";
					if (returnType != null) {
						returnTypeRef = returnType.getName();
						if (returnType.resolveBinding()) {
							returnTypeDef = returnType.getDefinition().getFullQualifiedName();
						}
					}
					String isPrimitive = "False";
					if (NameReferenceLabel.isPrimitiveTypeName(returnTypeRef)) {
						isPrimitive = "True";
					}
					String isConstructor = "False";
					if (method.isConstructor()) {
						isConstructor = "True";
					}
					
					NullStatementCounter result = countNullStatementForMethod(manager, unitFileName, root, className, methodDecl);
					counter = counter+1;
					Debug.println("Method " + counter + ": " + className + "." + methodName + ", " + isPrimitive);
					writer.println(counter + "\t" + unitFileName + "\t" + className + "\t" + methodName + "\t" + rowCol + "\t" + returnTypeRef + "\t" + returnTypeDef + "\t" + isPrimitive + "\t" + isConstructor +"\t" + result.returnStatement + "\t" + result.returnNull + "\t" + result.predicate + "\t" + result.predicateNull);
				}
				
			}
			
		}
		
	}
	
	private static DetailedTypeDefinition findDetailedTypeDefinition(CompilationUnitScope unit, CompilationUnit root, String unitFileName, TypeDeclaration typeDecl) {
		List<TypeDefinition> typeList = unit.getTypeList();
		if (typeList == null) {
			throw new AssertionError("There no type definition for compilation unit " + unitFileName);
		}
		
		String declFullName = typeDecl.getName().getIdentifier();
		SourceCodeLocation location = SourceCodeLocation.getStartLocation(typeDecl, root, unitFileName);
		for (TypeDefinition type : typeList) {
			if (type.getSimpleName().equals(declFullName) && type.getLocation().equals(location)) {
				if (type.isDetailedType()) return (DetailedTypeDefinition)type;
			}
		}
		
		throw new AssertionError("Can not find detailed type definition for type declaration " + typeDecl + " at " + location);
	}
	
	private static MethodDefinition findMethodDefinition(DetailedTypeDefinition type, CompilationUnit root, String unitFileName, MethodDeclaration methodDecl) {
		SourceCodeLocation location = SourceCodeLocation.getStartLocation(methodDecl, root, unitFileName);
		String methodSimpleName = methodDecl.getName().getIdentifier();
		
		List<MethodDefinition> methodList = type.getMethodList();
		for (MethodDefinition methodInType : methodList) {
			if (methodInType.getSimpleName().equals(methodSimpleName) && methodInType.getLocation().equals(location)) return methodInType;
		}
		
		throw new AssertionError("Can not find method definition for method delcaration " + methodSimpleName + ", in type " + type.getFullQualifiedName());
	}
	
	public static NullStatementCounter countNullStatementForMethod(NameTableManager manager, String unitFileName, CompilationUnit root, String className, MethodDeclaration methodDecl) {
		NullStatementCounter result = new NullStatementCounter();
		
		NameReferenceCreator referenceCreator = new NameReferenceCreator(manager, true);
		
		CFGCreator creator = new CFGCreator(unitFileName, root);
		ControlFlowGraph graph = creator.create(methodDecl, className);
		if (graph == null) return result;
		
		List<GraphNode> nodeList = graph.getAllNodes();
		if (nodeList == null) return result;
		
		for (GraphNode node : nodeList) {
			CFGNode cfgNode = (CFGNode)node;
			if (cfgNode.getCFGNodeType() != CFGNodeType.N_EXECUTION_POINT) continue;
			ExecutionPoint exePoint = (ExecutionPoint)cfgNode;
			
			if (exePoint.isPredicate()) {
				result.predicate++;
				
				Expression expression = (Expression)exePoint.getAstNode();
				if (expression == null) continue;
				NameReference reference = referenceCreator.createReferenceForExpressionASTNode(unitFileName, expression);
				List<NameReference> referenceListAtLeaf = reference.getReferencesAtLeaf();
				for (NameReference referenceAtLeaf : referenceListAtLeaf) {
					if (referenceAtLeaf.isNullReference()) {
						result.predicateNull++;
					}
				}
			} else if (exePoint.getType() == ExecutionPointType.FLOW_CONTROLLER && exePoint.getLabel() == ExecutionPointLabel.RETURN_LABEL) {
				result.returnStatement++;
				ASTNode astNode = exePoint.getAstNode();
				List<NameReference> referenceList = referenceCreator.createReferencesForASTNode(unitFileName, astNode);	
				if (referenceList != null) {
					if (referenceList.size() == 1) {
						NameReference reference = referenceList.get(0);
						if (reference.isNullReference()) {
							result.returnNull++;
						}
					}
				}
			}
		}
		
		return result;
	}
}

class NullStatementCounter {
	public int returnStatement = 0;
	public int returnNull = 0;
	
	public int predicate = 0;
	public int predicateNull = 0;
}
