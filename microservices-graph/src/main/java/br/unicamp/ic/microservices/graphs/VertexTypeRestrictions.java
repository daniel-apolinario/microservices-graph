/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class VertexTypeRestrictions {

	private boolean apiGatewayRestrict = false;

	private boolean serviceRegistryRestrict = false;

	private boolean apiCompositionRestrict = false;

	private boolean eventDrivenRestrict = false;

	private boolean externalizedConfigurationRestrict = false;

	private boolean distributedTracingRestrict = false;

	/**
	 * @param apiGatewayRestrict
	 * @param serviceRegistryRestrict
	 * @param apiCompositionRestrict
	 * @param eventDrivenRestrict
	 * @param externalizedConfigurationRestrict
	 * @param distributedTracingRestrict
	 */
	public VertexTypeRestrictions(boolean apiGatewayRestrict, boolean serviceRegistryRestrict,
			boolean apiCompositionRestrict, boolean eventDrivenRestrict, boolean externalizedConfigurationRestrict,
			boolean distributedTracingRestrict) {
		super();
		this.apiGatewayRestrict = apiGatewayRestrict;
		this.serviceRegistryRestrict = serviceRegistryRestrict;
		this.apiCompositionRestrict = apiCompositionRestrict;
		this.eventDrivenRestrict = eventDrivenRestrict;
		this.externalizedConfigurationRestrict = externalizedConfigurationRestrict;
		this.distributedTracingRestrict = distributedTracingRestrict;
	}

	public VertexTypeRestrictions() {
		this.apiGatewayRestrict = false;
		this.serviceRegistryRestrict = false;
		this.apiCompositionRestrict = false;
		this.eventDrivenRestrict = false;
		this.externalizedConfigurationRestrict = false;
		this.distributedTracingRestrict = false;
	}
	
	public boolean isApiGatewayRestrict() {
		return apiGatewayRestrict;
	}

	public void setApiGatewayRestrict(boolean apiGatewayRestrict) {
		this.apiGatewayRestrict = apiGatewayRestrict;
	}

	public boolean isServiceRegistryRestrict() {
		return serviceRegistryRestrict;
	}

	public void setServiceRegistryRestrict(boolean serviceRegistryRestrict) {
		this.serviceRegistryRestrict = serviceRegistryRestrict;
	}

	public boolean isApiCompositionRestrict() {
		return apiCompositionRestrict;
	}

	public void setApiCompositionRestrict(boolean apiCompositionRestrict) {
		this.apiCompositionRestrict = apiCompositionRestrict;
	}

	public boolean isEventDrivenRestrict() {
		return eventDrivenRestrict;
	}

	public void setEventDrivenRestrict(boolean eventDrivenRestrict) {
		this.eventDrivenRestrict = eventDrivenRestrict;
	}

	public boolean isExternalizedConfigurationRestrict() {
		return externalizedConfigurationRestrict;
	}

	public void setExternalizedConfigurationRestrict(boolean externalizedConfigurationRestrict) {
		this.externalizedConfigurationRestrict = externalizedConfigurationRestrict;
	}

	public boolean isDistributedTracingRestrict() {
		return distributedTracingRestrict;
	}

	public void setDistributedTracingRestrict(boolean distributedTracingRestrict) {
		this.distributedTracingRestrict = distributedTracingRestrict;
	}

	public boolean testVertexTypeRestrictions(String vertexName) {

		if (vertexName != null) {
			if (this.apiGatewayRestrict && vertexName.equals(VertexType.API_GATEWAY)) {
				return false;
			}
			if (this.serviceRegistryRestrict && vertexName.equals(VertexType.SERVICE_REGISTRY)) {
				return false;
			}
			if (this.apiCompositionRestrict && vertexName.equals(VertexType.API_COMPOSITION)) {
				return false;
			}
			if (this.eventDrivenRestrict && vertexName.equals(VertexType.EVENT_DRIVEN)) {
				return false;
			}
			if (this.externalizedConfigurationRestrict && vertexName.equals(VertexType.EXTERNALIZED_CONFIGURATION)) {
				return false;
			}
			if (this.distributedTracingRestrict && vertexName.equals(VertexType.DISTRIBUTED_TRACING)) {
				return false;
			}

		}
		return true;
	}

}
