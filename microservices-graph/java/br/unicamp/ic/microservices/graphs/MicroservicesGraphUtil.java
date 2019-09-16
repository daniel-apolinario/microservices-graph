/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroservicesGraphUtil {

	private static final String RELEASE_IDENTIFICATOR = "release";
	private static final String NAME_SEPARATOR = "-";

	/**
	 * Method to select randomly a percentual of vertices belonging to a graph
	 * 
	 * @param graph
	 * @param percentual
	 * @param vertexTypeRestrictions
	 * @return
	 */
	public static List<String> getPercentualRandomVertices(Graph<String, DefaultEdge> graph, int percentual,
			VertexTypeRestrictions vertexTypeRestrictions) {
		List<String> vertices = new ArrayList<String>();
		int graphSize = 0;
		if (graph != null && graph.vertexSet().size() > 0 && percentual > 0 && percentual <= 100) {
			graphSize = graph.vertexSet().size();
			int verticesQty = Math.floorDiv(graphSize * percentual, 100);
			vertices = getRandomVertices(graph, verticesQty, vertexTypeRestrictions);
		}
		return vertices;
	}

	/**
	 * Method to select randomly a exact number of vertices belonging to a graph
	 * 
	 * @param graph
	 * @param percentual
	 * @param vertexTypeRestrictions
	 * @return
	 */
	public static List<String> getRandomVertices(Graph<String, DefaultEdge> graph, int quantity,
			VertexTypeRestrictions vertexTypeRestrictions) {
		List<String> vertices = new ArrayList<String>();
		Random rd = new Random();
		int graphSize = 0;
		if (graph != null && graph.vertexSet().size() > 0 && quantity > 0 && quantity <= graph.vertexSet().size()) {
			graphSize = graph.vertexSet().size();
			List<String> allVertices = Graphs.getVertexToIntegerMapping(graph).getIndexList();
			int i = 0;
			while (i < quantity) {
				int vertexIndex = rd.nextInt(graphSize - i);
				if (vertexTypeRestrictions.testVertexTypeRestrictions(allVertices.get(vertexIndex))) {
					vertices.add(allVertices.get(vertexIndex));
					allVertices.remove(vertexIndex);
					i++;
				}
			}
		}
		return vertices;
	}

	/**
	 * @param microservicesGraph
	 * @param addedVertex
	 * @param string
	 * @return
	 */
	public static MicroservicesGraph<String, DefaultEdge> connectDirectedVerticesRandomly(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, String firstVertice, String secondVertice) {
		MicroservicesGraph<String, DefaultEdge> returnedGraph = microservicesGraph;
		Random rd = new Random();
		int randomNumber = rd.nextInt(2);
		if (randomNumber == 0) {
			returnedGraph.addEdge(firstVertice, secondVertice);
		} else {
			returnedGraph.addEdge(secondVertice, firstVertice);
		}
		return returnedGraph;
	}

	/**
	 * Method to export a Graph object to a DOT file format
	 * 
	 * @param graph
	 */
	public static void exportGraphToFile(Graph<String, DefaultEdge> graph, String dirName, String fileName) {
		if (graph != null) {
			// use helper classes to define how vertices should be rendered,
			// adhering to the DOT language restrictions
			ComponentNameProvider<String> vertexIdProvider = new ComponentNameProvider<String>() {
				public String getName(String name) {
					return name;
				}
			};
			ComponentNameProvider<String> vertexLabelProvider = new ComponentNameProvider<String>() {
				public String getName(String name) {
					return name;
				}
			};
			GraphExporter<String, DefaultEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexLabelProvider,
					null);
			Writer writer = null;
			try {
				writer = new FileWriter(dirName + "/" + fileName + ".dot");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				exporter.exportGraph(graph, writer);
			} catch (org.jgrapht.io.ExportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method that creates a vertices supplier to new Graph objects.
	 * 
	 * @param prefix - prefix to be put for each new vertice added
	 * @return
	 */
	public static Supplier<String> createVerticeSupplier(String prefix) {
		Supplier<String> vSupplier = new Supplier<String>() {
			private int id = 1;

			@Override
			public String get() {
				return prefix + id++;
			}
		};
		return vSupplier;
	}

	/**
	 * @param searchFolder
	 * @param matcher
	 * @return
	 */
	public static List<Path> findGraphFiles(String searchFolder, PathMatcher matcher) {
		List<Path> files = null;
		try {
			files = find(searchFolder, matcher);
			Collections.sort(files);
			files.forEach(n -> System.out.println(n));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return files;
	}

	private static List<Path> find(String searchDirectory, PathMatcher matcher) throws IOException {
		try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
			return (List<Path>)files.filter(matcher::matches).collect(Collectors.toList());

		}
	}

	public static String getExportCompletePath(String pathName, String appName, int appNumber) {
		StringBuffer completePath = new StringBuffer();
		completePath.append(pathName).append(appName).append(NAME_SEPARATOR).append(appNumber);
		return completePath.toString();
	}

	public static String getApplicationFileName(String appName, int appNumber, int releaseNumber, String suffix) {
		StringBuffer fileName = new StringBuffer();
		fileName.append(appName).append(NAME_SEPARATOR).append(String.format("%02d", appNumber));
		if (releaseNumber > 0) {
			fileName.append(NAME_SEPARATOR).append(RELEASE_IDENTIFICATOR).append(NAME_SEPARATOR).append(String.format("%02d", releaseNumber));
		}
		if (suffix != null) {
			fileName.append(NAME_SEPARATOR).append(suffix);
		}
		return fileName.toString();
	}
}
