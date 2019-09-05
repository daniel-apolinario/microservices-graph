/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.util.Random;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UniformRandomGenerator;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class Teste {

	public static void main(String args[]) {

		int nodes = 20;
		int releases = 10;

		int[] nodesToAdd = calculateVectorDistribution((int) (nodes * 1.3), 10);
		int[] nodesToRemove = calculateVectorDistribution((int) (nodes * 0.3), 10);

		for (int i = 0; i < releases; i++) {
			System.out.println("nodesToAdd[" + i + "]=" + nodesToAdd[i] + " e nodesToRemove[" + i + "]=" + nodesToRemove[i]);
		}
	}

	public static int[] calculateVectorDistribution(int nodes, int releases) {
		int[] result = new int[releases];
		Random rd = new Random();
		int allNodes = nodes;
		System.out.println("allNodes=" + allNodes);
		int nodesToSort = (int) ((allNodes * 0.2) + 1);
//		System.out.println("nodesToSort=" + nodesToSort);
		double txCrescimento = (double) (allNodes - nodesToSort) / releases;
//		System.out.println("txCrescimento=" + txCrescimento);
		double nodesAdded = 0;
		int sum = 0;
		for (int i = 0; i < releases; i++) {
			int nodesIteraction = rd.nextInt(nodesToSort + 1);
			sum = sum + nodesIteraction;
			result[i] = nodesIteraction;
//			System.out.println("nodesIteraction=" + nodesIteraction);
			nodesToSort = nodesToSort - nodesIteraction;
			nodesAdded = nodesAdded + txCrescimento;
			if (nodesAdded >= 1) {
				int adding = (int) (nodesAdded - (nodesAdded % 1));
				nodesToSort = nodesToSort + adding;
				nodesAdded = nodesAdded - adding;
			}
//			System.out.println("nodesAdded=" + nodesAdded);
//			System.out.println("nodesToSort=" + nodesToSort);
		}
		if (allNodes - sum > 0) {
			result[releases - 1] = result[releases - 1] + allNodes - sum;
//			System.out.println("resto=" + (allNodes - sum));
		}
		return result;
	}
}
