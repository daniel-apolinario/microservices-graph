/**
 * 
 */
package br.unicamp.ic.microservices.graphs.analysis.controlchart;

import br.unicamp.ic.microservices.graphs.Metric;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public abstract class ControlChartBase<X, Y> implements ControlChart<X, Y> {

	private Double centerline;

	private Double rangeCenterline;

	private Double upperControlLimit;

	private Double rangeUpperControlLimit;

	private Double lowerControlLimit;

	private Double rangeLowerControlLimit;

	private Double oneSigma;

	private X[] xValues;

	private Y[] yValues;

	private Metric metric;

	@Override
	public X[] getXValues() {
		return this.xValues;
	}

	@Override
	public void setXValues(X[] xValues) {
		this.xValues = xValues;
	}

	public Y[] getYValues() {
		return this.yValues;
	}

	public void setYValues(Y[] yValues) {
		this.yValues = yValues;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	@Override
	public Double getCenterline() {
		return centerline;
	}

	@Override
	public void setCenterline(double centerline) {
		this.centerline = centerline;
	}

	@Override
	public Double getUpperControlLimit() {
		return upperControlLimit;
	}

	@Override
	public void setUpperControlLimit(double upperControlLimit) {
		this.upperControlLimit = upperControlLimit;
	}

	@Override
	public Double getLowerControlLimit() {
		return lowerControlLimit;
	}

	@Override
	public void setLowerControlLimit(double lowerControlLimit) {
		this.lowerControlLimit = lowerControlLimit;
	}

	@Override
	public Double getRangeCenterline() {
		return rangeCenterline;
	}

	@Override
	public void setRangeCenterline(double rangeCenterline) {
		this.rangeCenterline = rangeCenterline;
	}

	@Override
	public Double getRangeUpperControlLimit() {
		return rangeUpperControlLimit;
	}

	@Override
	public void setRangeUpperControlLimit(double rangeUpperControlLimit) {
		this.rangeUpperControlLimit = rangeUpperControlLimit;
	}

	@Override
	public Double getRangeLowerControlLimit() {
		return rangeLowerControlLimit;
	}

	@Override
	public void setRangeLowerControlLimit(double rangeLowerControlLimit) {
		this.rangeLowerControlLimit = rangeLowerControlLimit;
	}

	@Override
	public Double getOneSigma() {
		return oneSigma;
	}

	@Override
	public void setOneSigma(double oneSigma) {
		this.oneSigma = oneSigma;
	}

}
