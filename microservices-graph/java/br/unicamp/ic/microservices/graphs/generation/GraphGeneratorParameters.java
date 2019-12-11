package br.unicamp.ic.microservices.graphs.generation;

import java.util.Random;

import com.google.gson.annotations.Expose;

import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphScenario;
import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphSize;
import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphStructure;

/**
 * @author Daniel R. F. Apolinario
 * 
 *         Contains all the parameters for the graph generation. The generated
 *         graph simulate the services dependencies of a microservice-based
 *         application
 *
 */
public class GraphGeneratorParameters {

	public static final int SMALL_GRAPH_MIN = 5;
	public static final int SMALL_GRAPH_MAX = 10;

	public static final int MEDIUM_GRAPH_MIN = 11;
	public static final int MEDIUM_GRAPH_MAX = 25;

	public static final int BIG_GRAPH_MIN = 26;
	public static final int BIG_GRAPH_MAX = 60;
	
	public static final int API_COMPOSITION_AGGREGATED_MIN = 2;
	public static final int API_COMPOSITION_AGGREGATED_MAX = 10;
	
	@Expose
	private GraphStructure graphStructure;

	@Expose
	private GraphSize graphSize;
	
	@Expose
	private GraphScenario graphScenario;
	
	@Expose
	private int verticesNumber;
	
	private int serviceRegistryProbability =Integer.MIN_VALUE;
	
	private int distributedTracingProbability = Integer.MIN_VALUE;
	
	private int apiGatewayProbability = Integer.MIN_VALUE;
	
	private int apiCompositionProbability = Integer.MIN_VALUE;
	
	private int apiCompositionAggregatedProportion = Integer.MIN_VALUE;
	
	private int eventDrivingProbability = Integer.MIN_VALUE;
	
	private int eventDrivingProportionProbability = Integer.MIN_VALUE;
	
	private int externalizedConfigurationProbability = Integer.MIN_VALUE;
	
	private int externalizedConfigProportion = Integer.MIN_VALUE;

	public GraphStructure getGraphStructure() {
		return graphStructure;
	}

	public void setGraphStructure(GraphStructure graphStructure) {
		this.graphStructure = graphStructure;
	}

	public GraphSize getGraphSize() {
		return graphSize;
	}

	public void setGraphSize(GraphSize graphSize) {
		this.graphSize = graphSize;
	}

	/**
	 * @return the graphScenario
	 */
	public GraphScenario getGraphScenario() {
		return graphScenario;
	}

	/**
	 * @param graphScenario the graphScenario to set
	 */
	public void setGraphScenario(GraphScenario graphScenario) {
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

	/**
	 * @return the apiCompositionAggregatedProportion
	 */
	public int getApiCompositionAggregatedProportion() {
		return apiCompositionAggregatedProportion;
	}

	/**
	 * @param apiCompositionAggregatedProportion the apiCompositionAggregatedProportion to set
	 */
	public void setApiCompositionAggregatedProportion(int apiCompositionAggregatedProportion) {
		this.apiCompositionAggregatedProportion = apiCompositionAggregatedProportion;
	}

	/**
	 * @return the externalizedConfigProportion
	 */
	public int getExternalizedConfigProportion() {
		return externalizedConfigProportion;
	}

	/**
	 * @param externalizedConfigProportion the externalizedConfigProportion to set
	 */
	public void setExternalizedConfigProportion(int externalizedConfigProportion) {
		this.externalizedConfigProportion = externalizedConfigProportion;
	}

	public void calculateVerticesNumber() {
		int verticesNumber = 0;
		Random rdVertNumber = new Random();
		switch (this.graphSize) {
		case SMALL:
			verticesNumber = SMALL_GRAPH_MIN + rdVertNumber.nextInt(SMALL_GRAPH_MAX - SMALL_GRAPH_MIN);
			break;
		case MEDIUM:
			verticesNumber = MEDIUM_GRAPH_MIN + rdVertNumber.nextInt(MEDIUM_GRAPH_MAX - MEDIUM_GRAPH_MIN);
			break;
		case BIG:
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
		result = prime * result + apiCompositionAggregatedProportion;
		result = prime * result + apiCompositionProbability;
		result = prime * result + apiGatewayProbability;
		result = prime * result + distributedTracingProbability;
		result = prime * result + eventDrivingProbability;
		result = prime * result + eventDrivingProportionProbability;
		result = prime * result + externalizedConfigProportion;
		result = prime * result + externalizedConfigurationProbability;
		result = prime * result + ((graphSize == null) ? 0 : graphSize.hashCode());
		result = prime * result + ((graphStructure == null) ? 0 : graphStructure.hashCode());
		result = prime * result + serviceRegistryProbability;
		result = prime * result + verticesNumber;
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
		if (apiCompositionAggregatedProportion != other.apiCompositionAggregatedProportion)
			return false;
		if (apiCompositionProbability != other.apiCompositionProbability)
			return false;
		if (apiGatewayProbability != other.apiGatewayProbability)
			return false;
		if (distributedTracingProbability != other.distributedTracingProbability)
			return false;
		if (eventDrivingProbability != other.eventDrivingProbability)
			return false;
		if (eventDrivingProportionProbability != other.eventDrivingProportionProbability)
			return false;
		if (externalizedConfigProportion != other.externalizedConfigProportion)
			return false;
		if (externalizedConfigurationProbability != other.externalizedConfigurationProbability)
			return false;
		if (graphSize != other.graphSize)
			return false;
		if (graphStructure != other.graphStructure)
			return false;
		if (serviceRegistryProbability != other.serviceRegistryProbability)
			return false;
		if (verticesNumber != other.verticesNumber)
			return false;
		return true;
	}



}
