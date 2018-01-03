package graph.dependenceGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import graph.basic.GraphUtil;
import nameTable.NameTableManager;
import nameTable.creator.NameDefinitionCreator;
import nameTable.creator.NameTableCreator;
import nameTable.filter.NameScopeFilter;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameDefinition.TypeDefinition;
import nameTable.nameScope.NameScope;
import nameTable.nameScope.NameScopeKind;
import nameTable.visitor.NameScopeVisitor;
import sourceCodeAST.SourceCodeFileSet;
import util.Debug;

/**
 * @author Zhou Xiaocong
 * @since 2017��1��8��
 * @version 1.0
 *
 */
public class TestDependenceGraphCreator {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String rootPath = "C:\\";
		
		String[] paths = {rootPath + "ZxcTools\\debug\\package\\print_tokens2\\", rootPath + "ZxcTools\\debug\\package\\replace\\", 
				rootPath + "ZxcWork\\ProgramAnalysis\\src\\", rootPath + "ZxcTools\\EclipseSource\\org\\", 
				rootPath + "ZxcWork\\ToolKit\\src\\", rootPath + "ZxcDeveloping\\OOPAndJavaExamples\\automata\\src\\", 
				rootPath + "ZxcProject\\AspectViz\\ZxcWork\\SortAnimator4\\", rootPath + "ZxcTools\\JDKSource\\", 
				rootPath + "ZxcCourse\\JavaProgramming\\JHotDraw5.2\\sources\\", rootPath + "ZxcWork\\FaultLocalization\\src\\", 
				rootPath + "ZxcTools\\ArgoUml\\", rootPath + "ZxcTools\\jEdit_5_1_0\\", 
				rootPath + "ZxcTools\\lucene_2_0_0\\", rootPath + "ZxcTools\\struts_2_0_1\\",
				rootPath + "ZxcTools\\apache_ant_1_9_3\\src\\", rootPath + "ZxcTools\\apache_ant_1_9_3\\src\\main\\org\\apache\\tools\\ant\\",
		};

		String netFilePath = rootPath + "ZxcWork\\ProgramAnalysis\\data\\";
		String netFilePostfix =  "CDG.net";
		
		String netFile = netFilePath + "PA" + netFilePostfix;
		String resultDotFile = rootPath + "ZxcWork\\ProgramAnalysis\\data\\result.dot";
		String resultNetFile = rootPath + "ZxcWork\\ProgramAnalysis\\data\\result.net";
		String resultTxtFile = rootPath + "ZxcWork\\ProgramAnalysis\\data\\result.txt";
		
		generatePackageDependenceGraph(paths[2], resultDotFile);
	}

	public static void generatePackageDependenceGraph(String path, String dotFile) {
		
		PrintWriter dotWriter = new PrintWriter(System.out);
		try {
			dotWriter = new PrintWriter(new FileOutputStream(new File(dotFile)));
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		SourceCodeFileSet parser = new SourceCodeFileSet(path);
		NameTableCreator creator = new NameDefinitionCreator(parser);

		Debug.setStart("Begin creating system, path = " + path);
		NameTableManager manager = creator.createNameTableManager();
		Debug.time("End creating.....");

		List<NameScope> scopeList = getNameScopeListForTesting(manager);
		
		NameBasedDependenceGraphCreator nameBasedDGCreator = new NameBasedDependenceGraphCreator(manager);
		Debug.setStart("Begin create CDG...., list size " + scopeList.size());
		nameBasedDGCreator.setProgressWriter(System.out);
		nameBasedDGCreator.setIncludeZeroDegreeNode(false);
		DependenceGraph graph = nameBasedDGCreator.create("Dependence_Graph", DependenceGraphKind.DGK_INVOCATION, scopeList, NameScopeKind.NSK_METHOD);
		Debug.time("End creating CDG....");

		if (graph != null) {
			try {
				GraphUtil.simplyWriteToDotFile(graph, dotWriter);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} else System.out.println("Class dependence graph is a null graph!");
		
		dotWriter.close();
	}
	
	public static List<NameScope> getNameScopeListForTesting(NameTableManager tableManager) {
		List<NameScope> result = new ArrayList<NameScope>();
		NameScopeFilter filter = new NameScopeFilter() {
			public boolean accept(NameScope scope) {
				if (scope.getScopeKind() != NameScopeKind.NSK_METHOD) return false;
				MethodDefinition method = (MethodDefinition)scope;
//				if (scope.getScopeName().contains("nameTable")) return true;
				TypeDefinition type = method.getEnclosingType();
				if (type.getFullQualifiedName().contains("DetailedTypeDefinition")) return true;
//				if (method.getSimpleName().contains("create")) return true;
				return false;
			}
		};
		NameScopeVisitor visitor = new NameScopeVisitor(filter);
		tableManager.accept(visitor);
		result = visitor.getResult();
		return result;
	}
}
