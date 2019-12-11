package br.unicamp.ic.microservices.graphs.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.paukov.combinatorics3.Generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig;
import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphScenario;
import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphSize;
import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphStructure;
import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.graphs.VertexType;
import br.unicamp.ic.microservices.graphs.VertexTypeRestrictions;

/**
 * @author Daniel R. F. Apolinario
 *
 *         Main class to generate graphs that represent microservices
 *         dependencies.
 * 
 */
public class App {

	private static final String PATH_NAME = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";
	private static final String APP_NAME = "application";
	private static final String RELEASE_NAME = "release";
	private static final String EXPERIMENTAL_DESIGN_CONFIG_FILE = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/experimentDesignConfig.json";

	public static void main(String[] args) {

		// get the experimental design to create the graphs
		List<List> experimentalDesign = getExperimentalTreatments(EXPERIMENTAL_DESIGN_CONFIG_FILE);

		if (experimentalDesign != null && !experimentalDesign.isEmpty()) {
			List<GraphGeneratorParameters> grGeParams = new ArrayList<GraphGeneratorParameters>();
			// create a list for treatmens to be saved in a json file
			List<ExperimentTreatment> treatmentsList = new ArrayList<ExperimentTreatment>();

			for (List treatment : experimentalDesign) {
				GraphGeneratorParameters graphGeneratorParameters = generateGraphParameters(treatment);
				grGeParams.add(graphGeneratorParameters);
			}

			int appNumber = 1;
			for (GraphGeneratorParameters graphGenParameters : grGeParams) {
				File newDirectory = new File(
						MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber));
				newDirectory.mkdir();

				ExperimentTreatment treatment = new ExperimentTreatment(
						MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
						graphGenParameters);
				treatmentsList.add(treatment);

				Graph<String, DefaultEdge> graph = generateGraph(graphGenParameters, appNumber);
				// Creates a new directory to put all the graph files inside it

				MicroservicesGraphUtil.exportGraphToFile(graph,
						MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
						MicroservicesGraphUtil.getApplicationFileName(RELEASE_NAME, 0, Integer.MIN_VALUE, null));
				appNumber++;
			}

			exportExperimentTreatments(treatmentsList);
		}
	}

	/**
	 * @param treatmentsList
	 */
	private static void exportExperimentTreatments(List<ExperimentTreatment> treatmentsList) {
		try (FileOutputStream fos = new FileOutputStream(PATH_NAME + "/experimentTreatments.json");
				OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

			gson.toJson(treatmentsList, isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param experimentalDesignConfigFile
	 * @return
	 */
	private static ExperimentDesignConfig getExperimentalDesign(String experimentalDesignConfigFile) {
		ExperimentDesignConfig experimentDesignConfig = null;

		File file = new File(experimentalDesignConfigFile);

		if (file.exists()) {
			Gson gson = new GsonBuilder().create();

			try (Reader targetReader = new FileReader(file)) {
				experimentDesignConfig = gson.fromJson(targetReader, ExperimentDesignConfig.class);
				targetReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return experimentDesignConfig;
	}

	/**
	 * 
	 * @param experimentalDesignConfigFile
	 * @return
	 */
	private static List<List> getExperimentalTreatments(String experimentalDesignConfigFile) {
		List<List> result = new ArrayList<>();

		ExperimentDesignConfig experimentDesignConfig = getExperimentalDesign(experimentalDesignConfigFile);

		if (experimentDesignConfig != null) {
			List<List> design = (List<List>) Generator
					.cartesianProduct(experimentDesignConfig.getGraphStructureFactor(),
							experimentDesignConfig.getGraphSizeFactor(),
							experimentDesignConfig.getGraphScenarioFactor())
					.stream().collect(Collectors.toList());

			if (design != null && !design.isEmpty()) {
				for (int i = 0; i < experimentDesignConfig.getReplicasQuantity(); i++) {
					result.addAll(new ArrayList(design));
				}
			}
		}

		return result;
	}

	/**
	 * @param graphGenParameters
	 * @return Graph - generated graph according the configured parameters
	 */
	private static Graph<String, DefaultEdge> generateGraph(GraphGeneratorParameters graphGenParameters,
			int graphCount) {

		Graph<String, DefaultEdge> graph = null;
		switch (graphGenParameters.getGraphStructure()) {

		case RANDOM_GRAPH:
			graph = generateRandomGraph(graphGenParameters);
			break;
		case BARABASI_ALBERT_GRAPH:
			graph = generateBarabasiAlbertGraph(graphGenParameters);
			break;

		default:
			break;
		}
		// apply microservice design patterns
		graph = applyMicroserviceDesignPatterns(graph, graphGenParameters, graphCount);

		return graph;
	}

	/**
	 * Method to apply common microservice design patterns according a probability
	 * 
	 * @param graph - graph to modify
	 * @return A updated graph object
	 */
	private static Graph<String, DefaultEdge> applyMicroserviceDesignPatterns(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters, int appNumber) {
		Graph<String, DefaultEdge> updatedGraph = graph;

//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "ini"));

		updatedGraph = applyAPIComposition(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "cps"));

		updatedGraph = applyAPIGateway(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "gtw"));

		updatedGraph = applyServiceRegistry(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "reg"));

		updatedGraph = applyEventDriven(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "evD"));

		updatedGraph = applyExternalizedConfiguration(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "cfg"));

		updatedGraph = applyDistributedTracing(updatedGraph, graphGenParameters);
//		MicroservicesGraphUtil.exportGraphToFile(updatedGraph,
//				MicroservicesGraphUtil.getExportCompletePath(PATH_NAME, APP_NAME, appNumber),
//				MicroservicesGraphUtil.getApplicationFileName(APP_NAME, appNumber, Integer.MIN_VALUE, "trc"));

		return updatedGraph;
	}

	/**
	 * @param updatedGraph
	 * @param graphGenParameters
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyDistributedTracing(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rd = new Random();
		int prob = 0;
		if (graphGenParameters.getDistributedTracingProbability() != Integer.MIN_VALUE) {
			prob = graphGenParameters.getDistributedTracingProbability() / 10;
		}

		if (rd.nextInt(10) <= prob) {
			List<String> allVertices = Graphs.getVertexToIntegerMapping(updatedGraph).getIndexList();
			VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(false, true, false, true, false,
					true);
			updatedGraph.addVertex(VertexType.DISTRIBUTED_TRACING);
			for (String vertex : allVertices) {
				if (vertexTypeRestrictions.testVertexTypeRestrictions(vertex)) {
					updatedGraph.addEdge(vertex, VertexType.DISTRIBUTED_TRACING);
				}
			}
			if (updatedGraph.containsVertex(VertexType.SERVICE_REGISTRY)) {
				updatedGraph.addEdge(VertexType.DISTRIBUTED_TRACING, VertexType.SERVICE_REGISTRY);
			}
		}
		return updatedGraph;

	}

	/**
	 * @param updatedGraph
	 * @param graphGenParameters
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyExternalizedConfiguration(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rd = new Random();
		int prob = 0;
		if (graphGenParameters.getExternalizedConfigurationProbability() != Integer.MIN_VALUE) {
			prob = graphGenParameters.getExternalizedConfigurationProbability() / 10;
		}

		if (rd.nextInt(10) <= prob) {
			List<String> sourceVerticesToExternalizedConfig = MicroservicesGraphUtil.getPercentualRandomVertices(
					updatedGraph, graphGenParameters.getExternalizedConfigProportion(),
					new VertexTypeRestrictions(true, true, false, true, true, true));
			updatedGraph.addVertex(VertexType.EXTERNALIZED_CONFIGURATION);
			for (String vertex : sourceVerticesToExternalizedConfig) {
				updatedGraph.addEdge(vertex, VertexType.EXTERNALIZED_CONFIGURATION);
			}
			if (updatedGraph.containsVertex(VertexType.SERVICE_REGISTRY)) {
				updatedGraph.addEdge(VertexType.SERVICE_REGISTRY, VertexType.EXTERNALIZED_CONFIGURATION);
			}

		}

		return updatedGraph;
	}

	/**
	 * @param updatedGraph
	 * @param graphGenParameters
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyAPIComposition(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rd = new Random();
		int prob = 0;
		if (graphGenParameters.getApiCompositionProbability() != Integer.MIN_VALUE) {
			prob = graphGenParameters.getApiCompositionProbability() / 10;
		}

		if (rd.nextInt(10) <= prob) {
			List<String> candidateVertices = new ArrayList<String>();
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				// we will catch only the leaf nodes
				if (updatedGraph.outDegreeOf(vertex) == 0) {
					candidateVertices.add(vertex);
				}
			}
			int verticesQty = updatedGraph.vertexSet().size();
			Random rdProp = new Random();
			int verticesToAggregateQty = Math
					.floorDiv(graphGenParameters.getApiCompositionAggregatedProportion() * verticesQty, 100);
			// if leaf nodes is not enough, we will add aleatory nodes
			System.out.println("verticesQty=" + verticesQty);
			System.out.println("verticesToAggregateQty=" + verticesToAggregateQty);
			System.out.println("candidateVertices.size()=" + candidateVertices.size());
			if (verticesToAggregateQty > candidateVertices.size()) {
				List<String> verticesList = Graphs.getVertexToIntegerMapping(updatedGraph).getIndexList();
				int aleatoryIndex = -1;
				while (candidateVertices.size() < verticesToAggregateQty) {
					aleatoryIndex = rdProp.nextInt(verticesList.size());
					String selectedVertex = verticesList.get(aleatoryIndex);
					if (!candidateVertices.contains(selectedVertex)) {
						candidateVertices.add(selectedVertex);
					}
				}
			}
			System.out.println("candidateVertices.size() depois=" + candidateVertices.size());
			int countWhile = 1;
			while (candidateVertices.size() > 0) {
				int aggregateQty = GraphGeneratorParameters.API_COMPOSITION_AGGREGATED_MIN + rdProp.nextInt(
						Math.min(candidateVertices.size(), GraphGeneratorParameters.API_COMPOSITION_AGGREGATED_MAX
								- GraphGeneratorParameters.API_COMPOSITION_AGGREGATED_MIN));
				// we can't leave only 1 element, because isn't possible to aggregate 1 element
				// just.
				if (candidateVertices.size() - aggregateQty < 2) {
					aggregateQty = candidateVertices.size();
				}
				updatedGraph.addVertex(VertexType.API_COMPOSITION + countWhile);
				for (int i = 0; i < aggregateQty; i++) {
					updatedGraph.addEdge(VertexType.API_COMPOSITION + countWhile, candidateVertices.get(0));
					candidateVertices.remove(0);
				}
				countWhile++;
			}

		}
		return updatedGraph;
	}

	/**
	 * @param updatedGraph
	 * @param graphGenParameters
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyEventDriven(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rdProb = new Random();
		Random rdDistProb = new Random();
		int eDProb = 0;
		int eDDProb = 0;
		if (graphGenParameters.getEventDrivingProbability() != Integer.MIN_VALUE) {
			eDProb = graphGenParameters.getEventDrivingProbability() / 10;
		}
		if (graphGenParameters.getEventDrivingProportionProbability() != Integer.MIN_VALUE) {
			eDDProb = graphGenParameters.getEventDrivingProportionProbability() / 10;
		}

		if (rdProb.nextInt(10) <= eDProb) {
			updatedGraph.addVertex(VertexType.EVENT_DRIVEN);
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, false, true, false,
					false);
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (vertexTypeRestrictions.testVertexTypeRestrictions(vertex)) {
					if (rdDistProb.nextInt(10) <= eDDProb) {
						Set<DefaultEdge> outgoingEdges = updatedGraph.outgoingEdgesOf(vertex);
						List<String> targetVertices = new ArrayList<String>();
						List<DefaultEdge> edgesToRemove = new ArrayList<DefaultEdge>();
						if (!outgoingEdges.isEmpty()) {
							for (DefaultEdge outEdge : outgoingEdges) {
								String targetVertex = updatedGraph.getEdgeTarget(outEdge);
								// we should not consider REG vertex as vertex
								if (!targetVertex.equals(VertexType.EVENT_DRIVEN)) {
									targetVertices.add(targetVertex);
									edgesToRemove.add(outEdge);
								}
							}

							updatedGraph.addEdge(vertex, VertexType.EVENT_DRIVEN);
							for (String targetVertex : targetVertices) {
								updatedGraph.addEdge(VertexType.EVENT_DRIVEN, targetVertex);
							}
							updatedGraph.removeAllEdges(edgesToRemove);
						}
					}
				}
			}
		}
		return updatedGraph;
	}

	/**
	 * @param graph
	 * @param graphGenParameters
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyAPIGateway(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rd = new Random();
		int prob = 0;
		if (graphGenParameters.getApiGatewayProbability() != Integer.MIN_VALUE) {
			prob = graphGenParameters.getApiGatewayProbability() / 10;
		}

		if (rd.nextInt(10) <= prob) {
			updatedGraph.addVertex(VertexType.API_GATEWAY);
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (!vertex.equals(VertexType.API_GATEWAY)) {
					// The API Gateway will connect with all vertices available to external
					// Our premise is all the vertices that they don`t have incoming edges are
					// available to external only
					if (updatedGraph.incomingEdgesOf(vertex).isEmpty()) {
						updatedGraph.addEdge(VertexType.API_GATEWAY, vertex);
					}
				}
			}
			// Check if gateway is isolated, in case of none all the vertices have incoming
			// edges.
			// In this case, we will selected a percentage of the vertices to be called by
			// gateway
			System.out.println("gateway degree = " + updatedGraph.degreeOf(VertexType.API_GATEWAY));
			if (updatedGraph.degreeOf(VertexType.API_GATEWAY) == 0) {
				Random rdVertices = new Random();
				int numberOfVertices = updatedGraph.vertexSet().size();
				int externalVertices = Math.floorDiv(numberOfVertices * 30, 100);
				List<String> verticesList = Graphs.getVertexToIntegerMapping(updatedGraph).getIndexList();
				List<String> verticesAlreadyUsed = new ArrayList<String>();
				int count = 0;
				while (count < externalVertices) {
					int vertexIndex = rdVertices.nextInt(numberOfVertices);

					String vertexSelected = verticesList.get(vertexIndex);
					if (!verticesAlreadyUsed.contains(vertexSelected) && !vertexSelected.equals("GTW")) {
						updatedGraph.addEdge(VertexType.API_GATEWAY, vertexSelected);
						verticesAlreadyUsed.add(vertexSelected);
						count++;
					}
				}
			}
		}
		return updatedGraph;
	}

	/**
	 * The Service Registry design pattern is used to services register and discover
	 * the address of the one service. This method will create a new vertex (which
	 * is the service register) and add out an in edges for all the other services
	 * in the graph.
	 * 
	 * @param graph
	 * @return
	 */
	private static Graph<String, DefaultEdge> applyServiceRegistry(Graph<String, DefaultEdge> graph,
			GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> updatedGraph = graph;
		Random rd = new Random();
		int prob = 0;
		if (graphGenParameters.getServiceRegistryProbability() != Integer.MIN_VALUE) {
			prob = graphGenParameters.getServiceRegistryProbability() / 10;
		}

		if (rd.nextInt(10) <= prob) {
			updatedGraph.addVertex(VertexType.SERVICE_REGISTRY);
			VertexTypeRestrictions vertexTypeRestrictions = new VertexTypeRestrictions(true, true, false, false, false,
					false);
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (vertexTypeRestrictions.testVertexTypeRestrictions(vertex)) {
					updatedGraph.addEdge(vertex, VertexType.SERVICE_REGISTRY);
					// The code line below was removed because this is not a functional dependency
					// updatedGraph.addEdge(VertexType.SERVICE_REGISTRY, vertex);
				}
			}
		}

		return updatedGraph;

	}

	public static <T> Iterable<T> iteratorToIterable(Iterator<T> iterator) {
		return () -> iterator;
	}

	/**
	 * @param graphGenParameters
	 */
	private static Graph<String, DefaultEdge> generateBarabasiAlbertGraph(GraphGeneratorParameters graphGenParameters) {
		// Create the VertexFactory so the generator can create vertices
		Supplier<String> vSupplier = new Supplier<String>() {
			private int id = 1;

			@Override
			public String get() {
				return "BA" + id++;
			}
		};

		// Create the graph object
		Graph<String, DefaultEdge> baGraph = new SimpleDirectedGraph<>(vSupplier,
				SupplierUtil.createDefaultEdgeSupplier(), false);

		// Create the BarabasiAlbertGraphGenerator object
		// number of initial nodes, edges for each new node, number of final nodes
		BarabasiAlbertGraphGenerator<String, DefaultEdge> baGenerator = new BarabasiAlbertGraphGenerator<>(2, 1,
				graphGenParameters.getVerticesNumber());

		// Use the BarabasiAlbertGraphGenerator object to make the graph
		baGenerator.generateGraph(baGraph);

		return baGraph;
	}

	/**
	 * @param graphGenParameters
	 */
	private static Graph<String, DefaultEdge> generateRandomGraph(GraphGeneratorParameters graphGenParameters) {
		Graph<String, DefaultEdge> randomGraph;
		Supplier<String> vSupplier = new Supplier<String>() {
			private int id = 1;

			@Override
			public String get() {
				return "RD" + id++;
			}
		};

		// Create the graph object
		randomGraph = new SimpleDirectedGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

		// Create the GnpRandomGraphGenerator object
		// The random graph will have 10 nodes and the probability 0.1 to have edge
		// between 2 nodes
		GnpRandomGraphGenerator<String, DefaultEdge> randomGenerator = new GnpRandomGraphGenerator<>(
				graphGenParameters.getVerticesNumber(), 0.1);

		// Use the GnpRandomGraphGenerator object to make a random graph
		randomGenerator.generateGraph(randomGraph);

		return randomGraph;
	}

	public static GraphGeneratorParameters generateGraphParameters(List treatment) {
		GraphGeneratorParameters gParams = new GraphGeneratorParameters();
		Random rdGrParams = new Random();
		// initially set the treatment paremeters for the experimental design
		gParams.setGraphStructure((GraphStructure) treatment.get(0));
		gParams.setGraphSize((GraphSize) treatment.get(1));
		gParams.setGraphScenario((GraphScenario) treatment.get(2));

		gParams.calculateVerticesNumber();
		gParams.setApiCompositionProbability(50);
		gParams.setApiCompositionAggregatedProportion(20);
		gParams.setApiGatewayProbability(80);
		gParams.setDistributedTracingProbability(50);
		gParams.setEventDrivingProbability(50);
		gParams.setEventDrivingProportionProbability(50);
		gParams.setExternalizedConfigurationProbability(50);
		gParams.setExternalizedConfigProportion(40);
		gParams.setServiceRegistryProbability(100);

		return gParams;
	}

}
