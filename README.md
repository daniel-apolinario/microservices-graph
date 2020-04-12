# microservices-graph-generator

Microservices Dependencies Graph Generation Tool

## Implementation Details ##

### Technologies used ###

We used the graph algorithms contained in the JGraphT library to generate the initial dependencies graphs. For the random graphs, we utilized the GnpRandomGraphGenerator class that follows the Erdős–Rényi model [13]. For the Barabasi Albert graphs, we utilized the BarabasiAlbertGraphGenerator class [1] that implements preferential attachment growth. 

## Tool Configurations ##
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
