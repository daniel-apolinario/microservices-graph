/**
 * 
 */
package br.unicamp.ic.microservices.graphs.generation;

import com.google.gson.annotations.Expose;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class ExperimentTreatmentList {

	@Expose
	private ExperimentTreatment[] treatmentListArray;

	/**
	 * @param treatmentListArray
	 */
	public ExperimentTreatmentList(ExperimentTreatment[] treatmentListArray) {
		super();
		this.treatmentListArray = treatmentListArray;
	}

	public ExperimentTreatment[] getTreatmentListArray() {
		return treatmentListArray;
	}

	public void setTreatmentListArray(ExperimentTreatment[] treatmentListArray) {
		this.treatmentListArray = treatmentListArray;
	}


}
