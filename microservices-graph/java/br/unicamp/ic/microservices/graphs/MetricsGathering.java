/**
 * 
 */
package br.unicamp.ic.microservices.graphs;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Collection;

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
		for (File file : files) {
			if(file.isDirectory()) {
				// get all the files that store the graphs for each release of one application
				Collection<Path> graphFilesDOT = MicroservicesGraphUtil.findGraphFiles(file.getPath(), matcher);

			}
		}

	}

}
