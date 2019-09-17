/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;

import com.google.gson.annotations.Expose;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroservicesApplication implements Application {

	@Expose
	private String name;

	@Expose
	private List<MicroservicesGraph<String, DefaultEdge>> dependenciesGraphs;

	@Expose
	private List<Metric<Application, String, Double>> applicationMetrics;

	@Expose
	private List<Metric<Microservice, String, Double>> microservicesMetrics;

	@Expose
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
	public List<MicroservicesGraph<String, DefaultEdge>> getDependenciesGraphs() {
		return dependenciesGraphs;
	}

	/**
	 * @param dependenciesGraph the dependenciesGraph to set
	 */
	public void setDependenciesGraphs(List<MicroservicesGraph<String, DefaultEdge>> dependenciesGraphs) {
		this.dependenciesGraphs = dependenciesGraphs;
	}

	public List<Metric<Application, String, Double>> getApplicationMetrics() {
		return applicationMetrics;
	}

	public void setApplicationMetrics(List<Metric<Application, String, Double>> applicationMetrics) {
		this.applicationMetrics = applicationMetrics;
	}

	public List<Metric<Microservice, String, Double>> getMicroservicesMetrics() {
		return microservicesMetrics;
	}

	public void setMicroservicesMetrics(List<Metric<Microservice, String, Double>> microservicesMetrics) {
		this.microservicesMetrics = microservicesMetrics;
	}

	public List<Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(List<Microservice> microservices) {
		this.microservices = microservices;
	}

	public void addMicroservice(Microservice microservice) {
		if (this.microservices == null) {
			this.microservices = new ArrayList<Microservice>();
		}
		this.microservices.add(microservice);
	}

	@Override
	public String toString() {
		return "MicroservicesApplication [name=" + name + ", applicationMetrics=" + applicationMetrics
				+ ", microservicesMetrics=" + microservicesMetrics + ", microservices=" + microservices + "]";
	}
}
