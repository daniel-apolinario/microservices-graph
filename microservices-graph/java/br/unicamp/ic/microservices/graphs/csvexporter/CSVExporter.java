/**
 * 
 */
package br.unicamp.ic.microservices.graphs.csvexporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
import br.unicamp.ic.microservices.metrics.Metric;
import br.unicamp.ic.microservices.metrics.Metric.MetricType;
import br.unicamp.ic.microservices.model.Microservice;
import br.unicamp.ic.microservices.model.MicroservicesApplication;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class CSVExporter {

	public static final String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";

	public static void main(String[] args) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**metrics.json}");

		List<Path> filesList = MicroservicesGraphUtil.findFiles(searchFolder, matcher);
		Gson gson = new GsonBuilder().create();
		for (Path path : filesList) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				MicroservicesApplication app = gson.fromJson(reader, MicroservicesApplication.class);
				List<String[]> applicationMetrics = transformApplicationMetricsIntoStringList(app);
				List<String[]> microservicesMetrics = transformMicroservicesMetricsIntoStringList(app);
				exportMetricsToCSV(app.getName(), applicationMetrics, "application-metrics.csv");
				exportMetricsToCSV(app.getName(), microservicesMetrics, "microservices-metrics.csv");
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
	private static List<String[]> transformMicroservicesMetricsIntoStringList(MicroservicesApplication app) {
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
					metricsHash.put(i, new StringBuffer(String.valueOf(i)));
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
				Optional<Metric> acsMetric = findSpecificMetric(microservice.getMetrics(), MetricType.ACS);
				Object[] acsMetricValues = null;
				if (acsMetric.isPresent()) {
					acsMetricValues = acsMetric.get().getValues();
				}
				Optional<Metric> rcsMetric = findSpecificMetric(microservice.getMetrics(), MetricType.RCS);
				Object[] rcsMetricValues = null;
				if (rcsMetric.isPresent()) {
					rcsMetricValues = rcsMetric.get().getValues();
				}
				Optional<Metric> risMetric = findSpecificMetric(microservice.getMetrics(), MetricType.RIS);
				Object[] risMetricValues = null;
				if (risMetric.isPresent()) {
					risMetricValues = risMetric.get().getValues();
				}

				StringBuffer headerRow = metricsHash.get(0);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.ADS);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.AIS);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.ACS);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.RCS);
				headerRow.append("#").append(microservice.getName()).append("-").append(MetricType.RIS);

				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(adsMetricValues[i - 1].toString());
					dataRow.append("#").append(aisMetricValues[i - 1].toString());
					dataRow.append("#").append(acsMetricValues[i - 1].toString());
					dataRow.append("#").append(rcsMetricValues[i - 1].toString());
					dataRow.append("#").append(risMetricValues[i - 1].toString());
				}
			}
			for (Map.Entry<Integer, StringBuffer> row : metricsHash.entrySet()) {
				String[] dataArray = row.getValue().toString().split("#");
				microservicesMetrics.add(dataArray);
			}

		}
		return microservicesMetrics;
	}

	/**
	 * @param app
	 * @return
	 */
	private static List<String[]> transformApplicationMetricsIntoStringList(MicroservicesApplication app) {
		List<String[]> applicationMetrics = new ArrayList<String[]>();
		String[] header = "release#SIY#SCF#ADCS".split("#");
		applicationMetrics.add(header);
//		Optional<Metric> siyMetric = findSpecificMetric(app.getMetrics(), MetricType.SIY);
//		Object[] siyMetricValues = null;
		Optional<Metric> scfMetric = findSpecificMetric(app.getMetrics(), MetricType.SCF);
		Object[] scfMetricValues = null;		
		int numberOfReleases = 0;
		if (scfMetric.isPresent()) {
			scfMetricValues = scfMetric.get().getValues();
			numberOfReleases = scfMetricValues.length;
		}
		Optional<Metric> adcsMetric = findSpecificMetric(app.getMetrics(), MetricType.ADCS);
		Object[] adcsMetricValues = null;
		if (adcsMetric.isPresent()) {
			adcsMetricValues = adcsMetric.get().getValues();
		}

		for (int i = 0; i < numberOfReleases; i++) {
			StringBuffer sb = new StringBuffer(String.valueOf(i)).append("#").append("")
					.append("#").append(scfMetricValues[i].toString()).append("#")
					.append(adcsMetricValues[i].toString());
			String[] row = sb.toString().split("#");
			applicationMetrics.add(row);
		}
		return applicationMetrics;
	}

	private static Optional<Metric> findSpecificMetric(List<Metric> metrics, MetricType metricType) {
		return metrics.stream().filter(m -> m.getType().equals(metricType)).findFirst();
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
