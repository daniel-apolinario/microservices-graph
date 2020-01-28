/**
 * 
 */
package br.unicamp.ic.microservices.graphs.csvexporter;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class GiniGroupedResultKey {
	/**
	 * @param applicationType
	 * @param sizeApplication
	 * @param initialApplicationStatus
	 * @param applicationEvolution
	 */
	public GiniGroupedResultKey(String applicationType, String sizeApplication, String initialApplicationStatus,
			String applicationEvolution) {
		super();
		this.applicationType = applicationType;
		this.sizeApplication = sizeApplication;
		this.initialApplicationStatus = initialApplicationStatus;
		this.applicationEvolution = applicationEvolution;
	}

	private String applicationType;
	private String sizeApplication;
	private String initialApplicationStatus;
	private String applicationEvolution;

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getSizeApplication() {
		return sizeApplication;
	}

	public void setSizeApplication(String sizeApplication) {
		this.sizeApplication = sizeApplication;
	}

	public String getInitialApplicationStatus() {
		return initialApplicationStatus;
	}

	public void setInitialApplicationStatus(String initialApplicationStatus) {
		this.initialApplicationStatus = initialApplicationStatus;
	}

	public String getApplicationEvolution() {
		return applicationEvolution;
	}

	public void setApplicationEvolution(String applicationEvolution) {
		this.applicationEvolution = applicationEvolution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationEvolution == null) ? 0 : applicationEvolution.hashCode());
		result = prime * result + ((applicationType == null) ? 0 : applicationType.hashCode());
		result = prime * result + ((initialApplicationStatus == null) ? 0 : initialApplicationStatus.hashCode());
		result = prime * result + ((sizeApplication == null) ? 0 : sizeApplication.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GiniGroupedResultKey other = (GiniGroupedResultKey) obj;
		if (applicationEvolution == null) {
			if (other.applicationEvolution != null)
				return false;
		} else if (!applicationEvolution.equals(other.applicationEvolution))
			return false;
		if (applicationType == null) {
			if (other.applicationType != null)
				return false;
		} else if (!applicationType.equals(other.applicationType))
			return false;
		if (initialApplicationStatus == null) {
			if (other.initialApplicationStatus != null)
				return false;
		} else if (!initialApplicationStatus.equals(other.initialApplicationStatus))
			return false;
		if (sizeApplication == null) {
			if (other.sizeApplication != null)
				return false;
		} else if (!sizeApplication.equals(other.sizeApplication))
			return false;
		return true;
	}
}
