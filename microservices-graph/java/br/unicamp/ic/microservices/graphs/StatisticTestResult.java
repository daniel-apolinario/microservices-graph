/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class StatisticTestResult {

	@Expose
	private boolean stable;

	@Expose
	private List<StatisticTestError> testingErrors;

	public boolean isStable() {
		return stable;
	}

	public void setStable(boolean stable) {
		this.stable = stable;
	}

	public List<StatisticTestError> getTestingErrors() {
		return testingErrors;
	}

	public void setTestingErrors(List<StatisticTestError> testingErrors) {
		this.testingErrors = testingErrors;
	}
	
	public void addTestError(StatisticTestError statisticTestError) {
		if(testingErrors == null) {
			testingErrors = new ArrayList<StatisticTestError>();
		}
		testingErrors.add(statisticTestError);
	}
}
