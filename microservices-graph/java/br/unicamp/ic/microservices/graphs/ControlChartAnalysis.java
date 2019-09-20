/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Daniel R. F. Apolinario
 *
 */
public class ControlChartAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String searchFolder = "/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/";
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:{**.json}");

		List<Path> filesList = MicroservicesGraphUtil.findFiles(searchFolder, matcher);

		Gson gson = new GsonBuilder().create();
		for (Path path : filesList) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				MicroservicesApplication app = gson.fromJson(reader, MicroservicesApplication.class);
				createControlCharts(app);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param app
	 */
	private static void createControlCharts(MicroservicesApplication app) {
		
		
	}

}
