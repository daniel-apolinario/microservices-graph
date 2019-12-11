/**
 * 
 */
package br.unicamp.ic.microservices.graphs.csvexporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.metrics.GiniSeries;
import br.unicamp.ic.microservices.metrics.Metric;
import br.unicamp.ic.microservices.metrics.Metric.MetricType;
import br.unicamp.ic.microservices.model.Microservice;
import br.unicamp.ic.microservices.model.MicroservicesApplication;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class CSVExporterGiniResults {

	public static final String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";

	public static void main(String[] args) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**metrics-with-gini.json}");

		List<Path> filesList = MicroservicesGraphUtil.findFiles(searchFolder, matcher);
		Gson gson = new GsonBuilder().create();
		for (Path path : filesList) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				MicroservicesApplication app = gson.fromJson(reader, MicroservicesApplication.class);
				List<String[]> applicationMetricsAndGiniValues = transformMetricsAndGiniValuesIntoStringList(app);
				exportMetricsToCSV(app.getName(), applicationMetricsAndGiniValues,
						app.getName() + "-metrics-gini-values.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param name
	 * @param microservicesMetrics
	 * @return
	 */
	private static String exportMetricsToCSV(String name, List<String[]> metrics, String fileName) {
		Path path = Paths.get(searchFolder, name, fileName);
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(path.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String[] array : metrics) {
			writer.writeNext(array);
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readFile(path);

	}

	/**
	 * @param app
	 * @return
	 */
	private static List<String[]> transformMetricsAndGiniValuesIntoStringList(MicroservicesApplication app) {
		List<String[]> microservicesMetrics = new ArrayList<String[]>();
		List<Microservice> microservices = app.getMicroservices();
		if (microservices != null && microservices.size() > 0) {
			int numberOfReleases = 0;
			if (microservices.get(0) != null && app.getMicroservices().get(0).getMetrics() != null
					&& app.getMicroservices().get(0).getMetrics().size() > 0
					&& app.getMicroservices().get(0).getMetrics().get(0).getReleases() != null) {
				numberOfReleases = app.getMicroservices().get(0).getMetrics().get(0).getReleases().length;
			}
			HashMap<Integer, StringBuffer> metricsHash = new HashMap<Integer, StringBuffer>();
			for (int i = 0; i <= numberOfReleases; i++) {
				if (i == 0) {
					metricsHash.put(i, new StringBuffer("release"));
				} else {
					metricsHash.put(i, new StringBuffer(String.valueOf(i - 1)));
				}
			}

			// include gini values for SIY, ADS and AIS metrics between the microservices
			// metric values
			// for all the releases
			StringBuffer headerToInclude = metricsHash.get(0);
			headerToInclude.append("#").append(MetricType.SIY);
			headerToInclude.append("#").append("GINI - ").append(MetricType.SIY);
			headerToInclude.append("#").append("APP - GINI -").append(MetricType.ADS);
			headerToInclude.append("#").append("APP - GINI - ").append(MetricType.AIS);
			List<GiniSeries<MetricType, Integer, BigDecimal>> appGiniSeriesList = app.getGiniSeries();
			Optional<Metric> siyMetric = findSpecificMetric(app.getMetrics(), MetricType.SIY);
			Object[] siyMetricValues = null;
			if (siyMetric.isPresent()) {
				siyMetricValues = siyMetric.get().getValues();
			}
			GiniSeries<MetricType, Integer, BigDecimal> appSIYGiniSeries = findSpecificGiniSeriesByMetric(
					appGiniSeriesList, MetricType.SIY);
			GiniSeries<MetricType, Integer, BigDecimal> appADSGiniSeries = findSpecificGiniSeriesByMetric(
					appGiniSeriesList, MetricType.ADS);
			GiniSeries<MetricType, Integer, BigDecimal> appAISGiniSeries = findSpecificGiniSeriesByMetric(
					appGiniSeriesList, MetricType.AIS);
			if (appADSGiniSeries != null && appAISGiniSeries != null) {
				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(siyMetricValues[i - 1].toString());
					dataRow.append("#").append(appSIYGiniSeries.getSeriesData().get(i - 1).toString());
					dataRow.append("#").append(appADSGiniSeries.getSeriesData().get(i - 1).toString());
					dataRow.append("#").append(appAISGiniSeries.getSeriesData().get(i - 1).toString());
				}
			}
			for (Microservice microservice : microservices) {
				Optional<Metric> adsMetric = findSpecificMetric(microservice.getMetrics(), MetricType.ADS);
				Object[] adsMetricValues = null;
				if (adsMetric.isPresent()) {
					adsMetricValues = adsMetric.get().getValues();
				}
				Optional<Metric> aisMetric = findSpecificMetric(microservice.getMetrics(), MetricType.AIS);
				Object[] aisMetricValues = null;
				if (aisMetric.isPresent()) {
					aisMetricValues = aisMetric.get().getValues();
				}
				// Get the gini values for this microservice
				List<GiniSeries<MetricType, Integer, BigDecimal>> giniSeriesList = microservice.getGiniSeries();
				GiniSeries<MetricType, Integer, BigDecimal> adsGiniSeries = findSpecificGiniSeriesByMetric(
						giniSeriesList, MetricType.ADS);
				HashMap adsGiniHash = null;
				if (adsGiniSeries != null && adsGiniSeries.getSeriesData() != null) {
					adsGiniHash = adsGiniSeries.getSeriesData();
				}

				GiniSeries aisGiniSeries = findSpecificGiniSeriesByMetric(giniSeriesList, MetricType.AIS);
				HashMap aisGiniHash = null;
				if (aisGiniSeries != null && aisGiniSeries.getSeriesData() != null) {
					aisGiniHash = aisGiniSeries.getSeriesData();
				}

				StringBuffer headerRow = metricsHash.get(0);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.ADS);
				headerRow.append("#").append(microservice.getName()).append("-").append("GINI -")
						.append(MetricType.ADS);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.AIS);
				headerRow.append("#").append(microservice.getName()).append("-").append("GINI -")
						.append(MetricType.AIS);

				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(adsMetricValues[i - 1].toString());
					if (adsGiniHash != null && adsGiniHash.get(i - 1) != null) {
						dataRow.append("#").append(adsGiniHash.get(i - 1).toString());
					} else {
						dataRow.append("#").append("");
					}
					dataRow.append("#").append(aisMetricValues[i - 1].toString());
					if (aisGiniHash != null && aisGiniHash.get(i - 1) != null) {
						dataRow.append("#").append(aisGiniHash.get(i - 1).toString());
					} else {
						dataRow.append("#").append("");
					}

				}
			}
			for (Map.Entry<Integer, StringBuffer> row : metricsHash.entrySet()) {
				String[] dataArray = row.getValue().toString().split("#");
				microservicesMetrics.add(dataArray);
			}

		}
		return microservicesMetrics;
	}

	private static Optional<Metric> findSpecificMetric(List<Metric> metrics, MetricType metricType) {
		return metrics.stream().filter(m -> m.getType().equals(metricType)).findFirst();
	}

	private static GiniSeries<MetricType, Integer, BigDecimal> findSpecificGiniSeriesByMetric(
			List<GiniSeries<MetricType, Integer, BigDecimal>> giniSeriesList, MetricType metricType) {
		GiniSeries<MetricType, Integer, BigDecimal> giniSeriesFound = null;
		for (GiniSeries<MetricType, Integer, BigDecimal> giniSeries : giniSeriesList) {
			if (giniSeries.getMetricType() != null) {
				MetricType giniSeriesMetricType = (MetricType) giniSeries.getMetricType();
				if (giniSeriesMetricType.equals(metricType)) {
					giniSeriesFound = giniSeries;
					break;
				}
			}
		}
		return giniSeriesFound;
	}

	/**
	 * Simple File Reader
	 */

	public static String readFile(Path path) {
		String response = "";
		try {
			FileReader fr = new FileReader(path.toString());
			BufferedReader br = new BufferedReader(fr);
			String strLine;
			StringBuffer sb = new StringBuffer();
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
			}
			response = sb.toString();
			System.out.println(response);
			fr.close();
			br.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return response;
	}

}
