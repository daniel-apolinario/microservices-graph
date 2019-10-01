/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class OutsideControlLimitStatisticTest implements StatisticTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.unicamp.ic.microservices.graphs.StatisticTest#runTest(br.unicamp.ic.
	 * microservices.graphs.ControlChart)
	 */
	@Override
	public StatisticTestResult runTest(ControlChart controlChart) {
		StatisticTestResult result = new StatisticTestResult();
		if (controlChart != null) {
			result.setStable(true);
			for (Object value : controlChart.getXValues()) {
				Double point = (Double) value;
				if (point.doubleValue() > controlChart.getUpperControlLimit().doubleValue()) {
					result.setStable(false);
					String message = "Point above of upper control limit. Upper control limit: "
							+ controlChart.getUpperControlLimit() + ". Point value: " + point.doubleValue();
					result.addTestError(createNewTestError(controlChart, message));
				} else {
					if (point.doubleValue() < controlChart.getLowerControlLimit().doubleValue()) {
						result.setStable(false);
						String message = "Point below of lower control limit. Lower control limit: "
								+ controlChart.getLowerControlLimit() + ". Point value: " + point.doubleValue();
						result.addTestError(createNewTestError(controlChart, message));
					}
				}

			}
		}

		return result;
	}

	private StatisticTestError createNewTestError(ControlChart controlChart, String message) {
		StatisticTestError error = new StatisticTestError();
		if (controlChart.getMetric().getOwner() instanceof Application) {
			Application app = (Application) controlChart.getMetric().getOwner();
			error.setApplicationName(app.getName());
		}
		if (controlChart.getMetric().getOwner() instanceof Microservice) {
			Microservice microservice = (Microservice) controlChart.getMetric().getOwner();
			error.setMicroserviceName(microservice.getName());
			error.setApplicationName(microservice.getApplication().getName());
		}
		error.setControlChart(controlChart);
		error.setMetric(controlChart.getMetric());
		error.setMetricType(controlChart.getMetric().getType().name());
		error.setStatisticTestType(StatisticTestType.TEST_1);
		error.setErrorDescription(message);
		return error;
	}
}
