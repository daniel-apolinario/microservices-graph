/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.util.Arrays;
import java.util.function.Supplier;

import org.jgrapht.graph.SimpleDirectedGraph;

import com.google.gson.annotations.Expose;

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

	@Expose
	private String fileName;
	
	private String pathName;
	
	private InitialArchitectureState initialArchitectureState;

	private ArchitectureEvolutionTarget architectureEvolutionTarget;

	private ArchitectureEvolutionIssue architectureEvolutionIssue;

	private double architectureEvolutionGrowthRate;
	
	private int[] verticesToAddInReleases;
	
	private int[] verticesToRemoveInReleases;
	
	private int[] edgesToAddInReleases;
	
	private int[] edgesToRemoveInReleases;
		
	/**
	 * @param edgeClass
	 */
	public MicroservicesGraph(Class<? extends E> edgeClass) {
		super(edgeClass);
	}

	/**
	 * @param vertexSupplier
	 * @param edgeSupplier
	 * @param weighted
	 */
	public MicroservicesGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier) {
		super(vertexSupplier, edgeSupplier, false);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the pathName
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * @param pathName the pathName to set
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
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

	public int[] getVerticesToAddInReleases() {
		return verticesToAddInReleases;
	}

	public void setVerticesToAddInReleases(int[] verticesToAddInReleases) {
		this.verticesToAddInReleases = verticesToAddInReleases;
	}

	public int[] getVerticesToRemoveInReleases() {
		return verticesToRemoveInReleases;
	}

	public void setVerticesToRemoveInReleases(int[] verticesToRemoveInReleases) {
		this.verticesToRemoveInReleases = verticesToRemoveInReleases;
	}

	public int[] getEdgesToAddInReleases() {
		return edgesToAddInReleases;
	}

	public void setEdgesToAddInReleases(int[] edgesToAddInReleases) {
		this.edgesToAddInReleases = edgesToAddInReleases;
	}

	public int[] getEdgesToRemoveInReleases() {
		return edgesToRemoveInReleases;
	}

	public void setEdgesToRemoveInReleases(int[] edgesToRemoveInReleases) {
		this.edgesToRemoveInReleases = edgesToRemoveInReleases;
	}

	@Override
	public String toString() {
		return "MicroservicesGraph [fileName=" + fileName + ", pathName=" + pathName + ", initialArchitectureState="
				+ initialArchitectureState + ", architectureEvolutionTarget=" + architectureEvolutionTarget
				+ ", architectureEvolutionIssue=" + architectureEvolutionIssue + ", architectureEvolutionGrowthRate="
				+ architectureEvolutionGrowthRate + ", verticesToAddInReleases="
				+ Arrays.toString(verticesToAddInReleases) + ", verticesToRemoveInReleases="
				+ Arrays.toString(verticesToRemoveInReleases) + ", edgesToAddInReleases="
				+ Arrays.toString(edgesToAddInReleases) + ", edgesToRemoveInReleases="
				+ Arrays.toString(edgesToRemoveInReleases) + "]";
	}
	
}
