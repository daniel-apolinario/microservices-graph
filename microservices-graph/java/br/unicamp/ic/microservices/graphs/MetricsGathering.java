/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.unicamp.ic.microservices.graphs.Metric.MetricType;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class MetricsGathering {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**.dot}");

		// For each directory we have one application
		File directory = new File(searchFolder);
		File[] files = directory.listFiles();
		Arrays.sort(files);
		List<MicroservicesApplication> applicationList = new ArrayList<MicroservicesApplication>();
		for (File file : files) {
			if (file.isDirectory()) {
				// get all the files that store the graphs for each release of one application
				Collection<Path> graphFilesDOT = MicroservicesGraphUtil.findGraphFiles(file.getPath(), matcher);
				// transform the files to Graph objects to work with them
				List<MicroservicesGraph<String, DefaultEdge>> microservicesGraphList = MicroservicesGraphUtil
						.importMicroservicesGraphList(graphFilesDOT);
				MicroservicesApplication application = new MicroservicesApplication();
				application.setDependenciesGraphs(microservicesGraphList);
				application.setName(file.getName());
				applicationList.add(application);
			}
		}
		for (MicroservicesApplication app : applicationList) {
			calculateMetrics(app);
		}

		for (MicroservicesApplication app : applicationList) {
			try (FileOutputStream fos = new FileOutputStream(searchFolder + app.getName() + "/metrics.json");
					OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

				gson.toJson(app, isr);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param app
	 */
	private static void calculateMetrics(MicroservicesApplication app) {
		calculateApplicationMetrics(app);
		calculateMicroserviceMetrics(app);
	}

	/**
	 * @param app
	 */
	private static void calculateMicroserviceMetrics(MicroservicesApplication app) {
		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			// the last graph contains all the vertices
			MicroservicesGraph<String, DefaultEdge> lastGraph = app.getDependenciesGraphs()
					.get(app.getDependenciesGraphs().size() - 1);

			for (String vertice : lastGraph.vertexSet()) {
				Microservice microservice = new Microservice();
				microservice.setApplication(app);
				microservice.setName(vertice);
				calculateAbsoluteDependencyOfServicesMetrics(app, microservice, vertice);
				calculateAbsoluteImportanceOfServicesMetrics(app, microservice, vertice);
			}

		}

	}

	/**
	 * @param app
	 * @param vertice
	 */
	private static void calculateAbsoluteImportanceOfServicesMetrics(MicroservicesApplication app,
			Microservice microservice, String vertice) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Integer[] values = new Integer[app.getDependenciesGraphs().size()];
			Metric<Microservice, Integer, Integer> metric = new Metric<Microservice, Integer, Integer>();
			metric.setType(MetricType.AIS);
			metric.setOwner(microservice);
			metric.setReleases(releases);
			metric.setValues(values);

			for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
				MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
				String release = graph.getFileName().substring(graph.getFileName().length() - 6,
						graph.getFileName().length() - 4);
				metric.addRelease(i, Integer.parseInt(release));
				int aisMetric = 0;
				if (graph.containsVertex(vertice)) {
					aisMetric = graph.inDegreeOf(vertice);
				}
				metric.addValue(i, aisMetric);
			}
			microservice.addMetric(metric);
			app.addMicroservice(microservice);
		}

	}

	/**
	 * @param app
	 * @param vertice
	 */
	private static void calculateAbsoluteDependencyOfServicesMetrics(MicroservicesApplication app,
			Microservice microservice, String vertice) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Integer[] values = new Integer[app.getDependenciesGraphs().size()];
			Metric<Microservice, Integer, Integer> metric = new Metric<Microservice, Integer, Integer>();
			metric.setType(MetricType.ADS);
			metric.setOwner(microservice);
			metric.setReleases(releases);
			metric.setValues(values);

			for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
				MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
				String release = graph.getFileName().substring(graph.getFileName().length() - 6,
						graph.getFileName().length() - 4);
				metric.addRelease(i, Integer.parseInt(release));
				int adsMetric = 0;
				if (graph.containsVertex(vertice)) {
					adsMetric = graph.outDegreeOf(vertice);
				}
				metric.addValue(i, adsMetric);
			}
			microservice.addMetric(metric);
			app.addMicroservice(microservice);
		}
	}

	/**
	 * @param app
	 */
	private static void calculateApplicationMetrics(MicroservicesApplication app) {
		calculateServicesInterdependenceInTheSystem(app);
	}

	/**
	 * @param app
	 */
	private static void calculateServicesInterdependenceInTheSystem(MicroservicesApplication app) {

		// TEM QUE SER POR GRAFO!!!

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {

			for (MicroservicesGraph<String, DefaultEdge> graph : app.getDependenciesGraphs()) {
				

			}
			
		}
	}

}
