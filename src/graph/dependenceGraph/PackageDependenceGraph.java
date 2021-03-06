package graph.dependenceGraph;

import graph.basic.AbstractGraph;
import graph.basic.GraphEdge;
import graph.basic.GraphNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import nameTable.nameDefinition.PackageDefinition;

/**
 * @author Zhou Xiaocong
 * @since 2014/1/29
 * @version 1.0
 */
public class PackageDependenceGraph extends AbstractGraph {

	private String systemPath = null; 
	
	public PackageDependenceGraph(String path) {
		super(path);
		this.systemPath = path;
	}

	@Override
	public String getId() {
		return systemPath;
	}

	public PackageDependenceNode findNodeByDefinition(PackageDefinition definition) {
		if (nodes == null) return null;
		for (GraphNode node : nodes) {
			PackageDependenceNode dNode = (PackageDependenceNode)node;
			if (dNode.getDefinition() == definition) return dNode;
		}
		return null;
	}
	
	/**
	 * Check if the edge between two nodes is in the graph
	 */
	public boolean hasEdge(PackageDependenceNode from, PackageDependenceNode to) {
		if (edges == null) return false;
		for (GraphEdge edge : edges) {
			PackageDependenceNode start = (PackageDependenceNode)edge.getStartNode(); 
			PackageDependenceNode end = (PackageDependenceNode)edge.getEndNode(); 
			if (start.equals(from) && end.equals(to)) return true;
		}
		return false;
	}
	
	/**
	 * Add an edge to the node. If the graph has an edge with the same start and end, then we DO NOT add it!
	 */
	public void addEdge(PackageDependenceEdge edge) {
		if (edges == null) edges = new ArrayList<GraphEdge>(10);
		PackageDependenceNode start = (PackageDependenceNode)edge.getStartNode();
		PackageDependenceNode end = (PackageDependenceNode)edge.getEndNode();
		if (start.equals(end)) return;		// We do not add the edge from a node to itself.
		if (hasEdge(start, end)) return;
		else edges.add(edge);
	}

	public static PackageDependenceGraph readFromNetFile(String netFile) throws IOException {
		final char splitter = ' ';		// the space char is used as splitter in the .net file
		
		// Read the node list from the .net file
		Scanner netScanner = new Scanner(new File(netFile));

		PackageDependenceGraph result = new PackageDependenceGraph(netFile);
		
		boolean isVertice = false;
		boolean isArcs = false;
		
		while(netScanner.hasNextLine()) {
			String line = netScanner.nextLine();
			if (line.contains("*Vertices")) {
				// This is the head line of the .net file, which show that the next line begins the node of the graph, and give the total number of nodes
				isVertice = true;
			} else if (line.contains("*Arcs") || line.contains("*Edges")) {
				// This line shows that the next line begins the edges (or arcs, i.e. directed edges)  of the graph
				isVertice = false;
				isArcs = true;
			} else if (line.trim().equals("")) {
				// This line shows that the next line begins the edges (or arcs, i.e. directed edges)  of the graph
				continue;
			} else if (isVertice == true) {
				// This line give a vertex (i.e. a node) of the graph, which gives the id and the label of the node, and uses a space
				// to split the id and the label
				int spacePosition = line.indexOf(splitter);
				if (spacePosition < 0) {
					netScanner.close();
					throw new AssertionError("In file [" + netFile + "], illegal line: [" + line + "]");
				}
				String idString = line.substring(0, spacePosition);
				String labelString = line.substring(spacePosition+1, line.length());
				if (labelString.contains("\"")) {
					// Clear the quota character (i.e. '\"') in the label string!
					labelString = labelString.replace("\"", "");
				}
				
				PackageDependenceNode node = new PackageDependenceNode(idString, labelString);
				result.addNode(node);
			} else if (isArcs == true) {
				// This line give a arc (i.e. an edge) of the graph, which gives the start node id and end node id
				int spacePosition = line.indexOf(splitter);
				if (spacePosition < 0) {
					netScanner.close();
					throw new AssertionError("In file [" + netFile + "], illegal line: [" + line + "]");
				}

				String startId = line.substring(0, spacePosition);
				String endId = line.substring(spacePosition+1, line.length());
				
				PackageDependenceNode startNode = (PackageDependenceNode)result.findById(startId);
				if (startNode == null) {
					netScanner.close();
					throw new AssertionError("Can not find node id " + startId);
				}
				PackageDependenceNode endNode = (PackageDependenceNode)result.findById(endId);
				if (endNode == null) {
					netScanner.close();
					throw new AssertionError("Can not find node id " + startId);
				}
				
				result.addEdge(new PackageDependenceEdge(startNode, endNode));
			}
		}
		netScanner.close();
		return result;
	}
	
}
