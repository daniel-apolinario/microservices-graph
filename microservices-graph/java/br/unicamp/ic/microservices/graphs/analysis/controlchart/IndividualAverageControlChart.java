/**
 * 
 */
package br.unicamp.ic.microservices.graphs.analysis.controlchart;

import java.util.List;

/**
 * @author Daniel R. F. Apolinario
 * @param <X, Y>
 *
 */
public class IndividualAverageControlChart<X, Y> extends ControlChartBase<X, Y> implements ControlChart<X, Y> {

	private Double[] movingRangeValues;

	// This constants are based on the table of disperion and bias factos to range
	// data
	// Book: Measuring the software process
	private static final double CONSTANT_CONTROL_LIMIT_1 = 2.660;
	private static final double CONSTANT_CONTROL_LIMIT_2 = 3.268;
	private static final double CONSTANT_CONTROL_LIMIT_3 = 1.128;

	/**
	 * @return the movingRangeValues
	 */
	public Double[] getMovingRangeValues() {
		return movingRangeValues;
	}

	/**
	 * @param movingRangeValues the movingRangeValues to set
	 */
	public void setMovingRangeValues(Double[] movingRangeValues) {
		this.movingRangeValues = movingRangeValues;
	}

	@Override
	public void calculateControlLimits() throws ValuesNotFoundException {
		if (this.getXValues() != null && this.getXValues().length > 0 && this.getYValues() != null
				&& this.getYValues().length > 0) {
			calculateCenterline();
			calculateRangeCenterline();
			calculateUpperControlLimit();
			calculateLowerControlLimit();
			calculateRangeUpperControlLimit();
			calculateRangeLowerControlLimit();
			calculateOneSigma();
		} else {
			throw new ValuesNotFoundException("X and Y values must be provided for calculate the control limits");
		}
	}

	/**
	 * 
	 */
	private void calculateOneSigma() {
		this.setOneSigma(getRangeCenterline() / CONSTANT_CONTROL_LIMIT_3);

	}

	/**
	 * 
	 */
	private void calculateRangeLowerControlLimit() {
		this.setRangeLowerControlLimit(CONSTANT_CONTROL_LIMIT_2 * getRangeCenterline() * (-1));
	}

	/**
	 * 
	 */
	private void calculateRangeUpperControlLimit() {
		this.setRangeUpperControlLimit(CONSTANT_CONTROL_LIMIT_2 * getRangeCenterline());
	}

	/**
	 * 
	 */
	private void calculateRangeCenterline() {
		int xValuesSize = this.getXValues().length;
		Double[] mRValues = new Double[xValuesSize - 1];
		for (int i = 0; i < xValuesSize - 1; i++) {
			mRValues[i] = Math.abs((double) (this.getXValues()[i + 1]) - (double) (this.getXValues()[i]));
		}
		this.movingRangeValues = mRValues;

		int mRSize = this.movingRangeValues.length;
		double rangeCenterlineValue = 0;
		double rValuesSum = 0;
		for (int i = 0; i < mRSize; i++) {
			rValuesSum += (double) this.movingRangeValues[i];
		}
		rangeCenterlineValue = rValuesSum / mRSize;
		this.setRangeCenterline(rangeCenterlineValue);
	}

	/**
	 * 
	 */
	private void calculateLowerControlLimit() {
		this.setLowerControlLimit(getCenterline() - (CONSTANT_CONTROL_LIMIT_1 * getRangeCenterline()));
	}

	/**
	 * 
	 */
	private void calculateUpperControlLimit() {
		this.setUpperControlLimit(getCenterline() + (CONSTANT_CONTROL_LIMIT_1 * getRangeCenterline()));
	}

	/**
	 * 
	 */
	private void calculateCenterline() {
		int xValuesSize = this.getXValues().length;
		double centerlineValue = 0;
		double xValuesSum = 0;
		for (int i = 0; i < xValuesSize; i++) {
			xValuesSum += (double) this.getXValues()[i];
		}
		centerlineValue = xValuesSum / xValuesSize;
		this.setCenterline(centerlineValue);
	}

	@Override
	public List<StatisticTestResult> applyTests() {
		return null;

	}

}
