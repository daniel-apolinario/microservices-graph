/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public abstract class Metric<R, V> {

	private R[] releases;
	private V[] values;
	
	abstract void calculate();

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
	
	
}
