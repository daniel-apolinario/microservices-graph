package br.unicamp.ic.microservices.graphs;

import java.util.Random;

/**
 * @author Daniel R. F. Apolinario
 * 
 *         Contains all the parameters for the graph generation. The generated
 *         graph simulate the services dependencies of a microservice-based
 *         application
 *
 */
public class GraphGeneratorParameters {

	public static final int RANDOM_GRAPH = 0;
	public static final int BARABASI_ALBERT_GRAPH = 1;

	public static final int SMALL_GRAPH = 0;
	public static final int MEDIUM_GRAPH = 1;
	public static final int BIG_GRAPH = 2;

	public static final int SMALL_GRAPH_MIN = 5;
	public static final int SMALL_GRAPH_MAX = 10;

	public static final int MEDIUM_GRAPH_MIN = 11;
	public static final int MEDIUM_GRAPH_MAX = 25;

	public static final int BIG_GRAPH_MIN = 26;
	public static final int BIG_GRAPH_MAX = 60;

	private int graphStructure;

	private int graphSize;

	private int graphScenario;

	private int verticesNumber;
	
	private int serviceRegistryProbability =Integer.MIN_VALUE;
	
	private int distributedTracingProbability = Integer.MIN_VALUE;
	
	private int apiGatewayProbability = Integer.MIN_VALUE;
	
	private int apiCompositionProbability = Integer.MIN_VALUE;
	
	private int cqrsProbability = Integer.MIN_VALUE;
	
	private int eventDrivingProbability = Integer.MIN_VALUE;
	
	private int eventDrivingProportionProbability = Integer.MIN_VALUE;
	
	private int externalizedConfigurationProbability = Integer.MIN_VALUE;

	public int getGraphStructure() {
		return graphStructure;
	}

	public void setGraphStructure(int graphStructure) {
		this.graphStructure = graphStructure;
	}

	public int getGraphSize() {
		return graphSize;
	}

	public void setGraphSize(int graphSize) {
		this.graphSize = graphSize;
	}

	public int getGraphScenario() {
		return graphScenario;
	}

	public void setGraphScenario(int graphScenario) {
		this.graphScenario = graphScenario;
	}

	/**
	 * @return the verticesNumber
	 */
	public int getVerticesNumber() {
		return verticesNumber;
	}

	/**
	 * @param verticesNumber the verticesNumber to set
	 */
	public void setVerticesNumber(int verticesNumber) {
		this.verticesNumber = verticesNumber;
	}

	public int getServiceRegistryProbability() {
		return serviceRegistryProbability;
	}

	public void setServiceRegistryProbability(int serviceRegistryProbability) {
		this.serviceRegistryProbability = serviceRegistryProbability;
	}

	public int getDistributedTracingProbability() {
		return distributedTracingProbability;
	}

	public void setDistributedTracingProbability(int distributedTracingProbability) {
		this.distributedTracingProbability = distributedTracingProbability;
	}

	public int getApiGatewayProbability() {
		return apiGatewayProbability;
	}

	public void setApiGatewayProbability(int apiGatewayProbability) {
		this.apiGatewayProbability = apiGatewayProbability;
	}

	public int getApiCompositionProbability() {
		return apiCompositionProbability;
	}

	public void setApiCompositionProbability(int apiCompositionProbability) {
		this.apiCompositionProbability = apiCompositionProbability;
	}

	public int getCqrsProbability() {
		return cqrsProbability;
	}

	public void setCqrsProbability(int cqrsProbability) {
		this.cqrsProbability = cqrsProbability;
	}

	public int getEventDrivingProbability() {
		return eventDrivingProbability;
	}

	public void setEventDrivingProbability(int eventSourcingProbability) {
		this.eventDrivingProbability = eventSourcingProbability;
	}

	public int getExternalizedConfigurationProbability() {
		return externalizedConfigurationProbability;
	}

	public void setExternalizedConfigurationProbability(int externalizedConfigurationProbability) {
		this.externalizedConfigurationProbability = externalizedConfigurationProbability;
	}

	/**
	 * @return the eventDrivingProportionProbability
	 */
	public int getEventDrivingProportionProbability() {
		return eventDrivingProportionProbability;
	}

	/**
	 * @param eventDrivingProportionProbability the eventDrivingProportionProbability to set
	 */
	public void setEventDrivingProportionProbability(int eventDrivingProportionProbability) {
		this.eventDrivingProportionProbability = eventDrivingProportionProbability;
	}

	public void calculateVerticesNumber() {
		int verticesNumber = 0;
		Random rdVertNumber = new Random();
		switch (this.graphSize) {
		case SMALL_GRAPH:
			verticesNumber = SMALL_GRAPH_MIN + rdVertNumber.nextInt(SMALL_GRAPH_MAX - SMALL_GRAPH_MIN);
			break;
		case MEDIUM_GRAPH:
			verticesNumber = MEDIUM_GRAPH_MIN + rdVertNumber.nextInt(MEDIUM_GRAPH_MAX - MEDIUM_GRAPH_MIN);
			break;
		case BIG_GRAPH:
			verticesNumber = BIG_GRAPH_MIN + rdVertNumber.nextInt(BIG_GRAPH_MAX - BIG_GRAPH_MIN);
			break;
		default:
			break;
		}
		this.verticesNumber = verticesNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + graphScenario;
		result = prime * result + graphSize;
		result = prime * result + graphStructure;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphGeneratorParameters other = (GraphGeneratorParameters) obj;
		if (graphScenario != other.graphScenario)
			return false;
		if (graphSize != other.graphSize)
			return false;
		if (graphStructure != other.graphStructure)
			return false;
		return true;
	}

}
