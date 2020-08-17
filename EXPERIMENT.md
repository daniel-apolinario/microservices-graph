## Experiment Description

We have designed an experiment for testing the metrics behavior in different scenarios. Due to the lack of open and available repositories with real cases of microservices applications, we worked with synthetic data (artificially-generated dependency graphs) representing microservice architectures to develop the proposed method. Therefore, we developed a tool for generating this data. This simulated environment enabled us to test different scenarios and provided additional insights to make decisions regarding the method development.

### Goal and Hypotheses

The aim of this study is *to analyze* releases of microservices applications, *for the purpose of* characterizing *with respect to* a coupling metrics suite and their sensitivity to reveal architecture erosion, *in the context of* artificially-generated dependency graphs representing microservice-based applications.

Based on this goal, we are testing the following hypotheses:

**Null Hypothesis**: The coupling metrics suite **is not** sensitive to reveal signs of architectural deterioration in dependency graphs that represent applications based on microservice architecture.

**Alternative Hypothesis**: The coupling metrics suite **is** sensitive to reveal signs of architectural deterioration in dependency graphs that represent applications based on microservice architecture.

## Experimental Design 

For designing different scenarios, we control three factors (independent variables) that are explained in the following sections: *Graph Structure*, *Graph Size*, *Graph Evolution Scenario*.

### Graph Structure
The generated dependency graphs need to be as realistic as possible w.r.t. coupling between microservices. In the absence of one specific reference model for microservice-based systems in the literature, we decided to follow the Barabasi-Albert model\cite{Barabasi1999}, which is an algorithm for generating random *scale-free networks* (SFN). SFN is a network whose degree distribution follows a power-law. Some works \cite{Wheeldon2003} \cite{Potanin2005} \cite{Wen2009} \cite{Myers2003} \cite{Vsubelj2012} \cite{Jing2006} have found characteristics of the SFN \cite{Barabasi1999} in software objects, mainly related to the object-oriented systems. Alternatively, we also use a random model of graph generation for comparison purposes.  

Therefore, we have two levels for the factor of graph structure (or generation models): the first is the traditional random graph generation, in which the probability of one new node connects with one already existent node is the same for all the nodes in the graph; the second is the preferential attachment process that follows the power-law, which the probability of one new node connects with the pre-existing node is 
\begin{equation}
p(k) = k ^{-\gamma} ,
\end{equation}
where $k$ is the number of connections of a node and $\gamma$ is the degree distribution component. In the Figure ~\ref{fig:dependency-graphs-samples}, there is an example of a graph for each level.

\subsubsection{Graph Size}
A microservice-based system may vary in scale. Netflix and Amazon, pioneers in microservices architecture, claim to have hundreds of microservices in their core products \cite{NetflixMicroservices} \cite{AmazonMicroservices}. On the other hand, in open-source projects, we have many examples of applications that have few microservices, as we can see in "The Microservice Dataset, Version 1.0". \cite{Rahman2019}. 

In our experiment, we create three levels for the size of an application: small, medium and large. As we have no benchmark on the size of microservices application, we made an attempt to defined the application size in terms of amount of services as follows: from five to ten services it is considered a small application; from eleven to twenty-five services it is medium; above 25 is large, however, due to computational restrictions, we decided to limit it to 60 in this experiment, since we intend to evaluate the first results before scaling the number of services. 

\subsubsection{Graph Evolution Scenario}
We are interested in how the metrics behave throughout software evolution. Therefore, we consider two levels for this factor: a scenario of improvement in the architecture and another of deterioration. For the improvement scenario, we introduce one architecture smell in the first release and, during the next releases, the main action is to remove the architecture smell. For the level of deterioration of the architecture, the first release is free of architecture smell and, during the following releases, we introduce an architecture smell. 

Since it is a simulation, we decided to apply full factorial design to this experiment. The Table~\ref{tab:experimentScenarios} shows all the scenarios resulting from the combinations between the factors and their levels.

\begin{table}[!ht]
    \caption{Experiment Scenarios}
    \label{tab:experimentScenarios}
    \begin{tabular}{l|ccc}
        \toprule
        Scenario & \multicolumn{3}{c}{Factors and Levels}\\
        \midrule
        \multicolumn{1}{l|}{\#} & \multicolumn{1}{c|}{Graph Structure} & \multicolumn{1}{c|}{Graph Size} & Graph Evolution\\
        \midrule
        1 & Random & Small & Improvement\\
        2 & Random & Small & Deterioration\\
        3 & Random & Medium & Improvement\\
        4 & Random & Medium & Deterioration\\
        5 & Random & Large & Improvement\\
        6 & Random & Large & Deterioration\\
        7 & Barabasi-Albert & Small & Improvement\\
        8 & Barabasi-Albert & Small & Deterioration\\
        9 & Barabasi-Albert & Medium & Improvement\\
        10 & Barabasi-Albert & Medium & Deterioration\\
        11 & Barabasi-Albert & Large & Improvement\\
        12 & Barabasi-Albert & Large & Deterioration\\
      \bottomrule
    \end{tabular}
\end{table}
