/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * @author Daniel R. F. Apolinario
 * @param <E>
 * @param <V>
 *
 */
public class MicroservicesGraph<V, E> extends SimpleDirectedGraph<V, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum InitialArchitectureState {
		GOOD, BAD;
	}

	public enum ArchitectureEvolutionTarget {
		KEEP, BETTER, WORSE;
	}

	public enum ArchitectureEvolutionIssue {
		MEGA_SERVICE, CYCLIC_DEPENDENCY;
	}

	private InitialArchitectureState initialArchitectureState;

	private ArchitectureEvolutionTarget architectureEvolutionTarget;

	private ArchitectureEvolutionIssue architectureEvolutionIssue;

	private double architectureEvolutionGrowthRate;

	/**
	 * @param edgeClass
	 */
	public MicroservicesGraph(Class<? extends E> edgeClass) {
		super(edgeClass);
	}

	public InitialArchitectureState getInitialArchitectureState() {
		return initialArchitectureState;
	}

	public void setInitialArchitectureState(InitialArchitectureState initialArchitectureState) {
		this.initialArchitectureState = initialArchitectureState;
	}

	public ArchitectureEvolutionTarget getArchitectureEvolutionTarget() {
		return architectureEvolutionTarget;
	}

	public void setArchitectureEvolutionTarget(ArchitectureEvolutionTarget architectureEvolutionTarget) {
		this.architectureEvolutionTarget = architectureEvolutionTarget;
	}

	public ArchitectureEvolutionIssue getArchitectureEvolutionIssue() {
		return architectureEvolutionIssue;
	}

	public void setArchitectureEvolutionIssue(ArchitectureEvolutionIssue architectureEvolutionIssue) {
		this.architectureEvolutionIssue = architectureEvolutionIssue;
	}

	public double getArchitectureEvolutionGrowthRate() {
		return architectureEvolutionGrowthRate;
	}

	public void setArchitectureEvolutionGrowthRate(double architectureEvolutionGrowthRate) {
		this.architectureEvolutionGrowthRate = architectureEvolutionGrowthRate;
	}
}
