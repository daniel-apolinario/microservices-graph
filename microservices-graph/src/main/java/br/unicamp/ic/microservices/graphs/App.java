package br.unicamp.ic.microservices.graphs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.util.SupplierUtil;
import org.jgrapht.util.VertexToIntegerMapping;

/**
 * @author Daniel R. F. Apolinario
 *
 *         Main class to generate graphs that represent microservices
 *         dependencies.
 * 
 */
public class App {

	public static void main(String[] args) {

		// Número de grafos a serem gerados.
		int n = 5;

		List<GraphGeneratorParameters> grGeParams = new ArrayList<GraphGeneratorParameters>();
		for (int i = 0; i < n; i++) {
			GraphGeneratorParameters graphGeneratorParameters = generateGraphParameters();
			grGeParams.add(graphGeneratorParameters);
		}

		int graphCount = 1;
		for (GraphGeneratorParameters graphGenParameters : grGeParams) {
			Graph<String, DefaultEdge> graph = generateGraph(graphGenParameters, graphCount);
			exportGraphToFile(graph, Integer.toString(graphCount));
			graphCount++;
		}
	}

	/**
	 * Method to export a Graph object to a DOT file format
	 * 
	 * @param graph
	 */
	private static void exportGraphToFile(Graph<String, DefaultEdge> graph, String graphName) {
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
				writer = new FileWriter("/home/daniel/Downloads/grafo-" + graphName + ".dot");
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
	 * @param graphGenParameters
	 * @return Graph - generated graph according the configured parameters
	 */
	private static Graph<String, DefaultEdge> generateGraph(GraphGeneratorParameters graphGenParameters,
			int graphCount) {

		Graph<String, DefaultEdge> graph = null;
		switch (graphGenParameters.getGraphStructure()) {

		case GraphGeneratorParameters.RANDOM_GRAPH:
			graph = generateRandomGraph(graphGenParameters);
			break;
		case GraphGeneratorParameters.BARABASI_ALBERT_GRAPH:
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
			GraphGeneratorParameters graphGenParameters, int graphCount) {
		Graph<String, DefaultEdge> updatedGraph = null;

		updatedGraph = applyAPIGateway(graph, graphGenParameters);
		exportGraphToFile(updatedGraph, graphCount + "-gtw");

		updatedGraph = applyServiceRegistry(updatedGraph, graphGenParameters);
		exportGraphToFile(updatedGraph, graphCount + "-reg");

		updatedGraph = applyEventDriven(updatedGraph, graphGenParameters);

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
			String messageBrokerVertex = "MB";
			updatedGraph.addVertex(messageBrokerVertex);
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (!vertex.equals(messageBrokerVertex) && !vertex.equals("REG") && !vertex.equals("GTW")) {
					if (rdDistProb.nextInt(10) <= eDDProb) {
						Set<DefaultEdge> outgoingEdges = updatedGraph.outgoingEdgesOf(vertex);
						List<String> targetVertices = new ArrayList<String>();
						List<DefaultEdge> edgesToRemove = new ArrayList<DefaultEdge>();
						if (!outgoingEdges.isEmpty()) {
							for (DefaultEdge outEdge : outgoingEdges) {
								String targetVertex = updatedGraph.getEdgeTarget(outEdge);
								// we should not consider REG vertex as vertex
								if (!targetVertex.equals("REG")) {
									targetVertices.add(targetVertex);
									edgesToRemove.add(outEdge);
								}
							}

							updatedGraph.addEdge(vertex, messageBrokerVertex);
							for (String targetVertex : targetVertices) {
								updatedGraph.addEdge(messageBrokerVertex, targetVertex);
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
			String apiGatewayVertex = "GTW";
			updatedGraph.addVertex(apiGatewayVertex);
			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (!vertex.equals(apiGatewayVertex)) {
					// The API Gateway will connect with all vertices available to external
					// Our premise is all the vertices that they don`t have incoming edges are
					// available to external only
					if (updatedGraph.incomingEdgesOf(vertex).isEmpty()) {
						updatedGraph.addEdge(apiGatewayVertex, vertex);
					}
				}
			}
			// Check if gateway is isolated, in case of none all the vertices have incoming
			// edges.
			// In this case, we will selected a percentage of the vertices to be called by
			// gateway
			System.out.println("gateway degree = "+ updatedGraph.degreeOf(apiGatewayVertex));
			if (updatedGraph.degreeOf(apiGatewayVertex) == 0) {
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
						updatedGraph.addEdge(apiGatewayVertex, vertexSelected);
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
			String serviceRegistryVertex = "REG";
			updatedGraph.addVertex(serviceRegistryVertex);

			Iterator<String> it = updatedGraph.vertexSet().iterator();
			while (it.hasNext()) {
				String vertex = (String) it.next();
				if (!vertex.equals(serviceRegistryVertex) && !vertex.equals("GTW")) {
					updatedGraph.addEdge(vertex, serviceRegistryVertex);
					updatedGraph.addEdge(serviceRegistryVertex, vertex);
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
		// The random graph will have 10 nodes and the probability 0.2 to have edge
		// between 2 nodes
		GnpRandomGraphGenerator<String, DefaultEdge> randomGenerator = new GnpRandomGraphGenerator<>(
				graphGenParameters.getVerticesNumber(), 0.2);

		// Use the GnpRandomGraphGenerator object to make a random graph
		randomGenerator.generateGraph(randomGraph);

		return randomGraph;
	}

	public static GraphGeneratorParameters generateGraphParameters() {
		GraphGeneratorParameters gParams = new GraphGeneratorParameters();
		Random rdGrParams = new Random();
		// só há 2 opções para a estrutura do grafo
		gParams.setGraphStructure(rdGrParams.nextInt(2));
		// há 3 opções de tamanho de grafo
		gParams.setGraphSize(rdGrParams.nextInt(3));
		gParams.calculateVerticesNumber();
		gParams.setApiCompositionProbability(0);
		gParams.setApiGatewayProbability(100);
		gParams.setCqrsProbability(0);
		gParams.setDistributedTracingProbability(0);
		gParams.setEventDrivingProbability(50);
		gParams.setEventDrivingProportionProbability(50);
		gParams.setExternalizedConfigurationProbability(0);
		gParams.setServiceRegistryProbability(100);

		return gParams;
	}

}
