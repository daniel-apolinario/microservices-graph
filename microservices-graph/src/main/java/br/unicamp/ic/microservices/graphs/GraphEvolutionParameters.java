/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class GraphEvolutionParameters {

	private int numberOfReleases;

	private double initialArchitectureGoodPercentage;

	private double architectureEvolutionTargetKeepPercentage;

	private double architectureEvolutionTargetBetterPercentage;

	private double architectureEvolutionTargetWorsePercentage;

	private double architectureEvolutionGrowthRateMininum;

	private double architectureEvolutionGrowthRateMaximum;	

	public int getNumberOfReleases() {
		return numberOfReleases;
	}

	public void setNumberOfReleases(int numberOfReleases) {
		this.numberOfReleases = numberOfReleases;
	}

	public double getInitialArchitectureGoodPercentage() {
		return initialArchitectureGoodPercentage;
	}

	public void setInitialArchitectureGoodPercentage(double initialArchitectureGoodPercentage) {
		this.initialArchitectureGoodPercentage = initialArchitectureGoodPercentage;
	}

	public double getArchitectureEvolutionTargetKeepPercentage() {
		return architectureEvolutionTargetKeepPercentage;
	}

	public void setArchitectureEvolutionTargetKeepPercentage(double architectureEvolutionTargetKeepPercentage) {
		this.architectureEvolutionTargetKeepPercentage = architectureEvolutionTargetKeepPercentage;
	}

	public double getArchitectureEvolutionTargetBetterPercentage() {
		return architectureEvolutionTargetBetterPercentage;
	}

	public void setArchitectureEvolutionTargetBetterPercentage(double architectureEvolutionTargetBetterPercentage) {
		this.architectureEvolutionTargetBetterPercentage = architectureEvolutionTargetBetterPercentage;
	}

	public double getArchitectureEvolutionTargetWorsePercentage() {
		return architectureEvolutionTargetWorsePercentage;
	}

	public void setArchitectureEvolutionTargetWorsePercentage(double architectureEvolutionTargetWorsePercentage) {
		this.architectureEvolutionTargetWorsePercentage = architectureEvolutionTargetWorsePercentage;
	}

	public double getArchitectureEvolutionGrowthRateMininum() {
		return architectureEvolutionGrowthRateMininum;
	}

	public void setArchitectureEvolutionGrowthRateMininum(double architectureEvolutionGrowthRateMininum) {
		this.architectureEvolutionGrowthRateMininum = architectureEvolutionGrowthRateMininum;
	}

	public double getArchitectureEvolutionGrowthRateMaximum() {
		return architectureEvolutionGrowthRateMaximum;
	}

	public void setArchitectureEvolutionGrowthRateMaximum(double architectureEvolutionGrowthRateMaximum) {
		this.architectureEvolutionGrowthRateMaximum = architectureEvolutionGrowthRateMaximum;
	}

}
