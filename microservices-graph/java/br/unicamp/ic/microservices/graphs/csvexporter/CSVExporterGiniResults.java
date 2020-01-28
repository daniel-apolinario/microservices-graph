/**
 * 
 */
package br.unicamp.ic.microservices.graphs.csvexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;

import br.unicamp.ic.microservices.experiment.ExperimentDesignConfig.GraphScenario;
import br.unicamp.ic.microservices.graphs.MicroservicesGraphUtil;
import br.unicamp.ic.microservices.graphs.generation.ExperimentTreatment;
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
	private static final String EXPERIMENTAL_TREATMENTS_FILE = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/experimentTreatments.json";

	public static void main(String[] args) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**metrics-with-gini.json}");
		List<String[]> giniResultsSummary = new ArrayList<String[]>();
		List<String[]> giniGroupedResultsSummary = new ArrayList<String[]>();
		List<ExperimentTreatment> experimentTreatmentList = getExperimentTreatmentList(EXPERIMENTAL_TREATMENTS_FILE);

		List<Path> filesList = MicroservicesGraphUtil.findFiles(searchFolder, matcher);
		Gson gson = new GsonBuilder().create();
		int releasesNumber = 0;
		for (Path path : filesList) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				MicroservicesApplication app = gson.fromJson(reader, MicroservicesApplication.class);
				List<String[]> applicationMetricsAndGiniValues = transformMetricsAndGiniValuesIntoStringList(app);
				// just for the first iteration, include the header row
				if (path.equals(filesList.get(0))) {
					releasesNumber = getReleasesNumber(app);
					giniResultsSummary.add(getHeaderRowSummary(releasesNumber));
				}
				String[] giniResultsRowSummary = getRowSummaryForApp(app, applicationMetricsAndGiniValues,
						experimentTreatmentList);
				giniResultsSummary.add(giniResultsRowSummary);
				exportMetricsToCSV(app.getName(), applicationMetricsAndGiniValues,
						app.getName() + "-metrics-gini-values.csv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		giniGroupedResultsSummary = groupResults(releasesNumber, giniResultsSummary);
		exportMetricsToCSV("", giniResultsSummary, "gini-results-summary.csv");
		exportMetricsToCSV("", giniGroupedResultsSummary, "gini-grouped-results.csv");
	}

	/**
	 * @param app
	 * @return
	 */
	private static int getReleasesNumber(MicroservicesApplication app) {
		int releasesNumber = 0;
		if (app != null && app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			releasesNumber = app.getDependenciesGraphs().size();
		}
		return releasesNumber;
	}

	/**
	 * @param giniResultsSummary
	 * @return
	 */
	private static List<String[]> groupResults(int releasesNumber, List<String[]> giniResultsSummary) {
		List<String[]> groupedResults = new ArrayList<>();
		groupedResults.add(getHeaderRowGroupedResults(releasesNumber));
		GiniGroupedResultKey currentKey = null;
		int totalNumberOfValues = giniResultsSummary.get(0).length - 6; // there are 6 fields that not are values
		double[] sumValues = initializeValuesArray(totalNumberOfValues);
		int replicasQuantity = 0;
		for (int i = 1; i <= giniResultsSummary.size(); i++) {
			GiniGroupedResultKey iterationKey = null;
			if (i < giniResultsSummary.size()) {
				iterationKey = new GiniGroupedResultKey(giniResultsSummary.get(i)[1], giniResultsSummary.get(i)[2],
						giniResultsSummary.get(i)[4], giniResultsSummary.get(i)[5]);
			}
			if (currentKey == null) {
				currentKey = iterationKey;
			}
			if (iterationKey == null || !currentKey.equals(iterationKey)) {
				StringBuffer dataRow = fillGroupedResultKeyFields(currentKey);
				for (int j = 0; j < sumValues.length; j++) {
					sumValues[j] = sumValues[j] / replicasQuantity;
					dataRow.append("#").append(sumValues[j]);
				}
				groupedResults.add(dataRow.toString().split("#"));
				replicasQuantity = 0;
				sumValues = initializeValuesArray(totalNumberOfValues);
				currentKey = iterationKey;
			}
			if (i < giniResultsSummary.size()) {
				for (int j = 0; j < sumValues.length; j++) {
					sumValues[j] = sumValues[j] + Double.valueOf(giniResultsSummary.get(i)[j + 6]);
				}
				replicasQuantity += 1;
			}

		}
		return groupedResults;
	}

	/**
	 * @param currentKey
	 * @return
	 */
	private static StringBuffer fillGroupedResultKeyFields(GiniGroupedResultKey currentKey) {
		StringBuffer groupedResultKeyFields = new StringBuffer();
		if (currentKey != null) {
			groupedResultKeyFields.append(currentKey.getApplicationType());
			groupedResultKeyFields.append("#").append(currentKey.getSizeApplication());
			groupedResultKeyFields.append("#").append(currentKey.getInitialApplicationStatus());
			groupedResultKeyFields.append("#").append(currentKey.getApplicationEvolution());
		}
		return groupedResultKeyFields;
	}

	/**
	 * @param totalNumberOfValues
	 * @return
	 */
	private static double[] initializeValuesArray(int totalNumberOfValues) {
		double[] valuesArray = new double[totalNumberOfValues];
		for (int i = 0; i < totalNumberOfValues; i++) {
			valuesArray[i] = 0;
		}
		return valuesArray;
	}

	private static String[] getHeaderRowGroupedResults(int releasesNumber) {
		StringBuffer header = new StringBuffer();
		header.append("TIPO");
		header.append("#").append("TAMANHO");
		header.append("#").append("RELEASE 0");
		header.append("#").append("EVOLUÇÃO");
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("GINI-ADS-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("GINI-AIS-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("SCF-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("ADCS-V" + i);
		}

		return header.toString().split("#");
	}

	private static List<ExperimentTreatment> getExperimentTreatmentList(String fileName) {
		List<ExperimentTreatment> experimentTreatmentList = null;
		File treatmentsFile = new File(fileName);

		Gson gson = new Gson();

		Type type = new TypeToken<ArrayList<ExperimentTreatment>>() {
		}.getType();

		try (Reader targetReader = new FileReader(treatmentsFile)) {
			experimentTreatmentList = gson.fromJson(targetReader, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return experimentTreatmentList;
	}

	/**
	 * @return
	 */
	private static String[] getHeaderRowSummary(int releasesNumber) {
		StringBuffer header = new StringBuffer();
		header.append("APPLICATION");
		header.append("#").append("TIPO");
		header.append("#").append("TAMANHO");
		header.append("#").append("VERTICES INÍCIO");
		header.append("#").append("RELEASE 0");
		header.append("#").append("EVOLUÇÃO");
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("GINI-ADS-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("GINI-AIS-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("SCF-V" + i);
		}
		for (int i = 0; i < releasesNumber; i++) {
			header.append("#").append("ADCS-V" + i);
		}
		return header.toString().split("#");
	}

	/**
	 * @param app
	 * @param applicationMetricsAndGiniValues
	 * @return
	 */
	private static String[] getRowSummaryForApp(MicroservicesApplication app,
			List<String[]> applicationMetricsAndGiniValues, List<ExperimentTreatment> experimentTreatmentList) {
		StringBuffer rowSummaryForApp = new StringBuffer();
		ExperimentTreatment expTreatment = findExperimentTreatmentByApplicationName(experimentTreatmentList,
				app.getName());
		rowSummaryForApp.append(expTreatment.getApplicationName());
		rowSummaryForApp.append("#").append(expTreatment.getGraphGeneratorParameters().getGraphStructure().name());
		rowSummaryForApp.append("#").append(expTreatment.getGraphGeneratorParameters().getGraphSize().name());
		rowSummaryForApp.append("#").append(expTreatment.getGraphGeneratorParameters().getVerticesNumber());
		if (GraphScenario.IMPROVE.equals(expTreatment.getGraphGeneratorParameters().getGraphScenario())) {
			rowSummaryForApp.append("#").append("MEGASERVICE PROBLEM");
		}
		if (GraphScenario.WORSEN.equals(expTreatment.getGraphGeneratorParameters().getGraphScenario())) {
			rowSummaryForApp.append("#").append("NO PROBLEM");
		}
		rowSummaryForApp.append("#").append(expTreatment.getGraphGeneratorParameters().getGraphScenario());
		int releasesNumber = 0;
		if (app != null && app.getDependenciesGraphs() != null && app.getDependenciesGraphs().size() > 0) {
			releasesNumber = app.getDependenciesGraphs().size();
		}
		for (int i = 1; i <= releasesNumber; i++) {
			String[] dataRow = applicationMetricsAndGiniValues.get(i);
			rowSummaryForApp.append("#").append(dataRow[1]); // 1 is index row for GINI-ADS values
		}
		for (int i = 1; i <= releasesNumber; i++) {
			String[] dataRow = applicationMetricsAndGiniValues.get(i);
			rowSummaryForApp.append("#").append(dataRow[2]); // 2 is index row for GINI-AIS values
		}
		for (int i = 1; i <= releasesNumber; i++) {
			String[] dataRow = applicationMetricsAndGiniValues.get(i);
			rowSummaryForApp.append("#").append(dataRow[3]); // 3 is index row for SCF metric values
		}
		for (int i = 1; i <= releasesNumber; i++) {
			String[] dataRow = applicationMetricsAndGiniValues.get(i);
			rowSummaryForApp.append("#").append(dataRow[4]); // 2 is index row for ADCS metric values
		}

		return rowSummaryForApp.toString().split("#");
	}

	public static ExperimentTreatment findExperimentTreatmentByApplicationName(
			List<ExperimentTreatment> experimentTreatmentList, String applicationName) {
		ExperimentTreatment experimentTreatment = null;
		Optional<ExperimentTreatment> expTreatmentOptional = experimentTreatmentList.stream()
				.filter(treatment -> applicationName.equals(treatment.getApplicationName())).findFirst();
		if (expTreatmentOptional.isPresent()) {
			experimentTreatment = expTreatmentOptional.get();
		}

		return experimentTreatment;
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

			// include gini values for ADS and AIS metrics between the microservices
			// metric values for all the releases
			StringBuffer headerToInclude = metricsHash.get(0);
			headerToInclude.append("#").append("APP - GINI -").append(MetricType.ADS);
			headerToInclude.append("#").append("APP - GINI - ").append(MetricType.AIS);
			headerToInclude.append("#").append("APP - ").append(MetricType.SCF);
			headerToInclude.append("#").append("APP - ").append(MetricType.ADCS);
			List<GiniSeries<MetricType, Integer, BigDecimal>> appGiniSeriesList = app.getGiniSeries();
			GiniSeries<MetricType, Integer, BigDecimal> appADSGiniSeries = findSpecificGiniSeriesByMetric(
					appGiniSeriesList, MetricType.ADS);
			GiniSeries<MetricType, Integer, BigDecimal> appAISGiniSeries = findSpecificGiniSeriesByMetric(
					appGiniSeriesList, MetricType.AIS);
			if (appADSGiniSeries != null && appAISGiniSeries != null) {
				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(appADSGiniSeries.getSeriesData().get(i - 1).toString());
					dataRow.append("#").append(appAISGiniSeries.getSeriesData().get(i - 1).toString());
				}
			}

			Optional<Metric> scfMetric = findSpecificMetric(app.getMetrics(), MetricType.SCF);
			Object[] scfMetricValues = null;
			if (scfMetric.isPresent()) {
				scfMetricValues = scfMetric.get().getValues();
			}
			if (scfMetricValues != null && scfMetricValues.length > 0) {
				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(scfMetricValues[i - 1].toString());
				}
			}
			Optional<Metric> adcsMetric = findSpecificMetric(app.getMetrics(), MetricType.ADCS);
			Object[] adcsMetricValues = null;
			if (adcsMetric.isPresent()) {
				adcsMetricValues = adcsMetric.get().getValues();
			}
			if (adcsMetricValues != null && adcsMetricValues.length > 0) {
				for (int i = 1; i <= numberOfReleases; i++) {
					StringBuffer dataRow = metricsHash.get(i);
					dataRow.append("#").append(adcsMetricValues[i - 1].toString());
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
