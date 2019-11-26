/**
 * 
 */
package br.unicamp.ic.microservices.graphs.evolution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.unicamp.ic.microservices.graphs.MicroservicesGraph;
import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.graphs.VertexType;
import br.unicamp.ic.microservices.graphs.VertexTypeRestrictions;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionIssue;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionTarget;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.InitialArchitectureState;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroservicesGraphEvolution {

	private static final String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**release*[0-9].dot}");

		// get all the files that store the graph initial structure of the microservices
		// apps
		Collection<Path> graphFilesDOT = MicroservicesGraphUtil.findFiles(searchFolder, matcher);
		// transform the files to Graph objects to work with them
		List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = MicroservicesGraphUtil
				.importMicroservicesGraphList(graphFilesDOT);

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
						if (microservicesGraph.getArchitectureEvolutionTarget() == ArchitectureEvolutionTarget.BETTER) {
							simulateArchitectureEvolutionToBetter(microservicesGraph, evolutionParameters);
						} else {
							simulateArchitectureEvoluionToKeep(microservicesGraph);
						}
					}
				}
				// generate a json file to store the parameters for each app evolution
				try (FileOutputStream fos = new FileOutputStream(microservicesGraph.getPathName() + "/evolution.json");
						OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
					Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

					gson.toJson(microservicesGraph, isr);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param microservicesGraph
	 */
	private static void simulateArchitectureEvolutionToBetter(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, GraphEvolutionParameters evolutionParameters) {
		if (microservicesGraph != null) {
			if (microservicesGraph.getArchitectureEvolutionIssue() == ArchitectureEvolutionIssue.MEGA_SERVICE) {
				// to select one vertice to be the Mega Service
				VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true, true, true,
						true);
				int graphSize = microservicesGraph.vertexSet().size();
				int maxDependencies = (int) Math.floor(
						graphSize * (((double) evolutionParameters.getArchitectureEvolutionGrowthRateMininum() / 100)));

				List<String> megaServices = findVerticesMegaServices(microservicesGraph, maxDependencies,
						vertexTypeRestrictions);

				int[] verticesToAdd = microservicesGraph.getVerticesToAddInReleases();

				if (verticesToAdd != null && verticesToAdd.length > 0) {
					for (int i = 0; i < verticesToAdd.length; i++) {
						List<String> addedVertices = breakDownMegaServices(microservicesGraph, megaServices,
								verticesToAdd[i], maxDependencies);
						for (String vertice : addedVertices) {
							keepArchitectureConsistency(microservicesGraph, vertice);
						}
						int releaseNumber = i + 1;
						MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, microservicesGraph.getPathName(),
								"release-" + String.format("%02d", releaseNumber));
					}
				}
			}
		}
	}

	/**
	 * @param microservicesGraph
	 * @param megaServices
	 * @param i
	 * @return
	 */
	private static List<String> breakDownMegaServices(MicroservicesGraph<String, DefaultEdge> microservicesGraph,
			List<String> megaServices, int verticesToAdd, int maxDependencies) {
		List<String> addedVertices = new ArrayList<String>();
		for (int i = 0; i < verticesToAdd; i++) {
			if (megaServices != null && megaServices.size() > 0) {
				String megaServiceName = megaServices.get(0);
				Set<DefaultEdge> incomingEdges = microservicesGraph.incomingEdgesOf(megaServiceName);
				Iterator<DefaultEdge> incomingEdgesIterator = incomingEdges.iterator();
				List<DefaultEdge> edgesToRemove = new ArrayList<DefaultEdge>();
				List<String> verticesSource = new ArrayList<String>();
				for (int j = 0; j < incomingEdges.size() / 2; j++) {
					DefaultEdge edgeToRemove = incomingEdgesIterator.next();
					edgesToRemove.add(edgeToRemove);
					verticesSource.add(microservicesGraph.getEdgeSource(edgeToRemove));
				}
				microservicesGraph.removeAllEdges(edgesToRemove);
				String newVertex = microservicesGraph.addVertex();
				Graphs.addIncomingEdges(microservicesGraph, newVertex, verticesSource);
				addedVertices.add(newVertex);
				if (incomingEdges.size() < maxDependencies) {
					megaServices.remove(0);
				} else {
					megaServices.add(newVertex);
				}
			} else {
				VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true, true, true,
						true);
				List<String> vertices = MicroservicesGraphUtil.getRandomVertices(microservicesGraph, 1,
						vertexTypeRestrictions);

				String addedVertex = microservicesGraph.addVertex();
				microservicesGraph = MicroservicesGraphUtil.connectDirectedVerticesRandomly(microservicesGraph,
						addedVertex, vertices.get(0));
				microservicesGraph = keepArchitectureConsistency(microservicesGraph, addedVertex);
			}
		}
		return addedVertices;
	}

	/**
	 * @param microservicesGraph
	 * @param vertexTypeRestrictions
	 * @return
	 */
	private static List<String> findVerticesMegaServices(MicroservicesGraph<String, DefaultEdge> microservicesGraph,
			int maxDependencies, VertexTypeRestrictions vertexTypeRestrictions) {
		List<String> megaServices = new ArrayList<String>();
		if (microservicesGraph != null && microservicesGraph.vertexSet().size() > 0) {
			List<String> allVertices = Graphs.getVertexToIntegerMapping(microservicesGraph).getIndexList();
			for (String vertice : allVertices) {
				if (microservicesGraph.inDegreeOf(vertice) > maxDependencies
						&& vertexTypeRestrictions.testVertexTypeRestrictions(vertice)) {
					megaServices.add(vertice);
				}
			}
		}
		return megaServices;
	}

	/**
	 * @param microservicesGraph
	 */
	private static void simulateArchitectureEvoluionToKeep(MicroservicesGraph<String, DefaultEdge> microservicesGraph) {
		if (microservicesGraph != null) {
			if (microservicesGraph.getArchitectureEvolutionIssue() == ArchitectureEvolutionIssue.MEGA_SERVICE) {
				int[] verticesToAdd = microservicesGraph.getVerticesToAddInReleases();

				if (verticesToAdd != null && verticesToAdd.length > 0) {
					for (int i = 0; i < verticesToAdd.length; i++) {
						if (verticesToAdd[i] > 0) {
							VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true,
									true, true, true);
							List<String> vertices = MicroservicesGraphUtil.getRandomVertices(microservicesGraph,
									verticesToAdd[i], vertexTypeRestrictions);
							for (int j = 0; j < verticesToAdd[i]; j++) {
								String addedVertex = microservicesGraph.addVertex();
								MicroservicesGraphUtil.connectDirectedVerticesRandomly(microservicesGraph, addedVertex,
										vertices.get(j));
								keepArchitectureConsistency(microservicesGraph, addedVertex);
							}
						}
						int releaseNumber = i + 1;
						MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, microservicesGraph.getPathName(),
								"release-" + String.format("%02d", releaseNumber));
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
		Random rd = new Random();
		// if new vertex added is calling any other existent service
		if (microservicesGraph.outDegreeOf(addedVertex) > 0) {
			// we will include an API Gateway call
			if (existsVertexType(microservicesGraph, VertexType.API_GATEWAY)) {
				microservicesGraph.addEdge(VertexType.API_GATEWAY, addedVertex);
			}
		}
		if (existsVertexType(microservicesGraph, VertexType.SERVICE_REGISTRY)) {
			//microservicesGraph.addEdge(VertexType.SERVICE_REGISTRY, addedVertex);
			microservicesGraph.addEdge(addedVertex, VertexType.SERVICE_REGISTRY);
		}
		if (existsVertexType(microservicesGraph, VertexType.EVENT_DRIVEN)) {
			// I'm not sure about what to do here
		}
		if (existsVertexType(microservicesGraph, VertexType.DISTRIBUTED_TRACING)) {
			if (rd.nextInt(2) == 0) {
				microservicesGraph.addEdge(addedVertex, VertexType.DISTRIBUTED_TRACING);
			}
		}
		if (existsVertexType(microservicesGraph, VertexType.EXTERNALIZED_CONFIGURATION)) {
			if (rd.nextInt(2) == 0) {
				microservicesGraph.addEdge(addedVertex, VertexType.EXTERNALIZED_CONFIGURATION);
			}
		}

		return microservicesGraph;
	}

	/**
	 * @param microservicesGraph
	 * @param apiGateway
	 * @return
	 */
	private static boolean existsVertexType(MicroservicesGraph<String, DefaultEdge> microservicesGraph,
			String vertexType) {
		boolean found = false;
		switch (vertexType) {
		case VertexType.API_GATEWAY:
		case VertexType.SERVICE_REGISTRY:
		case VertexType.EVENT_DRIVEN:
		case VertexType.DISTRIBUTED_TRACING:
		case VertexType.EXTERNALIZED_CONFIGURATION:

			found = microservicesGraph.containsVertex(vertexType);
			break;
		case VertexType.API_COMPOSITION:
			int cont = 0;
			List<String> allVertices = Graphs.getVertexToIntegerMapping(microservicesGraph).getIndexList();
			while (!found && cont < allVertices.size()) {
				if (allVertices.get(cont).startsWith(VertexType.API_COMPOSITION)) {
					found = true;
				}
			}
		default:
			break;
		}

		return found;
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

				if (verticesToAdd != null && verticesToAdd.length > 0) {
					for (int i = 0; i < verticesToAdd.length; i++) {
						List<String> addedVertices = addNewVerticesToOneTargetVertice(microservicesGraph,
								verticesToAdd[i], maxIncomingDegreeVertex);
						for (String addedVertex : addedVertices) {
							keepArchitectureConsistency(microservicesGraph, addedVertex);
						}
						int releaseNumber = i + 1;
						MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, microservicesGraph.getPathName(),
								"release-" + String.format("%02d", releaseNumber));
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
	private static List<String> addNewVerticesToOneTargetVertice(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, int verticesToAdd, String targetVertex) {
		List<String> addedVertices = new ArrayList<String>();
		for (int i = 0; i < verticesToAdd; i++) {
			String addedVertex = microservicesGraph.addVertex();
			microservicesGraph.addEdge(addedVertex, targetVertex);
			addedVertices.add(addedVertex);
		}
		return addedVertices;
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
					microservicesGraph = applyMegaServiceIssue(microservicesGraph, evolutionParameters);
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
	 * @param microservicesGraph
	 * @return
	 */
	private static MicroservicesGraph<String, DefaultEdge> applyMegaServiceIssue(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, GraphEvolutionParameters evolutionParameters) {
		MicroservicesGraph<String, DefaultEdge> returnedGraph = microservicesGraph;
		Random rd = new Random();
		int growthRate = rd
				.nextInt(evolutionParameters.getArchitectureEvolutionGrowthRateMaximum()
						- evolutionParameters.getArchitectureEvolutionGrowthRateMininum())
				+ evolutionParameters.getArchitectureEvolutionGrowthRateMininum();
		int graphSize = microservicesGraph.vertexSet().size();
		int megaServiceDependents = (int) Math.ceil(graphSize * ((double) growthRate / 100));
		VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true, true, true, true);
		String verticeMaxIncomingDegree = findMaxIncomingDegreeVertex(microservicesGraph, vertexTypeRestrictions);
		int currentDegree = microservicesGraph.inDegreeOf(verticeMaxIncomingDegree);
		Set<DefaultEdge> edgesAlreadyIncoming = microservicesGraph.incomingEdgesOf(verticeMaxIncomingDegree);
		vertexTypeRestrictions.addExtraRestrictions(verticeMaxIncomingDegree);
		Iterator<DefaultEdge> edgesIterator = edgesAlreadyIncoming.iterator();
		while (edgesIterator.hasNext()) {
			vertexTypeRestrictions.addExtraRestrictions(microservicesGraph.getEdgeSource(edgesIterator.next()));
		}
		int servicesToChange = megaServiceDependents - currentDegree;
		List<String> servicesToChangeList = MicroservicesGraphUtil.getRandomVertices(microservicesGraph,
				servicesToChange, vertexTypeRestrictions);

		for (int i = 0; i < servicesToChange; i++) {
			removeRandomEdges(microservicesGraph, 1, servicesToChangeList.get(i));
		}

		Graphs.addIncomingEdges(microservicesGraph, verticeMaxIncomingDegree, servicesToChangeList);
		// export graph changed with the mega-service issue to substitute the first release
		MicroservicesGraphUtil.exportGraphToFile(microservicesGraph, microservicesGraph.getPathName(),
				"release-" + String.format("%02d", 0));

		return returnedGraph;
	}

	/**
	 * @param microservicesGraph
	 * @param edgesQuantitytity
	 * @param verticeName
	 */
	private static void removeRandomEdges(MicroservicesGraph<String, DefaultEdge> microservicesGraph, int edgesQuantity,
			String verticeName) {
		Set<DefaultEdge> outEdges = microservicesGraph.outgoingEdgesOf(verticeName);
		VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, true, true, true, true);
		DefaultEdge outEdgeToRemove = findFirstOutEdgeToAnyCommonService(microservicesGraph, outEdges,
				vertexTypeRestrictions);
		if (outEdgeToRemove != null) {
			microservicesGraph.removeEdge(outEdgeToRemove);
		} else {
			Set<DefaultEdge> inEdges = microservicesGraph.incomingEdgesOf(verticeName);
			DefaultEdge inEdgeToRemove = findFirstInEdgeToAnyCommonService(microservicesGraph, inEdges,
					vertexTypeRestrictions);
			if (inEdgeToRemove != null) {
				microservicesGraph.removeEdge(inEdgeToRemove);
			}
		}
	}

	/**
	 * @param microservicesGraph
	 * @param inEdges
	 * @param vertexTypeRestrictions
	 * @return
	 */
	private static DefaultEdge findFirstInEdgeToAnyCommonService(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, Set<DefaultEdge> inEdges,
			VertexTypeRestrictions vertexTypeRestrictions) {
		DefaultEdge firstEdge = null;

		for (DefaultEdge edge : inEdges) {
			String edgeSource = microservicesGraph.getEdgeSource(edge);
			if (vertexTypeRestrictions.testVertexTypeRestrictions(edgeSource)) {
				firstEdge = edge;
			}
		}
		return firstEdge;
	}

	/**
	 * @param outEdges
	 * @return
	 */
	private static DefaultEdge findFirstOutEdgeToAnyCommonService(
			MicroservicesGraph<String, DefaultEdge> microservicesGraph, Set<DefaultEdge> outEdges,
			VertexTypeRestrictions vertexTypeRestrictions) {
		DefaultEdge firstEdge = null;

		for (DefaultEdge edge : outEdges) {
			String edgeTarget = microservicesGraph.getEdgeTarget(edge);
			if (vertexTypeRestrictions.testVertexTypeRestrictions(edgeTarget)) {
				firstEdge = edge;
			}
		}
		return firstEdge;
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

}
