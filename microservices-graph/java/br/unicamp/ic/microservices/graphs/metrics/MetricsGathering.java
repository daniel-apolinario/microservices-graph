/**
 * 
 */
package br.unicamp.ic.microservices.graphs.metrics;

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
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.HawickJamesSimpleCycles;
import org.jgrapht.graph.DefaultEdge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.unicamp.ic.microservices.graphs.MicroservicesGraph;
import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.metrics.Metric;
import br.unicamp.ic.microservices.metrics.Metric.MetricType;
import br.unicamp.ic.microservices.model.Application;
import br.unicamp.ic.microservices.model.Microservice;
import br.unicamp.ic.microservices.model.MicroservicesApplication;

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
				Collection<Path> graphFilesDOT = MicroservicesGraphUtil.findFiles(file.getPath(), matcher);
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
		calculateMicroserviceMetrics(app);
		calculateApplicationMetrics(app);
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
				app.addMicroservice(microservice);
				calculateAbsoluteDependencyOfServicesMetrics(app, microservice, vertice);
				calculateAbsoluteImportanceOfServicesMetrics(app, microservice, vertice);
				calculateAbsoluteCriticalityOfServicesMetrics(app, microservice, vertice);
				calculateRelativeCouplingOfServicesMetrics(app, microservice, vertice);
				calculateRelativeImportanceOfServicesMetrics(app, microservice, vertice);
			}

		}

	}

	/**
	 * @param app
	 * @param microservice
	 * @param vertice
	 */
	private static void calculateAbsoluteCriticalityOfServicesMetrics(MicroservicesApplication app,
			Microservice microservice, String vertice) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0
				&& app.getMicroservices() != null && app.getMicroservices().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Integer[] values = new Integer[app.getDependenciesGraphs().size()];
			Metric<Microservice, Integer, Integer> metric = new Metric<Microservice, Integer, Integer>();
			metric.setType(MetricType.ACS);
			metric.setOwner(microservice);
			metric.setReleases(releases);
			metric.setValues(values);

			Optional<Metric> adsMetric = microservice.getMetrics().stream()
					.filter(m -> m.getType().equals(MetricType.ADS)).findFirst();
			Integer[] adsMetricValues = null;
			if (adsMetric.isPresent()) {
				adsMetricValues = (Integer[]) adsMetric.get().getValues();
			}

			Optional<Metric> aisMetric = microservice.getMetrics().stream()
					.filter(m -> m.getType().equals(MetricType.AIS)).findFirst();
			Integer[] aisMetricValues = null;
			if (aisMetric.isPresent()) {
				aisMetricValues = (Integer[]) aisMetric.get().getValues();
			}
			if (adsMetricValues != null && aisMetricValues != null) {
				for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
					MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
					String releaseNumber = graph.getFileName().substring(graph.getFileName().length() - 6,
							graph.getFileName().length() - 4);
					int release = Integer.parseInt(releaseNumber);
					metric.addRelease(i, release);
					int acsMetric = adsMetricValues[release] * aisMetricValues[release];
					metric.addValue(i, acsMetric);
				}
				microservice.addMetric(metric);
			}
		}

	}

	/**
	 * @param app
	 * @param microservice
	 * @param vertice
	 */
	private static void calculateRelativeImportanceOfServicesMetrics(MicroservicesApplication app,
			Microservice microservice, String vertice) {
		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Double[] values = new Double[app.getDependenciesGraphs().size()];
			Metric<Microservice, Integer, Double> metric = new Metric<Microservice, Integer, Double>();
			metric.setType(MetricType.RIS);
			metric.setOwner(microservice);
			metric.setReleases(releases);
			metric.setValues(values);
			Optional<Metric> aisMetric = microservice.getMetrics().stream()
					.filter(m -> m.getType().equals(MetricType.AIS)).findFirst();
			Integer[] aisMetricValues = null;
			if (aisMetric.isPresent()) {
				aisMetricValues = (Integer[]) aisMetric.get().getValues();
			}
			if (aisMetricValues != null && aisMetricValues.length > 0) {
				for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
					MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
					String release = graph.getFileName().substring(graph.getFileName().length() - 6,
							graph.getFileName().length() - 4);
					metric.addRelease(i, Integer.parseInt(release));
					int totalVertices = graph.vertexSet().size();
					double risMetric = 0;
					if (graph.containsVertex(vertice)) {
						risMetric = (double) aisMetricValues[Integer.parseInt(release)] / totalVertices;
					}
					metric.addValue(i, risMetric);
				}

				microservice.addMetric(metric);
			}
		}

	}

	/**
	 * @param app
	 * @param microservice
	 * @param vertice
	 */
	private static void calculateRelativeCouplingOfServicesMetrics(MicroservicesApplication app,
			Microservice microservice, String vertice) {
		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Double[] values = new Double[app.getDependenciesGraphs().size()];
			Metric<Microservice, Integer, Double> metric = new Metric<Microservice, Integer, Double>();
			metric.setType(MetricType.RCS);
			metric.setOwner(microservice);
			metric.setReleases(releases);
			metric.setValues(values);
			Optional<Metric> adsMetric = microservice.getMetrics().stream()
					.filter(m -> m.getType().equals(MetricType.ADS)).findFirst();
			Integer[] adsMetricValues = null;
			if (adsMetric.isPresent()) {
				adsMetricValues = (Integer[]) adsMetric.get().getValues();
			}
			if (adsMetricValues != null && adsMetricValues.length > 0) {
				for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
					MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
					String release = graph.getFileName().substring(graph.getFileName().length() - 6,
							graph.getFileName().length() - 4);
					metric.addRelease(i, Integer.parseInt(release));
					int totalVertices = graph.vertexSet().size();
					double rcsMetric = 0;
					if (graph.containsVertex(vertice)) {
						rcsMetric = (double) adsMetricValues[Integer.parseInt(release)] / totalVertices;
					}
					metric.addValue(i, rcsMetric);
				}

				microservice.addMetric(metric);
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
		}
	}

	/**
	 * @param app
	 */
	private static void calculateApplicationMetrics(MicroservicesApplication app) {
		//calculateServicesInterdependenceInTheSystem(app);
		calculateServiceCouplingFactor(app);
		calculateAverageNumberOfDirectlyConnectedServices(app);
	}

	/**
	 * @param app
	 */
	private static void calculateAverageNumberOfDirectlyConnectedServices(MicroservicesApplication app) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0
				&& app.getMicroservices() != null && app.getMicroservices().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Double[] values = new Double[app.getDependenciesGraphs().size()];
			Metric<Application, Integer, Double> metric = new Metric<Application, Integer, Double>();
			metric.setType(MetricType.ADCS);
			metric.setOwner(app);
			metric.setReleases(releases);
			metric.setValues(values);
			for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
				MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
				String releaseId = graph.getFileName().substring(graph.getFileName().length() - 6,
						graph.getFileName().length() - 4);
				int release = Integer.parseInt(releaseId);
				metric.addRelease(i, release);

				int totalADSRelease = 0;
				// get the ADS Metric to sum all values for this currente release
				for (Microservice microservice : app.getMicroservices()) {
					Optional<Metric> adsMetric = microservice.getMetrics().stream()
							.filter(m -> m.getType().equals(MetricType.ADS)).findFirst();
					Integer[] adsMetricValues = null;
					if (adsMetric.isPresent()) {
						adsMetricValues = (Integer[]) adsMetric.get().getValues();
					}
					totalADSRelease += adsMetricValues[release];
				}

				int totalVertices = graph.vertexSet().size();

				double adcsMetric = (double) totalADSRelease / totalVertices;
				metric.addValue(i, adcsMetric);
			}
			app.addMetric(metric);
		}
	}

	/**
	 * @param app
	 */
	private static void calculateServiceCouplingFactor(MicroservicesApplication app) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0
				&& app.getMicroservices() != null && app.getMicroservices().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Double[] values = new Double[app.getDependenciesGraphs().size()];
			Metric<Application, Integer, Double> metric = new Metric<Application, Integer, Double>();
			metric.setType(MetricType.SCF);
			metric.setOwner(app);
			metric.setReleases(releases);
			metric.setValues(values);
			for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
				MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
				String releaseId = graph.getFileName().substring(graph.getFileName().length() - 6,
						graph.getFileName().length() - 4);
				int release = Integer.parseInt(releaseId);
				metric.addRelease(i, release);

				int totalADSRelease = 0;
				// get the ADS Metric to sum all values for this currente release
				for (Microservice microservice : app.getMicroservices()) {
					Optional<Metric> adsMetric = microservice.getMetrics().stream()
							.filter(m -> m.getType().equals(MetricType.ADS)).findFirst();
					Integer[] adsMetricValues = null;
					if (adsMetric.isPresent()) {
						adsMetricValues = (Integer[]) adsMetric.get().getValues();
					}
					totalADSRelease += adsMetricValues[release];
				}

				int totalVertices = graph.vertexSet().size();

				double scfMetric = (double) totalADSRelease / ((totalVertices * totalVertices) - totalVertices);
				metric.addValue(i, scfMetric);
			}
			app.addMetric(metric);
		}
	}

	/**
	 * @param app
	 */
	private static void calculateServicesInterdependenceInTheSystem(MicroservicesApplication app) {

		if (app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			Integer[] releases = new Integer[app.getDependenciesGraphs().size()];
			Integer[] values = new Integer[app.getDependenciesGraphs().size()];
			Metric<Application, Integer, Integer> metric = new Metric<Application, Integer, Integer>();
			metric.setType(MetricType.SIY);
			metric.setOwner(app);
			metric.setReleases(releases);
			metric.setValues(values);
			for (int i = 0; i < app.getDependenciesGraphs().size(); i++) {
				MicroservicesGraph<String, DefaultEdge> graph = app.getDependenciesGraphs().get(i);
				String release = graph.getFileName().substring(graph.getFileName().length() - 6,
						graph.getFileName().length() - 4);
				metric.addRelease(i, Integer.parseInt(release));

//				CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
//				Set<String> cycleVertices = cycleDetector.findCycles();

				int siyMetric = calculateServicePairsWithinSimpleCycles(graph);
				metric.addValue(i, siyMetric);
			}
			app.addMetric(metric);
		}
	}

	private static int calculateServicePairsWithinSimpleCycles(Graph<String, DefaultEdge> graph) {
		int servicePairs = 0;
		HawickJamesSimpleCycles<String, DefaultEdge> sc = new HawickJamesSimpleCycles<>(graph);
		List<List<String>> simpleCycles = sc.findSimpleCycles();

		for (int i = 0; i < simpleCycles.size(); i++) {
			List<String> cycle = simpleCycles.get(i);
			if (cycle.size() == 2) {
				servicePairs += 1;
			}
		}

		return servicePairs;
	}

}
