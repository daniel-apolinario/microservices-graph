/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.jgrapht.util.SupplierUtil;

import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionIssue;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionTarget;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.InitialArchitectureState;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroservicesGraphEvolution {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String searchFolder = "/home/daniel/Downloads/";
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**grafo*[0-9].dot}");

		// get all the files that store the graph initial structure of the microservices
		// apps
		Collection<Path> graphFilesDOT = findGraphFiles(searchFolder, matcher);
		// transform the files to Graph objects to work with them
		List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = importMicroservicesGraphList(
				graphFilesDOT);

		// initialize general parameters to be used in the evolution through releases
		GraphEvolutionParameters evolutionParameters = initializeEvolutionParameters();

		// configure evolution parameters for each microservices graph in according
		// their sizes and the
		// general evolution parameters
		microservicesGraphList = configureEvolutionParameters(microservicesGraphList, evolutionParameters);

		// heuristic to distribute the growth rate by the releases
		microservicesGraphList = drawEvolutionSteps(microservicesGraphList, evolutionParameters);

		simulateArchitectureEvolution(microservicesGraphList, evolutionParameters);
	}

	/**
	 * @param microservicesGraphList
	 * @param evolutionParameters
	 */
	private static void simulateArchitectureEvolution(
			List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList,
			GraphEvolutionParameters evolutionParameters) {
		if (microservicesGraphList != null && microservicesGraphList.size() > 0) {
			for (MicroservicesGraph<String, DefaultEdge> microservicesGraph : microservicesGraphList) {
				if (microservicesGraph.getInitialArchitectureState() == InitialArchitectureState.GOOD) {
					if (microservicesGraph.getArchitectureEvolutionTarget() == ArchitectureEvolutionTarget.WORSE) {
						simulateArchitectureEvolutionToWorse(microservicesGraph);
					} else { // in this case the expected is the KEEP target
						simulateArchitectureEvoluionToKeep(microservicesGraph);
					}
				} else {
					if (microservicesGraph.getInitialArchitectureState() == InitialArchitectureState.BAD) {

					}
				}
			}
		}
	}

	/**
	 * @param microservicesGraph
	 */
	private static void simulateArchitectureEvoluionToKeep(MicroservicesGraph<String, DefaultEdge> microservicesGraph) {
		if (microservicesGraph != null) {
			if (microservicesGraph.getArchitectureEvolutionIssue() == ArchitectureEvolutionIssue.MEGA_SERVICE) {
				int[] verticesToAdd = microservicesGraph.getVerticesToAddInReleases();
				File newDirectory = new File(
						microservicesGraph.getFileName().substring(0, microservicesGraph.getFileName().length() - 4));
				newDirectory.mkdir();
				if (verticesToAdd != null && verticesToAdd.length > 0) {
					for (int i = 0; i < verticesToAdd.length; i++) {
						if (verticesToAdd[i] > 0) {
							VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true,
									true, true, true);
							List<String> vertices = MicroservicesGraphUtil.getRandomVertices(microservicesGraph,
									verticesToAdd[i], vertexTypeRestrictions);
							for (int j = 0; j < verticesToAdd[i]; j++) {
								String addedVertex = microservicesGraph.addVertex();
								microservicesGraph = MicroservicesGraphUtil.connectDirectedVerticesRandomly(
										microservicesGraph, addedVertex, vertices.get(j));
								microservicesGraph = keepArchitectureConsistency(microservicesGraph, addedVertex);
							}
						}
						MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, newDirectory.getAbsolutePath(),
								"release-" + i);
					}
				}
			}
		}
	}

	/**
	 * @param microservicesGraph
	 * @param addedVertex
	 * @return
	 */
	private static MicroservicesGraph<String, DefaultEdge> keepArchitectureConsistency(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, String addedVertex) {
		// if new vertex added is calling any other existent service
		if (microservicesGraph.outDegreeOf(addedVertex) > 0) {
			// we will include an API Gateway call 
			existsVertexType(VertexType)
		}
		return null;
	}

	
	/**
	 * @param microservicesGraph
	 */
	private static void simulateArchitectureEvolutionToWorse(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph) {
		if (microservicesGraph != null) {
			if (microservicesGraph.getArchitectureEvolutionIssue() == ArchitectureEvolutionIssue.MEGA_SERVICE) {
				// to select one vertice to be the Mega Service
				VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true, true, true,
						true);
				String maxIncomingDegreeVertex = findMaxIncomingDegreeVertex(microservicesGraph,
						vertexTypeRestrictions);
				int[] verticesToAdd = microservicesGraph.getVerticesToAddInReleases();
				File newDirectory = new File(
						microservicesGraph.getFileName().substring(0, microservicesGraph.getFileName().length() - 4));
				newDirectory.mkdir();
				if (verticesToAdd != null && verticesToAdd.length > 0) {
					for (int i = 0; i < verticesToAdd.length; i++) {
						microservicesGraph = addNewVerticesToOneTargetVertice(microservicesGraph, verticesToAdd[i],
								maxIncomingDegreeVertex);
						MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, newDirectory.getAbsolutePath(),
								"release-" + i);
					}
				}
			}
		}

	}

	/**
	 * @param microservicesGraph
	 * @param i
	 * @param maxIncomingDegreeVertex
	 * @return
	 */
	private static MicroservicesGraph<String, DefaultEdge> addNewVerticesToOneTargetVertice(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, int verticesToAdd, String targetVertex) {
		MicroservicesGraph<String, DefaultEdge> graph = microservicesGraph;
		for (int i = 0; i < verticesToAdd; i++) {
			String addedVertex = graph.addVertex();
			graph.addEdge(addedVertex, targetVertex);
		}
		return graph;
	}

	/**
	 * @param microservicesGraph
	 * @return
	 */
	private static String findMaxIncomingDegreeVertex(MicroservicesGraph<String, DefaultEdge> microservicesGraph,
			VertexTypeRestrictions vertexTypeRestrictions) {
		String selectedVertex = null;
		if (microservicesGraph != null && microservicesGraph.vertexSet().size() > 0) {
			int maxIncomingDegree = 0;
			for (String vertex : microservicesGraph.vertexSet()) {
				int incomingDegree = microservicesGraph.inDegreeOf(vertex);
				if (incomingDegree > maxIncomingDegree && vertexTypeRestrictions.testVertexTypeRestrictions(vertex)) {
					maxIncomingDegree = incomingDegree;
					selectedVertex = vertex;
				}
			}
		}
		return selectedVertex;
	}

	/**
	 * @param microservicesGraphList
	 * @param evolutionParameters
	 * @return
	 */
	private static List<MicroservicesGraph<String, DefaultEdge>> drawEvolutionSteps(
			List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList,
			GraphEvolutionParameters evolutionParameters) {
		List<MicroservicesGraph<String, DefaultEdge>> graphList = new ArrayList<MicroservicesGraph<String, DefaultEdge>>();

		Random rd = new Random();
		for (MicroservicesGraph<String, DefaultEdge> microservicesGraph : microservicesGraphList) {
			// the growth rate is per microservices graph
			int growthRate = rd
					.nextInt(evolutionParameters.getArchitectureEvolutionGrowthRateMaximum()
							- evolutionParameters.getArchitectureEvolutionGrowthRateMininum())
					+ evolutionParameters.getArchitectureEvolutionGrowthRateMininum();
			int currentVerticesNumber = microservicesGraph.vertexSet().size();
			// int currentEdgesNumber = microservicesGraph.edgeSet().size();
			// we will put 30% more vertices to cancel with 30% of removed vertices
			microservicesGraph.setVerticesToAddInReleases(
					calculateVectorDistribution((int) (currentVerticesNumber * ((double) growthRate / 100)),
							evolutionParameters.getNumberOfReleases()));
//			microservicesGraph.setVerticesToRemoveInReleases(calculateVectorDistribution(
//					(int) ((currentVerticesNumber * (1 + ((double) growthRate / 100))) + (currentVerticesNumber * 0.3)),
//					evolutionParameters.getNumberOfReleases()));
//			// we will put 30% more edges to cancel with 30% of removed edges
//			microservicesGraph.setEdgesToAddInReleases(calculateVectorDistribution(
//					(int) ((currentEdgesNumber * (1 + ((double) growthRate / 100))) + (currentEdgesNumber * 0.3)),
//					evolutionParameters.getNumberOfReleases()));
//			microservicesGraph.setEdgesToRemoveInReleases(calculateVectorDistribution(
//					(int) ((currentEdgesNumber * (1 + ((double) growthRate / 100))) + (currentEdgesNumber * 0.3)),
//					evolutionParameters.getNumberOfReleases()));
			graphList.add(microservicesGraph);

		}

		return graphList;
	}

	/**
	 * 
	 * @param elements - can be nodes or edges of the graph
	 * @param releases - number of releases to distribute the increasing
	 * @return integer vector where each item represents the number of elements
	 *         changed for each release
	 */
	public static int[] calculateVectorDistribution(int elements, int releases) {
		int[] result = new int[releases];
		Random rd = new Random();
		int allElements = elements;
		// don't catch all the Elements for the distribution bo be more balanced.
		// int elementsToSort = (int) ((allElements * 0.2) + 1); **oldVersion
		int elementsToSort = (int) ((allElements / releases) * (0.3 * releases));
		// this is the remainder elements that we will adding gradually
		double growthRate = (double) (allElements - elementsToSort) / releases;
		double elementsAdded = 0;
		// to control the total of added elements
		int sum = 0;
		for (int i = 0; i < releases; i++) {
			int drawnElements = rd.nextInt(elementsToSort + 1);
			result[i] = drawnElements;
			sum = sum + drawnElements;
			elementsToSort = elementsToSort - drawnElements;
			elementsAdded = elementsAdded + growthRate;
			if (elementsAdded >= 1) {
				int adding = (int) (elementsAdded - (elementsAdded % 1));
				elementsToSort = elementsToSort + adding;
				elementsAdded = elementsAdded - adding;
			}
		}
		// sum the leftover to the last element (last release)
		if (allElements - sum > 0) {
			result[releases - 1] = result[releases - 1] + allElements - sum;
		}
		return result;
	}

	/**
	 * @param microservicesGraphList
	 * @param evolutionParameters
	 * @return
	 */
	private static List<MicroservicesGraph<String, DefaultEdge>> configureEvolutionParameters(
			List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList,
			GraphEvolutionParameters evolutionParameters) {
		List<MicroservicesGraph<String, DefaultEdge>> configuredGraphList = microservicesGraphList;

		if (microservicesGraphList != null && microservicesGraphList.size() > 0 && evolutionParameters != null) {
			int graphsQuantity = microservicesGraphList.size();
			int graphsNumberOfArchitectureGood = (int) Math
					.ceil(((double) evolutionParameters.getInitialArchitectureGoodPercentage() / 100) * graphsQuantity);
			int graphsNumberOfArchitectureBad = graphsQuantity - graphsNumberOfArchitectureGood;
			System.out.println("graphsNumberOfArchitectureGood=" + graphsNumberOfArchitectureGood
					+ "-graphsNumberOfArchitectureBad=" + graphsNumberOfArchitectureBad);
			int graphsNumberOfTargetWorse = (int) Math.ceil((double) graphsNumberOfArchitectureGood / 2);
			int graphsNumberOfTargetBetter = (int) Math.ceil((double) graphsNumberOfArchitectureBad / 2);
			for (MicroservicesGraph<String, DefaultEdge> microservicesGraph : configuredGraphList) {
				// meanwhile, we will set just one architecture issue = MEGA_SERVICE
				microservicesGraph.setArchitectureEvolutionIssue(ArchitectureEvolutionIssue.MEGA_SERVICE);
				if (graphsNumberOfArchitectureGood > 0) {
					microservicesGraph.setInitialArchitectureState(InitialArchitectureState.GOOD);
					graphsNumberOfArchitectureGood--;
					if (graphsNumberOfTargetWorse > 0) {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.WORSE);
						graphsNumberOfTargetWorse--;
					} else {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.KEEP);
					}
				} else {
					microservicesGraph.setInitialArchitectureState(InitialArchitectureState.BAD);
					graphsNumberOfArchitectureBad--;
					if (graphsNumberOfTargetBetter > 0) {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.BETTER);
						graphsNumberOfTargetBetter--;
					} else {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.KEEP);
					}
				}
			}
		}

		return configuredGraphList;
	}

	/**
	 * @return
	 */
	private static GraphEvolutionParameters initializeEvolutionParameters() {
		GraphEvolutionParameters evolutionParameters = new GraphEvolutionParameters();
		evolutionParameters.setNumberOfReleases(20);
		evolutionParameters.setInitialArchitectureGoodPercentage(50);
		evolutionParameters.setArchitectureEvolutionTargetBetterPercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionTargetWorsePercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionTargetKeepPercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionGrowthRateMininum(30);
		evolutionParameters.setArchitectureEvolutionGrowthRateMaximum(60);
		return evolutionParameters;
	}

	/**
	 * @param graphFilesDOT
	 * @return
	 */
	private static List<MicroservicesGraph<String, DefaultEdge>> importMicroservicesGraphList(
			Collection<Path> graphFilesDOT) {
		List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = new ArrayList<MicroservicesGraph<String, DefaultEdge>>();
		if (!graphFilesDOT.isEmpty()) {
			VertexProvider<String> vp = (a, b) -> a;
			EdgeProvider<String, DefaultEdge> ep = (f, t, l, a) -> new DefaultEdge();
			GraphImporter<String, DefaultEdge> importer = new DOTImporter<String, DefaultEdge>(vp, ep);

			for (Path path : graphFilesDOT) {
				BufferedReader reader;
				try {
					reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
					Graph<String, DefaultEdge> graph = new MicroservicesGraph<String, DefaultEdge>(
							MicroservicesGraphUtil.createVerticeSupplier("MSV"),
							SupplierUtil.createDefaultEdgeSupplier());
					importer.importGraph(graph, reader);
					MicroservicesGraph<String, DefaultEdge> mg = (MicroservicesGraph<String, DefaultEdge>) graph;
					mg.setFileName(path.toString());
					microservicesGraphList.add(mg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ImportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return microservicesGraphList;
	}

	/**
	 * @param searchFolder
	 * @param matcher
	 * @return
	 */
	private static Collection<Path> findGraphFiles(String searchFolder, PathMatcher matcher) {
		Collection<Path> files = null;
		try {
			files = find("/home/daniel/Downloads/", matcher);
			files.forEach(n -> System.out.println(n));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return files;
	}

	protected static Collection<Path> find(String searchDirectory, PathMatcher matcher) throws IOException {
		try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
			return files.filter(matcher::matches).collect(Collectors.toList());

		}
	}

}
