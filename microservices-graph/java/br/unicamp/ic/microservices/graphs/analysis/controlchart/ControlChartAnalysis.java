/**
 * 
 */
package br.unicamp.ic.microservices.graphs.analysis.controlchart;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.metrics.Metric;
import br.unicamp.ic.microservices.model.Application;
import br.unicamp.ic.microservices.model.Microservice;
import br.unicamp.ic.microservices.model.MicroservicesApplication;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class ControlChartAnalysis {

	public static final String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**metrics.json}");

		List<Path> filesList = MicroservicesGraphUtil.findFiles(searchFolder, matcher);
		Gson gson = new GsonBuilder().create();
		for (Path path : filesList) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				List<ControlChart> controlChartList = new ArrayList<ControlChart>();
				MicroservicesApplication app = gson.fromJson(reader, MicroservicesApplication.class);
				controlChartList = createControlCharts(app);
				List<StatisticTestResult> testResultList = new ArrayList<StatisticTestResult>();
				for (ControlChart controlChart : controlChartList) {
					StatisticTest outsideControlLimit = new OutsideControlLimitStatisticTest();
					testResultList.add(outsideControlLimit.runTest(controlChart));
				}

				exportTestResultsToJson(app.getName(), testResultList);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param testResultList
	 */
	private static void exportTestResultsToJson(String appName, List<StatisticTestResult> testResultList) {

		if (testResultList != null && testResultList.size() > 0) {

			try (FileOutputStream fos = new FileOutputStream(searchFolder + appName + "/statisticTests.json");
					OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

				gson.toJson(testResultList, isr);
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
	private static List<ControlChart> createControlCharts(MicroservicesApplication app) {

		List<ControlChart> controlChartList = new ArrayList<ControlChart>();
		// create control charts for application metrics
		for (Metric<Application, Integer, Double> metric : app.getMetrics()) {
			metric.setOwner(app);
			ControlChart controlChart = createIndividualAverageControlChart(metric);
			controlChartList.add(controlChart);
		}

		// create control charts for microservice metrics
		for (Microservice microservice : app.getMicroservices()) {
			for (Metric metric : microservice.getMetrics()) {
				microservice.setApplication(app);
				metric.setOwner(microservice);
				ControlChart controlChart = createIndividualAverageControlChart(metric);
				controlChartList.add(controlChart);
			}
		}

		return controlChartList;
	}

	public static ControlChart createIndividualAverageControlChart(Metric metric) {
		ControlChart controlChart = new IndividualAverageControlChart();
		controlChart.setMetric(metric);			
		Object[] metricValues = metric.getValues();
		Object[] metricReleases = metric.getReleases();
		controlChart.setXValues(metricValues);
		controlChart.setYValues(metricReleases);
		try {
			controlChart.calculateControlLimits();			
			System.out.println("centerline=" + controlChart.getCenterline());
			System.out.println("RangeCenterline=" + controlChart.getRangeCenterline());
			System.out.println("upperControlLimit=" + controlChart.getUpperControlLimit());
			System.out.println("lowerControlLimit=" + controlChart.getLowerControlLimit());
			System.out.println("RangeupperControlLimit=" + controlChart.getRangeUpperControlLimit());
			System.out.println("LowerControlLimit=" + controlChart.getRangeLowerControlLimit());
			System.out.println("OneSigma=" + controlChart.getOneSigma());
			System.out.println("");
		} catch (ValuesNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return controlChart;
	}

}
