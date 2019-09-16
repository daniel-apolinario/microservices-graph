/*
 * (C) Copyright 2003-2018, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package br.unicamp.ic.microservices.graphs;


import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.server.ExportException;
import java.util.Iterator;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.*;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.SupplierUtil;


/**
 * A simple introduction to using JGraphT.
 *
 * @author Barak Naveh
 */
public final class HelloJGraphT
{
    private HelloJGraphT()
    {
    } // ensure non-instantiability.

    /**
     * The starting point for the demo.
     *
     * @param args ignored.
     *
     * @throws URISyntaxException if invalid URI is constructed.
     * @throws ExportException if graph cannot be exported.
     */
    public static void main(String[] args)
        throws URISyntaxException,
        ExportException
    {

              //  generateBarabasiAlbertGraph();
               
                generateRandomGraph();
                

    }

  
    private static void generateRandomGraph() {
    	 Supplier<String> vSupplier = new Supplier<String>()
         {
             private int id = 0;

             @Override
             public String get()
             {
                 return "v" + id++;
             }
         };

         // @example:generate:begin
         // Create the graph object
         Graph<String, DefaultEdge> randomGraph =
             new SimpleDirectedGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

         // Create the CompleteGraphGenerator object
         GnpRandomGraphGenerator<String, DefaultEdge> randomGenerator = new GnpRandomGraphGenerator<>(10, 0.2);

         // Use the CompleteGraphGenerator object to make completeGraph a
         // complete graph with [size] number of vertices
         randomGenerator.generateGraph(randomGraph);
         // @example:generate:end

         // use helper classes to define how vertices should be rendered,
         // adhering to the DOT language restrictions
         ComponentNameProvider<String> vertexIdProvider = new ComponentNameProvider<String>()
         {
             public String getName(String name)
             {
                 return name;
             }
         };
         ComponentNameProvider<String> vertexLabelProvider = new ComponentNameProvider<String>()
         {
             public String getName(String name)
             {
                 return name;
             }
         };
         GraphExporter<String, DefaultEdge> exporter =
                 new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
             Writer writer = null;
             try {
     			writer  = new FileWriter("/home/daniel/Downloads/grafo.dot");
     		} catch (IOException e1) {
     			// TODO Auto-generated catch block
     			e1.printStackTrace();
     		}
             //Writer writer = new StringWriter();
             try {
     			exporter.exportGraph(randomGraph, writer);
     		} catch (org.jgrapht.io.ExportException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}				
	}

	private static void generateBarabasiAlbertGraph() {
    	 // Create the VertexFactory so the generator can create vertices
        Supplier<String> vSupplier = new Supplier<String>()
        {
            private int id = 0;

            @Override
            public String get()
            {
                return "v" + id++;
            }
        };

        // @example:generate:begin
        // Create the graph object
        Graph<String, DefaultEdge> baGraph =
            new SimpleDirectedGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);

        // Create the CompleteGraphGenerator object
        BarabasiAlbertGraphGenerator<String, DefaultEdge> baGenerator =
            new BarabasiAlbertGraphGenerator<>(2, 1, 10);

        // Use the CompleteGraphGenerator object to make completeGraph a
        // complete graph with [size] number of vertices
        baGenerator.generateGraph(baGraph);
        // @example:generate:end

        // use helper classes to define how vertices should be rendered,
        // adhering to the DOT language restrictions
        ComponentNameProvider<String> vertexIdProvider = new ComponentNameProvider<String>()
        {
            public String getName(String name)
            {
                return name;
            }
        };
        ComponentNameProvider<String> vertexLabelProvider = new ComponentNameProvider<String>()
        {
            public String getName(String name)
            {
                return name;
            }
        };
        GraphExporter<String, DefaultEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
            Writer writer = null;
            try {
    			writer  = new FileWriter("/home/daniel/Downloads/grafo.dot");
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
            //Writer writer = new StringWriter();
            try {
    			exporter.exportGraph(baGraph, writer);
    		} catch (org.jgrapht.io.ExportException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}		
	}

	/**
     * Traverse a graph in depth-first order and print the vertices.
     *
     * @param hrefGraph a graph based on URI objects
     *
     * @param start the vertex where the traversal should start
     */
    private static void traverseHrefGraph(Graph<URI, DefaultEdge> hrefGraph, URI start)
    {
        Iterator<URI> iterator = new DepthFirstIterator<>(hrefGraph, start);
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            System.out.println(uri);
        }
    }

  
}
