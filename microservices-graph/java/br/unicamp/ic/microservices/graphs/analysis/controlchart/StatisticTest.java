/**
 * 
 */
package br.unicamp.ic.microservices.graphs.analysis.controlchart;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public interface StatisticTest {
	
	public enum StatisticTestType{
		TEST_1, // A single point falls outside the 3-sigma control limits.
		TEST_2, // At least two out of three sucessive values fall on the same side of,
			 	// and more than two sigma units away from, the centerline.
		TEST_3, // At least four out of five sucessive values fall on the same side of,
	 			// and more than one sigma units away from, the centerline.
		TEST_4, // At least eight sucessive values fall on the same side of the centerline.
	}

	StatisticTestResult runTest(ControlChart controlChart);
}
