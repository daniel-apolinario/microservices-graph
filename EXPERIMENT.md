## Experiment Report

We have designed an experiment for testing the metrics behavior in different scenarios. Due to the lack of open and available repositories with real cases of microservices applications, we worked with synthetic data (artificially-generated dependency graphs) representing microservice architectures to develop the proposed method. Therefore, we developed a tool for generating this data. This simulated environment enabled us to test different scenarios and provided additional insights to make decisions regarding the method development.

### Goal and Hypotheses

This study aims *to analyze* four coupling metrics, *for the purpose of* characterizing *with respect to* their behavior over time, *in the context of* artificially-generated dependency graphs representing MSA releases.
Based on this, we test the following hypotheses for each metric:

Based on this goal, we are testing the following hypotheses:

**Null Hypothesis (H0)**: There **is not** a significant difference in trends in the evolution of the one SID/SDD/ADCS/SCF metric between introducing and removing a architecture smell throughout releases of a MSA.

**Alternative Hypothesis (H1)**: There **is** a significant difference in trends in the evolution of the one SID/SDD/ADCS/SCF metric between introducing and removing a architecture smell throughout releases of a MSA.

## Experimental Design 

We adopted a full factorial design. Table below shows the scenarios resulting from the combinations between the two factors (*Graph Size*, and *Graph Evolution Scenario*) and their levels.

| Scenario | Graph Size | Graph Evolution |
| --- | :---: | :---: |
| 1 | Small| Improvement |
| 2 | Small| Erosion |
| 3 | Medium| Improvement |
| 4 | Medium| Erosion |
| 5 | Large| Improvement |
| 6 | Large| Erosion |


### Graph Structure
The generated dependency graphs need to be as realistic as possible w.r.t. coupling between microservices. In the absence of one specific reference model for microservice-based systems in the literature, we decided to follow the Barabasi-Albert model\cite{Barabasi1999}, which is an algorithm for generating random *scale-free networks* (SFN). SFN is a network whose degree distribution follows a power-law. Wheeldon et al \cite{Wheeldon2003} and Potanin et al. \cite{Potanin2005} verified in real Java programs that distributions of coupling metrics follow a power-law function, i.e., the vast majority classes have few dependencies whilst few classes have many dependencies. Wen et al. \cite{Wen2009} observed that dependencies between Java packages also follow scale-free properties. Many other studies \cite{Vsubelj2012} \cite{Jing2006}, observed that software objects have characteristics of complex networks such as scale-free and power-law. We understand that, semantically, coupling metrics for (micro)services have the meaning than OO coupling metrics. Therefore, all of the dependency graph\gammafollowing the power-law, which the probability of one new node connects with the pre-existing node is 

<img src="https://render.githubusercontent.com/render/math?math=p(k) = k ^{-\gamma}">

where <img src="https://render.githubusercontent.com/render/math?math=k"> is the number of connections of a node and <img src="https://render.githubusercontent.com/render/math?math=\gamma"> is the degree distribution component. In the Figure below, there is an example of a graph for each level.

![Barabasi Albert Graph Sample](barabasi-albert-example.png)

### Graph Size

A microservice-based system may vary in scale. Netflix and Amazon, pioneers in microservices architecture, claim to have hundreds of microservices in their core products \cite{NetflixMicroservices} \cite{AmazonMicroservices}. On the other hand, in open-source projects, we have many examples of applications that have few microservices, as we can see in "The Microservice Dataset, Version 1.0". \cite{Rahman2019}. 

In our experiment, we create three levels for the size of an application: small, medium and large. As we have no benchmark on the size of microservices application, we made an attempt to defined the application size in terms of amount of services as follows: from five to ten services it is considered a small application; from eleven to twenty-five services it is medium; above 25 is large, however, due to computational restrictions, we decided to limit it to 60 in this experiment, since we intend to evaluate the first results before scaling the number of services. 

### Graph Evolution Scenario

We are interested in how the metrics behave throughout software evolution. We established 21 releases (including the initial release 0) for the whole evolution of one application. The default changes during the evolution are limited to the inclusion of nodes. Additionally, we consider two levels for this factor: an improvement scenario and an erosion one. For the improvement scenario, we introduce one architecture smell in the first release and, during the following releases, the main action is to remove the smell. For the level of erosion of the architecture, the first release is free of architecture smell and, during the following releases, we introduce an architecture smell incrementally.

### Architecture Smells

For the improvement or erosion scenarios, we have chosen two coupling-related architecture problems: the concentration of incoming dependencies (problem 1) and outgoing dependencies (problem 2) around a single microservice. These problems reflect symptoms of known architecture smells with evidence of their existance in the field, such as God Component \cite{Azadi2019} or Megaservice \cite{Taibi2020}\cite{Bogner2019}, Hub-like Dependency \cite{Azadi2019}, Bottleneck Service\cite{Bogner2019}, Nanoservices\cite{Bogner2019} and The Knot\cite{Bogner2019}.

The number of edges to characterize a microservice with high concentration of incoming or outgoing dependencies is defined by a percentage of the total number of services in the system, which is a parameter of this experiment. Further details on experiment parameters and how architecture smells are included and removed from dependency graphs are available in a GitHub\textsuperscript{\ref{githubNote}} repository.

General assumptions in the evolution of architecture: 
1. Both problems are characterized in nodes that have a configured percentage of concentration of outgoing or incoming edges. Therefore, a problem is reached or resolved when it goes up or down this limit. 
2. The evolutions follow a node growth rate calculated randomly for each application within a defined range in minimum and maximum percentage of the application's number of nodes. 
3. After the problem is inserted (worsens) or solved (improves), new nodes are added randomly until the growth rate is reached.

*Architecture improvement scenario*: 
1. Problem 1 is inserted in the graph that represents the first release of the graph and in the next releases the newly added nodes receive half of the input edges of the node with a high concentration of incoming edges an so on. 
2. Problem 2 is inserted in the graph that represents the first release and in the next releases, according to the type of dependency, a node can be excluded (simulating the joining of services) or edges can be excluded.

*Architectural worsening scenario*: 
1. New nodes are inserted with an outgoing edge for the selected node to be the bearer of problem 1. 
2. Newly nodes are inserted with an incoming edge from the selected node to be the bearer of the problem 2.

