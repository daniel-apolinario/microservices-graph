# requires  package;
# 1) tidyverse
# 2) randtests
# 3) lsr
library(tidyverse)
library(randtests)
library(lsr)

# Lê os dados das métricas SCF, ADCS, Gini-ADS e Gini-AIS para todas as aplicações do experimento
results=read.csv("/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/gini-results-summary.csv")
results
summary(results)

# Title
cat("Análise de independência de variáveis", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt")
# add 2 newlines
cat("\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)


#pdf("/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/analise-correlacao-experimento.pdf")

# Separa em dataframes diferentes para cada métrica
results_GINI_ADS=results[, c(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27)]
results_GINI_AIS=results[, c(1,2,3,4,5,6,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48)]
results_SCF=results[, c(1,2,3,4,5,6,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69)]
results_ADCS=results[, c(1,2,3,4,5,6,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90)]

# cria função que retorna resultado de análise de tendência para séries temporais usando o teste de Cox Stuart
getTrendAnalysis = function(x){
  test1 <- cox.stuart.test(x, alternative="left.sided")$p.value
  test2 <- cox.stuart.test(x, alternative="right.sided")$p.value
  if(test1 < 0.05){
    return("IMPROVING")
  }else if(test2 < 0.05){
    return("WORSING")
  }else{
    return("NO TREND")
  }
}
# cria função que retorna p-value de análise de tendência para séries temporais usando o teste de Cox Stuart
getTrendAnalysisPvalue = function(x){
  test1 <- cox.stuart.test(x, alternative="left.sided")$p.value
  test2 <- cox.stuart.test(x, alternative="right.sided")$p.value
  if(test1 < 0.05){
    return(test1)
  }else if(test2 < 0.05){
    return(test2)
  }else{
    ifelse(test1 < test2, test1, test2)
  }
}

summary_GINI_AIS=cbind(groupedResults_GINI_AIS[,c(1:2, 4)], v0=round(groupedResults_GINI_AIS[,5], digits = 4), v20=round(groupedResults_GINI_AIS[,25], digits = 4), trend=groupedResults_GINI_AIS[,27], pvalue=round(groupedResults_GINI_AIS[,28], digits = 4))
summary_GINI_ADS=cbind(groupedResults_GINI_ADS[,c(1:2, 4)], v0=round(groupedResults_GINI_ADS[,5], digits = 4), v20=round(groupedResults_GINI_ADS[,25], digits = 4), trend=groupedResults_GINI_ADS[,27], pvalue=round(groupedResults_GINI_ADS[,28], digits = 4))
summary_ADCS=cbind(groupedResults_ADCS[,c(1:2, 4)], v0=round(groupedResults_ADCS[,5], digits = 4), v20=round(groupedResults_ADCS[,25], digits = 4), trend=groupedResults_ADCS[,27], pvalue=round(groupedResults_ADCS[,28], digits = 4))
summary_SCF=cbind(groupedResults_SCF[,c(1:2, 4)], v0=round(groupedResults_SCF[,5], digits = 4), v20=round(groupedResults_SCF[,25], digits = 4), trend=groupedResults_SCF[,27], pvalue=round(groupedResults_SCF[,28], digits = 4))


# Function to calculate the chi-square and cramersV statistics for any two columns
calculateChiSqAndCramersV = function(title, evolutionColum, trendColumn, testsSegmentResults){
  CONTINGENCY_TABLE=table(as.vector(evolutionColum), as.vector(trendColumn))
  print(CONTINGENCY_TABLE)
  CHISQ_RESULT=chisq.test(evolutionColum, trendColumn)
  CHISQ_MATRIX=matrix(CHISQ_RESULT$observed[1:6], nrow=2, byrow = FALSE)
  colnames(CHISQ_MATRIX) <- c("Improving","No Trend","Eroding")
  rownames(CHISQ_MATRIX) <- c("Improve","Erosion")
  CHISQ_MATRIX = as.table(CHISQ_MATRIX)
  cramersV_RESULT=cramersV(CHISQ_MATRIX)
  print(cramersV_RESULT)
  
  # include results in the total's table
  testsSegmentResults=rbind(testsSegmentResults, data.frame(SCENARIO=title, CHISQ_VALUE=CHISQ_RESULT$statistic, CHISQ_PVALUE=CHISQ_RESULT$p.value, CRAMERSV_VALUE=cramersV_RESULT[1]))
  
  # export test output
  cat( paste(title, " - Contingency Table\n"), file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  capture.output(CONTINGENCY_TABLE, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  cat(paste(title, " - Chi-square Test \n"), file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  capture.output(CHISQ_RESULT, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  cat(paste(title, " - CramersV Test\n"), file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  capture.output(cramersV_RESULT, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  cat("\n\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
  
  return(testsSegmentResults)
}

# create dataframes to store the summarized results of the statistics tests
TESTS_TOTAL_RESULTS=data.frame(SCENARIO=factor(), CHISQ_VALUE=numeric(), CHISQ_PVALUE=numeric(), CRAMERSV_VALUE=numeric(), row.names = NULL)
TESTS_SEGMENTED_RESULTS=data.frame(SCENARIO=factor(), CHISQ_VALUE=numeric(), CHISQ_PVALUE=numeric(), CRAMERSV_VALUE=numeric(), row.names = NULL)

# Gini ADS (DD)
results_GINI_ADS$TREND=NA
#results_GINI_ADS$PVALUE=NA
for(i in 1:dim(results_GINI_ADS)[1]){
  results_GINI_ADS$TREND[i]=getTrendAnalysis(ts(results_GINI_ADS[i,7:27]))
  #results_GINI_ADS$PVALUE[i]=getTrendAnalysisPvalue(ts(results_GINI_ADS[i,5:25]))
}
results_GINI_ADS$TREND=as.factor(results_GINI_ADS$TREND)
GINI_ADS_TABLE=table(results_GINI_ADS$EVOLUÇÃO, results_GINI_ADS$TREND)
print(GINI_ADS_TABLE)
chisq_DD = chisq.test(results_GINI_ADS$EVOLUÇÃO, results_GINI_ADS$TREND)
print(chisq_DD)

contDD=matrix(chisq_DD$observed[1:6],nrow=2,byrow=FALSE)
colnames(contDD) <- c("Improving","No Trend","Eroding")
rownames(contDD) <- c("Improve","Erosion")
contDD=as.table(contDD)
cramersV_DD=cramersV(contDD)
print(cramersV_DD)

# include results in the total's table
TESTS_TOTAL_RESULTS=rbind(TESTS_TOTAL_RESULTS, data.frame(SCENARIO="GINI ADS (DD) - TOTAL", CHISQ_VALUE=chisq_DD$statistic, CHISQ_PVALUE=chisq_DD$p.value, CRAMERSV_VALUE=cramersV_DD[1]))

# export test output
cat("Gini ADS (DD) - Summary Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(summary_GINI_ADS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("Gini ADS (DD) - Contingency Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(GINI_ADS_TABLE, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Chi-square Test for Gini ADS (DD) \n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(chisq_DD, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Gini ADS (DD) - CramersV Test\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(cramersV_DD, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# Segmented statistic tests for scenarios
TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - RANDOM-SMALL', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='SMALL'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - RANDOM-MEDIUM', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='MEDIUM'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - RANDOM-LARGE', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='BIG'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='RANDOM_GRAPH' & results_GINI_ADS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - BA-SMALL', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='SMALL'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - BA-MEDIUM', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='MEDIUM'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini ADS (DD) - BA-LARGE', results_GINI_ADS$EVOLUÇÃO[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='BIG'], results_GINI_ADS$TREND[results_GINI_ADS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_ADS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)

cat("******************************************************\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# Gini AIS (RD)
results_GINI_AIS$TREND=NA
#results_GINI_ADS$PVALUE=NA
for(i in 1:dim(results_GINI_AIS)[1]){
  results_GINI_AIS$TREND[i]=getTrendAnalysis(ts(results_GINI_AIS[i,7:27]))
  #results_GINI_ADS$PVALUE[i]=getTrendAnalysisPvalue(ts(results_GINI_ADS[i,5:25]))
}
results_GINI_AIS$TREND=as.factor(results_GINI_AIS$TREND)
GINI_AIS_TABLE=table(results_GINI_AIS$EVOLUÇÃO, results_GINI_AIS$TREND)
print(GINI_AIS_TABLE)
chisq_RD = chisq.test(results_GINI_AIS$EVOLUÇÃO, results_GINI_AIS$TREND)
print(chisq_RD)

contRD=matrix(chisq_RD$observed[1:6],nrow=2,byrow=FALSE)
colnames(contRD) <- c("Improving","No Trend","Eroding")
rownames(contRD) <- c("Improve","Erosion")
contRD = as.table(contRD)
cramersV_RD=cramersV(contRD)

# include results in the total's table
TESTS_TOTAL_RESULTS=rbind(TESTS_TOTAL_RESULTS, data.frame(SCENARIO="GINI AIS (RD) - TOTAL", CHISQ_VALUE=chisq_RD$statistic, CHISQ_PVALUE=chisq_RD$p.value, CRAMERSV_VALUE=cramersV_RD[1]))

# export test output
cat("Gini AIS (RD) - Summary Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(summary_GINI_AIS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("Gini AIS (RD) - Contingency Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(GINI_AIS_TABLE, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Chi-square Test for Gini AIS (RD) \n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(chisq_RD, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Gini AIS (RD) - CramersV Test\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(cramersV_RD, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# Segmented statistic tests for scenarios
TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - RANDOM-SMALL', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='SMALL'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - RANDOM-MEDIUM', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='MEDIUM'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - RANDOM-LARGE', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='BIG'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='RANDOM_GRAPH' & results_GINI_AIS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - BA-SMALL', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='SMALL'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - BA-MEDIUM', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='MEDIUM'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('Gini AIS (RD) - BA-LARGE', results_GINI_AIS$EVOLUÇÃO[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='BIG'], results_GINI_AIS$TREND[results_GINI_AIS$TIPO=='BARABASI_ALBERT_GRAPH' & results_GINI_AIS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)

cat("******************************************************\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# SCF
results_SCF$TREND=NA
#results_GINI_ADS$PVALUE=NA
for(i in 1:dim(results_SCF)[1]){
  results_SCF$TREND[i]=getTrendAnalysis(ts(results_SCF[i,7:27]))
  #results_GINI_ADS$PVALUE[i]=getTrendAnalysisPvalue(ts(results_GINI_ADS[i,5:25]))
}
results_SCF$TREND=as.factor(results_SCF$TREND)
SCF_TABLE=table(results_SCF$EVOLUÇÃO, results_SCF$TREND)
print(SCF_TABLE)
#chisq.test(results_SCF$EVOLUÇÃO, results_SCF$TREND, simulate.p.value = TRUE)
chisq_SCF = chisq.test(results_SCF$EVOLUÇÃO, results_SCF$TREND)
print(chisq_SCF)

contSCF=matrix(chisq_SCF$observed[1:6],nrow=2,byrow=FALSE)
colnames(contSCF) <- c("Improving","No Trend", "Eroding")
rownames(contSCF) <- c("Improve","Erosion")
contSCF=as.table(contSCF)
cramersV_SCF=cramersV(contSCF)

# include results in the total's table
TESTS_TOTAL_RESULTS=rbind(TESTS_TOTAL_RESULTS, data.frame(SCENARIO="SCF - TOTAL", CHISQ_VALUE=chisq_SCF$statistic, CHISQ_PVALUE=chisq_SCF$p.value, CRAMERSV_VALUE=cramersV_SCF[1]))

# export test output
cat("SCF - Summary Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(summary_SCF, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("SCF - Contingency Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(SCF_TABLE, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Chi-square Test for SCF \n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(chisq_SCF, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("SCF - CramersV Test\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(cramersV_SCF, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# Segmented statistic tests for scenarios
# calculateChiSqAndCramersV('SCF - RANDOM-SMALL', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='SMALL'], results_SCF$TREND[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='SMALL'])
# 
# calculateChiSqAndCramersV('SCF - RANDOM-MEDIUM', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='MEDIUM'], results_SCF$TREND[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='MEDIUM'])
# 
# calculateChiSqAndCramersV('SCF - RANDOM-LARGE', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='BIG'], results_SCF$TREND[results_SCF$TIPO=='RANDOM_GRAPH' & results_SCF$TAMANHO=='BIG'])
# 
# calculateChiSqAndCramersV('SCF - BA-SMALL', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='SMALL'], results_SCF$TREND[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='SMALL'])
# 
# calculateChiSqAndCramersV('SCF - BA-MEDIUM', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='MEDIUM'], results_SCF$TREND[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='MEDIUM'])
# 
# calculateChiSqAndCramersV('SCF - BA-LARGE', results_SCF$EVOLUÇÃO[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='BIG'], results_SCF$TREND[results_SCF$TIPO=='BARABASI_ALBERT_GRAPH' & results_SCF$TAMANHO=='BIG'])

cat("******************************************************\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# ADCS
results_ADCS$TREND=NA
#results_GINI_ADS$PVALUE=NA
for(i in 1:dim(results_ADCS)[1]){
  results_ADCS$TREND[i]=getTrendAnalysis(ts(results_ADCS[i,7:27]))
  #results_GINI_ADS$PVALUE[i]=getTrendAnalysisPvalue(ts(results_GINI_ADS[i,5:25]))
}
results_ADCS$TREND=as.factor(results_ADCS$TREND)
ADCS_TABLE=table(results_ADCS$EVOLUÇÃO, results_ADCS$TREND)
print(ADCS_TABLE)
chisq_ADCS = chisq.test(results_ADCS$EVOLUÇÃO, results_ADCS$TREND)
print(chisq_ADCS)

contADCS=matrix(chisq_ADCS$observed[1:6],nrow=2,byrow=FALSE)
colnames(contADCS) <- c("Improving","No Trend","Eroding")
rownames(contADCS) <- c("Improve","Erosion")
contADCS=as.table(contADCS)
cramersV_ADCS=cramersV(contADCS)

# include results in the total's table
TESTS_TOTAL_RESULTS=rbind(TESTS_TOTAL_RESULTS, data.frame(SCENARIO="ADCS - TOTAL", CHISQ_VALUE=chisq_ADCS$statistic, CHISQ_PVALUE=chisq_ADCS$p.value, CRAMERSV_VALUE=cramersV_ADCS[1]))

# export test output
cat("ADCS - Summary Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(summary_ADCS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("ADCS - Contingency Table\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(ADCS_TABLE, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("Chi-square Test for ADCS \n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(chisq_ADCS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("ADCS - CramersV Test\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(cramersV_ADCS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

# Segmented statistic tests for scenarios
TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - RANDOM-SMALL', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='SMALL'], results_ADCS$TREND[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - RANDOM-MEDIUM', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='MEDIUM'], results_ADCS$TREND[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - RANDOM-LARGE', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='BIG'], results_ADCS$TREND[results_ADCS$TIPO=='RANDOM_GRAPH' & results_ADCS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - BA-SMALL', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='SMALL'], results_ADCS$TREND[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='SMALL'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - BA-MEDIUM', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='MEDIUM'], results_ADCS$TREND[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='MEDIUM'], TESTS_SEGMENTED_RESULTS)

TESTS_SEGMENTED_RESULTS=calculateChiSqAndCramersV('ADCS - BA-LARGE', results_ADCS$EVOLUÇÃO[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='BIG'], results_ADCS$TREND[results_ADCS$TIPO=='BARABASI_ALBERT_GRAPH' & results_ADCS$TAMANHO=='BIG'], TESTS_SEGMENTED_RESULTS)


cat("******************************************************\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("TESTS_TOTAL_RESULTS\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(TESTS_TOTAL_RESULTS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
cat("TESTS_SEGMENTED_RESULTS\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)
capture.output(TESTS_SEGMENTED_RESULTS, file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

cat("\n\n\n", file = "/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/tests.txt", append = TRUE)

#dev.off()

