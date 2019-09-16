/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.util.List;

import org.jgrapht.graph.DefaultEdge;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroserviceApplication implements Application {

	private String name;

	private MicroservicesGraph<String, DefaultEdge> dependenciesGraph;

	private List<Metric<String, Double>> metrics;

	private List<Microservice> microservices;

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.ic.microservices.graphs.Application#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.ic.microservices.graphs.Application#setName()
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dependenciesGraph
	 */
	public MicroservicesGraph<String, DefaultEdge> getDependenciesGraph() {
		return dependenciesGraph;
	}

	/**
	 * @param dependenciesGraph the dependenciesGraph to set
	 */
	public void setDependenciesGraph(MicroservicesGraph<String, DefaultEdge> dependenciesGraph) {
		this.dependenciesGraph = dependenciesGraph;
	}

	public List<Metric<String, Double>> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric<String, Double>> metrics) {
		this.metrics = metrics;
	}

	public List<Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(List<Microservice> microservices) {
		this.microservices = microservices;
	}

}
