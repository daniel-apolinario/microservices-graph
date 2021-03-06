## Research Summary

This experiment was performed as part of a Software Engineering research project. The research project aims to develop a method for monitoring the evolution of coupling metrics for microservice-based architectures (MSAs), assuming it can support software engineers on improving software maintainability. For this, we select a subset of metrics for MSA found in the research literature [[1]](#1). 

### Coupling Metrics

The following metrics should be collected per individual service:
* **Absolute Importance of the Service (AIS)**: number of consumers invoking at least one operation from a service S1. The higher the AIS, the more important the service S1 is within the system. Average AIS can be useful for identifying and quantifying the most critical services.
* **Absolute Dependence of the Service (ADS)**: number of services on which the S1 service depends. In other words, ADS is the number of services that S1 calls for its operation to be complete. The higher the ADS, the more this service depends on other services, i.e., it is more vulnerable to the side effects of failures in the services invoked.

The following metrics work for the entire application:
* **Service Coupling Factor (SCF)**: this is a measure of the density of a graph's connectivity. <img src="https://render.githubusercontent.com/render/math?math=SCF = SC/(N^2 - N)">, where <img src="https://render.githubusercontent.com/render/math?math=SCF"> is the sum of all calls between services, and <img src="https://render.githubusercontent.com/render/math?math=N"> is the total number of services.
* **Average Number of Directly Connected Services (ADCS)**: Average of ADS metric of all services.

Analyzing metrics individually per microservice could lead to misunderstandings about the evolution of the complete system. This way, we use the Gini coefficient to analyze both the ADS and AIS metrics described above.

The Gini' coefficient is currently widely used to measure the distribution of wealth in the field of Economics. This index has the advantage of working with a [0;1] interval regardless of the statistical distribution of the data. Its value reveals how unequal the values of the coupling metrics (ADS and AIS) are among microservices in the same application. Thus, it allows to observe if few microservices concentrates coupling. For example, the *Single Responsibility Principle* is an important design principle for microservices, in which a service has one single responsibility. A Gini coefficient with a higher value may indicate a possible violation of the *Single Responsibility Principle*, as there must be a small number of services concentrating incoming or outgoing calls (logical coupling). There are several applications of the Gini coefficient in the literature for the software evolution analysis [[2]](#2) [[3]](#3) [[4]](#4). However, non of these works apply it in the context of microservices. 
We calculated the Gini coefficient <img src="https://render.githubusercontent.com/render/math?math=G"> as defined in [[5]](#5):

<img src="https://render.githubusercontent.com/render/math?math=G = \frac{\sum_{i=1}^{n} (2i - n - 1) x_i} {n \sum_{i=1}^{n} x_i}"> 

where the values <img src="https://render.githubusercontent.com/render/math?math=x_i">, <img src="https://render.githubusercontent.com/render/math?math=x_{i%2B1}">,...<img src="https://render.githubusercontent.com/render/math?math=x_n"> are ordered, <img src="https://render.githubusercontent.com/render/math?math=n"> is the number of values to be computed, and <img src="https://render.githubusercontent.com/render/math?math=i"> represents the rank of the value <img src="https://render.githubusercontent.com/render/math?math=x">. 
<img src="https://render.githubusercontent.com/render/math?math=G"> assumes values between 0 and 1, in which the value 0 indicates perfect equality, whereas values closer to 1 indicate more inequality among the observations. 

Therefore, the metrics evaluated in this experimental analysis are **SCF** and **ADCS** (both explained above), as well as two derived metrics:
* *Gini coefficient for AIS*: calculated using the individual AIS measures for each microservice in a given release. That is, this coefficient indicates how the importance of services are distributed among themselves. Values close to zero mean an even distribution of importance among the microservices. Otherwise, values close to one mean the importance are very concentrated in a few services. To simplify, we call this metric ***Service Importance Distribution (SID)***.
* *Gini coefficient for ADS*: calculated using the individual ADS measures for each microservice in a given release. That is, this coefficient indicates how balanced are dependencies among services. When close to zero, it represents evenly distributed dependencies among the microservices. Otherwise, values close to one mean that few services concentrate many dependencies. To simplify, we call this metric ***Service Dependency Distribution (SDD)***.


## Experiment Report

We have designed an experiment for testing the metrics behavior in different scenarios. Due to the lack of open and available repositories with real cases of microservices applications, we worked with synthetic data (artificially-generated dependency graphs) representing microservice architectures to develop the proposed method. Therefore, we developed a [tool](README.md) for generating this data. This simulated environment enabled us to test different scenarios and provided additional insights to make decisions regarding the method development.

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
The generated dependency graphs need to be as realistic as possible w.r.t. coupling between microservices. In the absence of one specific reference model for microservice-based systems in the literature, we decided to follow the Barabasi-Albert model [[6]](#6), which is an algorithm for generating random *scale-free networks* (SFN). SFN is a network whose degree distribution follows a power-law. Wheeldon et al [[7]](#7) and Potanin et al. [[8]](#8) verified in real Java programs that distributions of coupling metrics follow a power-law function, i.e., the vast majority classes have few dependencies whilst few classes have many dependencies. Wen et al. [[9]](#9) observed that dependencies between Java packages also follow scale-free properties. Many other studies [[10]](#10) [[11]](#11), observed that software objects have characteristics of complex networks such as scale-free and power-law. We understand that, semantically, coupling metrics for (micro)services have the meaning than OO coupling metrics. Therefore, all of the dependency graph following the power-law, which the probability of one new node connects with the pre-existing node is 

<img src="https://render.githubusercontent.com/render/math?math=p(k) = k ^{-\gamma}">

where <img src="https://render.githubusercontent.com/render/math?math=k"> is the number of connections of a node and <img src="https://render.githubusercontent.com/render/math?math=\gamma"> is the degree distribution component. In the Figure [below](#barabasi-albert-graph-example), there is an example of a graph for each level.

##### Barabasi Albert Graph Example
<p align="center">     
    <img src="https://github.com/daniel-apolinario/microservices-graph/blob/master/barabasi-albert-example.png"/>
</p>

### Graph Size

A microservice-based system may vary in scale. Netflix and Amazon, pioneers in microservices architecture, claim to have hundreds of microservices in their core products [[12]](#12) [[13]](#13). On the other hand, in open-source projects, we have many examples of applications that have few microservices, as we can see in "The Microservice Dataset, Version 1.0" [[14]](#14). 

In our experiment, we create three levels for the size of an application: small, medium and large. As we have no benchmark on the size of microservices application, we made an attempt to defined the application size in terms of amount of services as follows: from five to ten services it is considered a small application; from eleven to twenty-five services it is medium; above 25 is large, however, due to computational restrictions, we decided to limit it to 60 in this experiment, since we intend to evaluate the first results before scaling the number of services. 

### Graph Evolution Scenario

We are interested in how the metrics behave throughout software evolution. We established 21 releases (including the initial release 0) for the whole evolution of one application. The default changes during the evolution are limited to the inclusion of nodes. Additionally, we consider two levels for this factor: an improvement scenario and an erosion one. For the improvement scenario, we introduce one architecture smell in the first release and, during the following releases, the main action is to remove the smell. For the level of erosion of the architecture, the first release is free of architecture smell and, during the following releases, we introduce an architecture smell incrementally.

### Architecture Smells

For the improvement or erosion scenarios, we have chosen two coupling-related architecture problems: the concentration of incoming dependencies (problem 1) and outgoing dependencies (problem 2) around a single microservice. These problems reflect symptoms of known architecture smells with evidence of their existance in the field, such as God Component [[15]](#15) or Megaservice [[16]](#16) [[17]](#17), Hub-like Dependency [[15]](#15), Bottleneck Service[[17]](#17), Nanoservices[[17]](#17) and The Knot[[17]](#17).

The number of edges to characterize a microservice with high concentration of incoming or outgoing dependencies is defined by a percentage of the total number of services in the system, which is a parameter of this experiment.

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

### Microservices-related Design Patterns

Aiming at generating dependency graphs similar to real microservice-based systems, we apply six usual design patterns found in microservice-based software applications [[18]](#18) and that can also be expressed in a dependency graph. The selected patterns are API Composition, Message Service Broker, Externalized Configuration, API Gateway, Service Registry, and Distributed Tracing. We know that some design patterns can increase coupling and also concentrate incoming or outgoing edges on a few nodes. Therefore, the method is applied to the evolution of metrics values, performing the comparison with itself, thus avoiding the creation of generic thresholds that fail to consider the different architectural decisions of each software. 
The configurations related to the inclusion of these design patterns are available in this [page](README.md).

### Replications and Procedure

We need multiple trials as we have stochastic components to generate graphs, so that we can quantify variation in the results. 
To determine the minimum number of replications we adopted the procedure in [[19]](#19). 
After executing this procedure, we reached the amount of 210 replications. Based on this, we followed the experimental procedure:
* *Graph Generation*: based on the factors and levels, the tool generates the dependency graph corresponding to the first release of each MSA. As we have 6 scenarios and 210 replications each, we generated 1260 experimental units. For all the units in the improvement scenario, we introduced one architecture smell in the first release.
* *Application evolution*: according to the evolution scenario, the tool generates a dependency graph for each of the next 20 releases.
* *Metrics calculation*: the metrics are calculated for each release of each MSA. 
* *Metrics analysis*: we used the Cox-Stuart test to detect trends for each experimental unit, being ten statistical tests per unit considering a range of 12 observations per test. Additionally, we explore trends for the scenarios visually using the mean values for the coupling metrics evolution.
* *Hypothesis testing*: we used the *Chi-Square* Test of Independence to evaluate the experimental hypotheses for each individual replication. Also, we used the Cramér's V statistic as the *Chi-Square* Test is sensitive to large sample sizes.

## Experimental Results

Firstly, we analyze the general behavior of the metrics. For each scenario, we grouped the metric values of all replications, and we calculated the mean values for each release as they are independent. Figure [below](#mean-trends-for-the-medium-size-graphs) shows the plot of the four metrics for the scenario using graphs of medium size as an 
example of how we can get a visual sense of the metrics with most evident upward or downward trends.

##### Mean trends for the medium-size graphs
<p align="center">
     <img src="https://github.com/daniel-apolinario/microservices-graph/blob/master/BA-MEDIUM-METRICS.png"/>
</p>

In this analysis, the SCF metric presents a downward trend in all scenarios, even when purposefully introducing smells. Therefore, it is not sensitive enough to detect the architecture changes introduced during the experiment. The SCF metric seems to be more sensitive to the number of nodes in the graph than to the coupling between them, since the average number of edges does not vary significantly during evolution. An increase in SCF would reveal a chaotic architecture, where it should be evident the architecture remarkably deteriorated. 

We also realized the vast majority of cases in architectural erosion scenarios present an upward trend. In contrast, for ten improvement scenarios, trends are difficult to be identified only by visual analysis. In general, the SID and SDD metrics seem to be good indicators for the architecture smells, as their trends are easier to detect when there is a concentration of input or output edges in few nodes. 
The ADCS metric shows more considerable differences in behavior according to the size of the application, 
performing better for small applications.

We use Cox-Stuart test to characterize statistically a trend (upward or downward) in the series metrics values through the releases. In this experiment, we performed ten trend tests for each experimental unit (MSA), considering all possible intervals of 12 releases length (from release <img src="https://render.githubusercontent.com/render/math?math=n"> to <img src="https://render.githubusercontent.com/render/math?math=n %2B 1"> successively). For each scenario, we determined a contingency table similar to this [table](#contingency-tables-for-medium-graphs-scenario).

##### Contingency Tables for Medium Graphs Scenario
<table>
    <thead>
        <tr>
            <th rowspan="2">Scenario</th>
            <th colspan="3">SID</th>
            <th colspan="3">SDD</th>
            <th colspan="3">ADCS</th>
            <th colspan="3">SCF</th>
        </tr>
        <tr>            
            <th style="width:7%">Imp.</th>
            <th style="width:7%">No Trend</th>
            <th style="width:7%">Eros.</th>
            <th style="width:7%">Imp.</th>
            <th style="width:7%">No Trend</th>
            <th style="width:7%">Eros.</th>
            <th style="width:7%">Imp.</th>
            <th style="width:7%">No Trend</th>
            <th style="width:7%">Eros.</th>
            <th style="width:7%">Imp.</th>
            <th style="width:7%">No Trend</th>
            <th style="width:7%">Eros.</th>          
        </tr>
    </thead>  
    <tbody>
        <tr>
            <td align="center">Improve</td>
            <td align="center">60</td>
            <td align="center">80</td>
            <td align="center">70</td>
            <td align="center">73</td>
            <td align="center">85</td>
            <td align="center">52</td>
            <td align="center">82</td>
            <td align="center">89</td>
            <td align="center">39</td>
            <td align="center">117</td>
            <td align="center">85</td>
            <td align="center">8</td>
        </tr>
         <tr>
            <td align="center">Erosion</td>
            <td align="center">12</td>
            <td align="center">0</td>
            <td align="center">198</td>
            <td align="center">27</td>
            <td align="center">2</td>
            <td align="center">181</td>
            <td align="center">67</td>
            <td align="center">9</td>
            <td align="center">134</td>
            <td align="center">210</td>
            <td align="center">0</td>
            <td align="center">0</td>
        </tr>
        <tr>
            <td colspan="13">
            <b>Imp.</b>= Improving Trend, <b>Eros.</b>= Erosion Trend
            </td>
        </tr>
    </tbody>    
    
</table>

For each MSA, we count as *Improve Scenario* and *Improving Trend* when at least one of the ten tests resulted in Improving Trend. For instance (in the [contingency table](#contingency-tables-for-medium-graphs-scenario)), the evolution of 60 applications reveals a significant improving trend for the SID metric when we remove the architecture smell. The same is valid for counting as *Erosion Scenario* and *Erosion Trend*; that is when at least one of the ten tests resulted in Erosion Trend. Similarly, from 210 unities (MSA) in the erosion scenario, SID revealed a significant increase for 198 MSAs.

We justify this rationale as just one single intervention is made to improve or deteriorate the application, so it must affect the series in a unique change-point. Conversely, *Improve Scenario* and *Erosion Trend* will be computed when there is at least one test resulting in Erosion Trend and none resulting in Improving Trend. The opposite case (*Erosion Scenario* and *Improving Trend*) occurs when there is at least one test resulting in Improving Trend and none Erosion Trend. Finally, we count as *No Trend* only when all ten tests result in No Trend, i.e., it has no statistical significance.

Based on the contingency table for each scenario, we used the Chi-Square test of independence to verify how correlated are the intended evolution scenarios (Improve or Erosion) and the result of the Cox-Stuart test for trend analysis (results in Table [below](#experiment-results-per-metric)). We do not consider the SCF metric for testing the experiment's hypotheses due to its anomalous behavior (monotonic-decreasing no matter the scenario), which is also reflected in the [Contingency Table](#contingency-tables-for-medium-graphs-scenario).

##### Experiment Results per Metric
<table>
    <thead>
        <tr>
            <th>Scenarios</th>
            <th colspan="3">SID</th>
            <th colspan="3">SDD</th>
            <th colspan="3">ADCS</th>
        </tr>
        <tr>            
            <th style="width:7%">Size</th>
            <th style="width:7%">Chi</th>
            <th style="width:7%">p-value</th>
            <th style="width:7%"><img src="https://render.githubusercontent.com/render/math?math=\varphi_c"></th>
            <th style="width:7%">Chi</th>
            <th style="width:7%">p-value</th>
            <th style="width:7%"><img src="https://render.githubusercontent.com/render/math?math=\varphi_c"></th>
            <th style="width:7%">Chi</th>
            <th style="width:7%">p-value</th>
            <th style="width:7%"><img src="https://render.githubusercontent.com/render/math?math=\varphi_c"></th>            
        </tr>
    </thead>    
    <tbody>
        <tr>
            <td align="center">Large</td>
            <td align="center">145.53</td>
            <td align="center">2.5e-32</td>
            <td align="center">0.59</td>
            <td align="center">256.91</td>
            <td align="center">1.6e-56</td>
            <td align="center">0.78</td>
            <td align="center">142.62</td>
            <td align="center">1.1e-31</td>
            <td align="center">0.58</td>
        </tr>
        <tr>
            <td align="center">Medium</td>
            <td align="center">173.13</td>
            <td align="center">2.5e-38</td>
            <td align="center">0.64</td>
            <td align="center">171.76</td>
            <td align="center">5.0e-38</td>
            <td align="center">0.64</td>
            <td align="center">118.98</td>
            <td align="center">1.4e-26</td>
            <td align="center">0.53</td>
        </tr>
        <tr>
            <td align="center">Small</td>
            <td align="center">96.92</td>
            <td align="center">9.0e-22</td>
            <td align="center">0.48</td>
            <td align="center">65.01</td>
            <td align="center">7.6e-15</td>
            <td align="center">0.39</td>
            <td align="center">148.36</td>
            <td align="center">6.1e-33</td>
            <td align="center">0.59</td>
        </tr>        
        <tr>
            <td colspan="10">
                <b>Chi </b>= Chi-squared test statistic </br>
            <b>Chi </b>= Chi-squared test's p-value </br>
            <b><img src="https://render.githubusercontent.com/render/math?math=\varphi_c"> </b>= Cramér’s V statistic 
            </td>
        </tr>
    </tbody>
</table>



Table [above](#experiment-results-per-metric) also presents the Cramer's V measure. We use it in association with the Chi-Square test as the latter is sensitive to large sample sizes. The Cramer's V measures the correlation between two nominal variables (architectural evolution scenario and detected trends) for each coupling metric as an interval between zero (no association) and one (strong association). We consider rejecting the null hypothesis when the Chi-Square test (<img src="https://render.githubusercontent.com/render/math?math=p-value < 0.05">) and the Cramer's V statistic (<img src="https://render.githubusercontent.com/render/math?math=\varphi_c > 0.5">) result in a significant association. 

Therefore, we could not reject **H0** in the scenarios: SID metric with small MSAs and SDD metric with small MSAs. Except for these two combinations of metrics and scenarios, we can reject **H0** and accept the **H1** for the other 10 combinations (metrics x MSA size). 

The SDD metric for large graphs shows great results, since Cramér's V points to a strong correlation (0.78). The SID metric also has good results, mainly for large and medium MSAs. The ADCS metric seems to work appropriately for all MSA sizes.

The results presented help us to discard the use of the SCF metric and to validate the use of statistical trend calculations.

## Threats to Validity

We have no empirical evidence whether the model we used to create and evolve the graph structures used in the experiment resembles the graph structures of real MSAs. However, we do have evidence on this for other types of software. In architectural terms, the main difference is that MSAs have an extra level of abstraction (services).

In the wild scenario, several problems can occur together, and there may be problems that can cancel each other's effects. However, the controlled use of architecture smells in this experiment gives us the advantage of isolating the causes of metrics deterioration.

The trend analysis is effective but does not take into account *level changes* in a time series, and can cause misinterpretations when this occurs. We mitigate this by using several intervals for a single evolution, so that we could detect multiple change points.

Finally, the *Chi-Square* Test is sensitive to large sample sizes like the one we have in the experiment design considering the number of replications. Thus, it may impose a threat to conclusion validity. However, we associated the Cramér's V statistic to support the effect size analysis. Besides, the Chi-Square test statistic represent the independence magnitude, from which we can highlight the difference across the three metrics, corroborating the results in the Table [above](#experiment-results-per-metric) and discussion. 

### References ###
<a id="1">[1]</a> 
J. Bogner, S. Wagner, and A. Zimmermann. 2017. Automatically Measuring the Maintainability of Service- and Microservice-based Systems: A Literature Review. InProc. of the 27th Int. Workshop on Software Measurement(Gothenburg, Sweden). ACM, New York, NY, USA, 107–115

<a id="2">[2]</a> 
Giger, Emanuel & Pinzger, Martin & Gall, Harald. (2011). Using the Gini coefficient for bug prediction in eclipse. IWPSE-EVOL'11 - Proceedings of the 12th International Workshop on Principles on Software Evolution. 51-55. 10.1145/2024445.2024455. 

<a id="3">[3]</a>
Rajesh Vasa, Markus Lumpe, Philip Branch, and Oscar Nierstrasz. 2009. Comparative analysis of evolving software systems using the Gini coefficient. In 2009 IEEE International Conference on Software Maintenance. IEEE, 179–188.

<a id="4">[4]</a> 
S.D. Adnan. 2019. Software evolution on azureus bit torrent software: A study on growth and change analysis.Journal of Engineering Science and Technology 14 (01 2019), 430–447.
 
<a id="5">[5]</a> 
Kuan Xu. 2003.  How has the literature on Gini’s index evolved in the past 80 years? Dalhousie University, Economics Working Paper(2003).

<a id="6">[6]</a> 
Albert-László Barabási and Réka Albert. 1999. Emergence of Scaling in Random Networks.Science286, 5439 (1999), 509–512.

<a id="7">[7]</a> 
Richard Wheeldon and Steve Counsell. 2003.  Power law distributions in class relationships. In Proceedings Third IEEE International Workshop on Source Code Analysis and Manipulation. IEEE, 45–54.

<a id="8">[8]</a> 
Alex Potanin, James Noble, Marcus Frean, and Robert Biddle. 2005.  Scale-free geometry in OO programs.Commun. ACM 48, 5 (2005), 99–103.

<a id="9">[9]</a> 
Lian Wen, R Geoff Dromey, and Diana Kirk. 2009.  Software Engineering and Scale-Free Networks *. IEEE Transactions on Systems, Man, and Cybernetics, Part B (Cybernetics)39, 4 (2009), 845–854.

<a id="10">[10]</a> 
Lovro Šubelj and Marko Bajec. 2012. Software systems through complex networks science: Review, analysis and applications. In Proceedings of the First International Workshop on Software Mining. 9–16.

<a id="11">[11]</a> 
Liu Jing, He Keqing, Ma Yutao, and Peng Rong. 2006. Scale free in software metrics. In 30th Annual International Computer Software and Applications Conference (COMPSAC’06), Vol. 1. IEEE, 229–235.

<a id="12">[12]</a> 
MayukhNair.[n.d.]. How Netflix works:the (hugelysimplified) complex stuf fthat happens every time you hit Play. https://medium.com/refraction-tech-everything/how-netflix-works-the-hugely-simplified-complex-stuff-that-happens-every-time-you-hit-play-3a40c9be254b [Online] [Accessed: 28-Mar-2020].

<a id="13">[13]</a> 
Cristian Satnic. [n.d.]. Amazon, microservices and the birth of AWS cloud computing. https://www.linkedin.com/pulse/amazon-microservices-birth-aws-cloud-computing-cristian-satnic[On-line] [Accessed: 28-Mar-2020].

<a id="14">[14]</a> 
Davide Rahman, MI.and Taibi. 2019. A curated Dataset of Microservices-Based Systems. In Joint Proceedings of the Summer School on Software Maintenance and Evolution (Tampere, Finland). CEUR-WS.

<a id="15">[15]</a> 
U. Azadi, F. Arcelli Fontana, and D. Taibi. 2019. Architectural Smells Detected by Tools: a Catalogue Proposal. In2 019 IEEE/ACM International Conference on Technical Debt (TechDebt). 88–97.

<a id="16">[16]</a> 
Davide Taibi, Valentina Lenarduzzi, and Claus Pahl. 2020. Microservices Anti-patterns: A Taxonomy. In Microservices. Springer, 111–128.

<a id="17">[17]</a> 
J. Bogner, T. Boceck, M. Popp, D. Tschechlov, S. Wagner, and A. Zimmermann. 2019.  Towards a Collaborative Repository for the Documentation of Service-Based Antipatterns and Bad Smells. In 2019 IEEE International Conference on Software Architecture Companion (ICSA-C). 95–101.

<a id="18">[18]</a> 
https://microservices.io/

<a id="19">[19]</a> 
Averill M Law, W David Kelton, and W David Kelton. 2013. Simulation modeling and analysis. Vol. 5. McGraw-Hill Education.

