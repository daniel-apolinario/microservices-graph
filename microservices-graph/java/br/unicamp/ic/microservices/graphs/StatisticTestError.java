/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import com.google.gson.annotations.Expose;

import br.unicamp.ic.microservices.graphs.Metric.MetricType;
import br.unicamp.ic.microservices.graphs.StatisticTest.StatisticTestType;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class StatisticTestError {

	@Expose
	private String applicationName;

	@Expose
	private String microserviceName;

	private Metric metric;

	@Expose
	private String metricType;

	@Expose
	private ControlChart controlChart;

	@Expose
	private StatisticTestType statisticTestType;

	@Expose
	private String errorDescription;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getMicroserviceName() {
		return microserviceName;
	}

	public void setMicroserviceName(String microserviceName) {
		this.microserviceName = microserviceName;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public ControlChart getControlChart() {
		return controlChart;
	}

	public void setControlChart(ControlChart controlChart) {
		this.controlChart = controlChart;
	}

	public StatisticTestType getStatisticTestType() {
		return statisticTestType;
	}

	public void setStatisticTestType(StatisticTestType statisticTestType) {
		this.statisticTestType = statisticTestType;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getMetricType() {
		return this.metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}
}
