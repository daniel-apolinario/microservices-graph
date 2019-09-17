/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import com.google.gson.annotations.Expose;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class Metric<O, R, V> {

	@Expose
	private MetricType type;
	
	private O owner;
	@Expose
	private R[] releases;
	@Expose
	private V[] values;
	
	
	public enum MetricType {
		SIY, ADS, AIS, ACS, RCS, RIS, SCF, ADCS;
	}

	/**
	 * @return the releases
	 */
	public R[] getReleases() {
		return releases;
	}

	/**
	 * @param releases the releases to set
	 */
	public void setReleases(R[] releases) {
		this.releases = releases;
	}

	/**
	 * @return the values
	 */
	public V[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(V[] values) {
		this.values = values;
	}

	/**
	 * @return the owner
	 */
	public O getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(O owner) {
		this.owner = owner;
	}

	/**
	 * @return the type
	 */
	public MetricType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MetricType type) {
		this.type = type;
	}

	public void addRelease(int releaseIndex, R release) {
		this.releases[releaseIndex] = release;
	}

	public void addValue(int valueIndex, V value) {
		this.values[valueIndex] = value;
	}

}
