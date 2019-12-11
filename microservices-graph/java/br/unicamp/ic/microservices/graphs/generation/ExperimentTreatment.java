/**
 * 
 */
package br.unicamp.ic.microservices.graphs.generation;

import com.google.gson.annotations.Expose;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class ExperimentTreatment {

	@Expose
	private String applicationName;

	@Expose
	private GraphGeneratorParameters graphGeneratorParameters;
	
	/**
	 * @param applicationName
	 * @param graphGeneratorParameters
	 */
	public ExperimentTreatment(String applicationName, GraphGeneratorParameters graphGeneratorParameters) {
		super();
		this.applicationName = applicationName;
		this.graphGeneratorParameters = graphGeneratorParameters;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public GraphGeneratorParameters getGraphGeneratorParameters() {
		return graphGeneratorParameters;
	}

	public void setGraphGeneratorParameters(GraphGeneratorParameters graphGeneratorParameters) {
		this.graphGeneratorParameters = graphGeneratorParameters;
	}

}
