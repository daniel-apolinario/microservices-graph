/**
 * 
 */
package br.unicamp.ic.microservices.graphs.analysis.controlchart;

import java.util.List;

import br.unicamp.ic.microservices.metrics.Metric;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public interface ControlChart<X, Y> {

	Double getCenterline();

	void setCenterline(double centerline);
	
	Double getRangeCenterline();

	void setRangeCenterline(double rangeCenterline);	

	Double getUpperControlLimit();

	void setUpperControlLimit(double upperControlLimit);

	Double getRangeUpperControlLimit();

	void setRangeUpperControlLimit(double rangeUpperControlLimit);

	Double getLowerControlLimit();

	void setLowerControlLimit(double lowerControlLimit);

	Double getRangeLowerControlLimit();

	void setRangeLowerControlLimit(double rangeLowerControlLimit);

	Double getOneSigma();

	void setOneSigma(double oneSigma);

	X[] getXValues();

	void setXValues(X[] xvalues);

	Y[] getYValues();

	void setYValues(Y[] yvalues);
	
	void calculateControlLimits() throws ValuesNotFoundException;
	
	List<StatisticTestResult> applyTests();
	
	Metric getMetric();
	
	void setMetric(Metric metric);
}
