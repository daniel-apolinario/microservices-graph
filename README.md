# microservices-graph-generator

Microservices Dependencies Graph Generation Tool

## Implementation Details ##

### Technologies used ###

We used the graph algorithms contained in the JGraphT library to generate the initial dependencies graphs. For the random graphs, we utilized the GnpRandomGraphGenerator class that follows the Erdős–Rényi model [13]. For the Barabasi Albert graphs, we utilized the BarabasiAlbertGraphGenerator class [1] that implements preferential attachment growth. 

### Microservices-related Design Patterns ###

Aiming at generating dependency graphs similar to real microservice-based systems, we apply six usual design patterns found in microservice-based software applications. We selected them from the catalog in \cite{MicroservicesIO}.
Thus, the main selection criterion is their possibility to be expressed in a dependency graph. The selected patterns are: API Composition, Event Domain, Externalized Configuration, API Gateway, Service Registry, and Distributed Tracing. These patterns are commonly adopted together, using for example the Netflix microservices components \footnote{https://netflix.github.io/}.

To diversify the creation of graphs, each design pattern has a probability of being introduced in the generated graph. The table~\ref{tab:settingParameters} describes all the configurations related to the design patterns to generate and evolve the graphs. 
% For example, if API Composition is configured with a 50\% percentage of probability, half of the generated graphs will have this pattern included. Some design patterns are applied only to a set of services in an application. Consequently, we created a configuration related to the proportion of nodes that will participate in the applied standard. Therefore, in these cases, there is a percentage range that is randomly chosen for each graph generated that will be used to calculate the number of nodes who have the design pattern applied.

After the basic graph is generated by the algorithms, our tool includes the nodes related to the design patterns. We can note this in the Figure~\ref{fig:dependency-graphs-samples} that presents nodes with the labels: GTW (Gateway), CPS (API Composition), REG (Service Registry), MSB (Message Service Broker) e CFG (External Configuration). 

### Generated Graphs Samples ###

![Random Graph Sample](random-graph-example.png)**a)** *Random Graph Sample*

![Barabasi Albert Graph Sample](barabasi-albert-example.png)**b)** *Barabasi-Albert Sample*

### Tool Configurations ###
Tool configurations related to the dependency graph generation and evolution:

| Parameter | Value | Description |
| --- | :---: | --- |
| Minimum growth rate | 30\% | Minimum percentage of nodes to be included in the evolution.|
| Maximum growth rate | 80\% | Maximum percentage of nodes to be included in the evolution.|
| Service Registry Probability | 100\% | Probability of a microservice application to implement the Service Registry pattern.|
| API Gateway Probability | 80\% | Probability of a microservice application to implement the API Gateway pattern.|
| API Gateway Ratio | 20\% | The ratio of nodes to be composed.|
| API Composition Probability | 50\% | Probability of a microservice application has at least one compositon service.|
| Distributed Tracing Probability | 50\% | Probability of a microservice application has one service for Distributed Tracing.|
| Event Domain Probability | 50\% | Probability of a microservice application to implement the event domain pattern.|
| Event Domain Ratio | 50\% | Probability of one service using the message broker.|
| Externalized Configuration Probability | 50\% | Probability of a microservice application to implement externalized configuration pattern.|
| Externalized Configuration Ratio | 40\% | Ratio of nodes using the externalized configuration.|
| Connected nodes range for megaservice | 30-80\% | One node is a megaservice if a percentage of all nodes of this application call this service.|
