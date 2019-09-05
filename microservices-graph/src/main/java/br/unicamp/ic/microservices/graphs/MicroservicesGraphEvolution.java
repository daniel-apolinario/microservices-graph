/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionIssue;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.ArchitectureEvolutionTarget;
import br.unicamp.ic.microservices.graphs.MicroservicesGraph.InitialArchitectureState;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MicroservicesGraphEvolution {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String searchFolder = "/home/daniel/Downloads/";
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**grafo*[0-9].dot}");

		// get all the files that store the graph initial structure of the microservices
		// apps
		Collection<Path> graphFilesDOT = findGraphFiles(searchFolder, matcher);
		// transform the files to Graph objects to work with them
		List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = importMicroservicesGraphList(
				graphFilesDOT);

		GraphEvolutionParameters evolutionParameters = initializeEvolutionParameters();

		microservicesGraphList = configureEvolutionParameters(microservicesGraphList, evolutionParameters);
		
		

	}

	/**
	 * @param microservicesGraphList
	 * @param evolutionParameters
	 * @return
	 */
	private static List<MicroservicesGraph<String, DefaultEdge>> configureEvolutionParameters(
			List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList,
			GraphEvolutionParameters evolutionParameters) {
		List<MicroservicesGraph<String, DefaultEdge>> configuredGraphList = microservicesGraphList;

		if (microservicesGraphList != null && microservicesGraphList.size() > 0 && evolutionParameters != null) {
			int graphsQuantity = microservicesGraphList.size();
			int graphsNumberOfArchitectureGood = (int) Math
					.ceil(evolutionParameters.getInitialArchitectureGoodPercentage() * graphsQuantity);
			int graphsNumberOfArchitectureBad = graphsQuantity - graphsNumberOfArchitectureGood;
			System.out.println("graphsNumberOfArchitectureGood=" + graphsNumberOfArchitectureGood
					+ "-graphsNumberOfArchitectureBad=" + graphsNumberOfArchitectureBad);
			int graphsNumberOfTargetWorse = graphsNumberOfArchitectureGood / 2;
			int graphsNumberOfTargetBetter = graphsNumberOfArchitectureBad / 2;
			for (MicroservicesGraph<String, DefaultEdge> microservicesGraph : configuredGraphList) {
				// meanwhile, we will set just one architecture issue = MEGA_SERVICE
				microservicesGraph.setArchitectureEvolutionIssue(ArchitectureEvolutionIssue.MEGA_SERVICE);
				if (graphsNumberOfArchitectureGood > 0) {
					microservicesGraph.setInitialArchitectureState(InitialArchitectureState.GOOD);
					graphsNumberOfArchitectureGood--;
					if (graphsNumberOfTargetWorse > 0) {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.WORSE);
						graphsNumberOfTargetWorse--;
					} else {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.KEEP);
					}
				} else {
					microservicesGraph.setInitialArchitectureState(InitialArchitectureState.BAD);
					graphsNumberOfArchitectureBad--;
					if (graphsNumberOfTargetBetter > 0) {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.BETTER);
						graphsNumberOfTargetBetter--;
					} else {
						microservicesGraph.setArchitectureEvolutionTarget(ArchitectureEvolutionTarget.KEEP);
					}
				}
			}
		}

		return configuredGraphList;
	}

	/**
	 * @return
	 */
	private static GraphEvolutionParameters initializeEvolutionParameters() {
		GraphEvolutionParameters evolutionParameters = new GraphEvolutionParameters();
		evolutionParameters.setNumberOfReleases(20);
		evolutionParameters.setInitialArchitectureGoodPercentage(50);
		evolutionParameters.setArchitectureEvolutionTargetBetterPercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionTargetWorsePercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionTargetKeepPercentage(100 / 3);
		evolutionParameters.setArchitectureEvolutionGrowthRateMininum(30);
		evolutionParameters.setArchitectureEvolutionGrowthRateMininum(60);
		return evolutionParameters;
	}

	/**
	 * @param graphFilesDOT
	 * @return
	 */
	private static List<MicroservicesGraph<String, DefaultEdge>> importMicroservicesGraphList(
			Collection<Path> graphFilesDOT) {
		List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = new ArrayList<MicroservicesGraph<String, DefaultEdge>>();
		if (!graphFilesDOT.isEmpty()) {
			VertexProvider<String> vp = (a, b) -> a;
			EdgeProvider<String, DefaultEdge> ep = (f, t, l, a) -> new DefaultEdge();
			GraphImporter<String, DefaultEdge> importer = new DOTImporter<String, DefaultEdge>(vp, ep);

			for (Path path : graphFilesDOT) {
				BufferedReader reader;
				try {
					reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
					Graph<String, DefaultEdge> graph = new MicroservicesGraph<String, DefaultEdge>(DefaultEdge.class);
					importer.importGraph(graph, reader);
					MicroservicesGraph<String, DefaultEdge> mg = (MicroservicesGraph<String, DefaultEdge>) graph;
					microservicesGraphList.add(mg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ImportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return microservicesGraphList;
	}

	/**
	 * @param searchFolder
	 * @param matcher
	 * @return
	 */
	private static Collection<Path> findGraphFiles(String searchFolder, PathMatcher matcher) {
		Collection<Path> files = null;
		try {
			files = find("/home/daniel/Downloads/", matcher);
			files.forEach(n -> System.out.println(n));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return files;
	}

	protected static Collection<Path> find(String searchDirectory, PathMatcher matcher) throws IOException {
		try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
			return files.filter(matcher::matches).collect(Collectors.toList());

		}
	}

}
