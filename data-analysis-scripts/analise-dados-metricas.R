  # requires  package;
  # 1) tidyverse
  # 2) randtests
  library(tidyverse)
  library(randtests)
  # Lê os dados das métricas SCF, ADCS, Gini-ADS e Gini-AIS para todas as aplicações do experimento
  results=read.csv("/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/gini-results-summary.csv")
  results
  summary(results)
  
  #pdf("/home/daniel/Google Drive/note-17/Documentos/mestrado-2018/resultados-metricas.pdf")
  
  # Separa em dataframes diferentes para cada métrica
  results_GINI_ADS=results[, c(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27)]
  results_GINI_AIS=results[, c(1,2,3,4,5,6,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48)]
  results_SCF=results[, c(1,2,3,4,5,6,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69)]
  results_ADCS=results[, c(1,2,3,4,5,6,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90)]
  
  # cria função para cálculo de valor de nova coluna para os dataframes
  getPerformance = function(x, y){
    ifelse(x > y, "MELHOROU", "PIOROU")
  }
  
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
  
  
  # modifica os dataframes incluindo nova coluna PERFORMANCE
  # results_GINI_ADS = transform(results_GINI_ADS, PERFORMANCE=getPerformance(results_GINI_ADS$GINI.ADS.V0, results_GINI_ADS$GINI.ADS.V20))
  # barplot(table(results_GINI_ADS$PERFORMANCE, results_GINI_ADS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índíce Gini para métrica ADS")
  # 
  # results_GINI_AIS=transform(results_GINI_AIS, PERFORMANCE=getPerformance(results_GINI_AIS$GINI.AIS.V0, results_GINI_AIS$GINI.AIS.V20))
  # barplot(table(results_GINI_AIS$PERFORMANCE, results_GINI_AIS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índíce Gini para métrica AIS")
  # 
  # results_SCF=transform(results_SCF, PERFORMANCE=getPerformance(results_SCF$SCF.V0, results_SCF$SCF.V20))
  # barplot(table(results_SCF$PERFORMANCE, results_SCF$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Métrica SCF (densidade do grafo)")
  # 
  # results_ADCS=transform(results_ADCS, PERFORMANCE=getPerformance(results_ADCS$ADCS.V0, results_ADCS$ADCS.V20))
  # barplot(table(results_ADCS$PERFORMANCE, results_ADCS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Métrica ADCS (ADS médio)")
  # 
  # Lê os dados das métricas SCF, ADCS, Gini-ADS e Gini-AIS para os agrupamentos criados para os tratamentos do experimento
  groupedResults = read.csv("/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/gini-grouped-results.csv")
  
  #Separa em dataframes diferentes para cada métrica
  groupedResults_GINI_ADS = groupedResults[,c(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25)]
  groupedResults_GINI_AIS = groupedResults[,c(1,2,3,4,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46)]
  groupedResults_SCF = groupedResults[,c(1,2,3,4,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67)]
  groupedResults_ADCS = groupedResults[,c(1,2,3,4,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88)]
  
  #modifica os dataframes incluindo nova coluna performance-
  groupedResults_GINI_ADS=transform(groupedResults_GINI_ADS, PERFORMANCE=getPerformance(groupedResults_GINI_ADS$GINI.ADS.V0, groupedResults_GINI_ADS$GINI.ADS.V20))
  groupedResults_GINI_ADS$TREND=NA
  groupedResults_GINI_ADS$PVALUE=NA
  for(i in 1:dim(groupedResults_GINI_ADS)[1]){
    groupedResults_GINI_ADS$TREND[i]=getTrendAnalysis(ts(groupedResults_GINI_ADS[i,5:25]))
    groupedResults_GINI_ADS$PVALUE[i]=getTrendAnalysisPvalue(ts(groupedResults_GINI_ADS[i,5:25]))
  }
  #barplot(table(groupedResults_GINI_ADS$PERFORMANCE, groupedResults_GINI_ADS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índice Gini ADS médio")
  barplot(table(groupedResults_GINI_ADS$TREND, groupedResults_GINI_ADS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índice Gini ADS médio")
  
  groupedResults_GINI_AIS=transform(groupedResults_GINI_AIS, PERFORMANCE=getPerformance(groupedResults_GINI_AIS$GINI.AIS.V0, groupedResults_GINI_AIS$GINI.AIS.V20))
  groupedResults_GINI_AIS$TREND=NA
  groupedResults_GINI_AIS$PVALUE=NA
  for(i in 1:dim(groupedResults_GINI_AIS)[1]){
    groupedResults_GINI_AIS$TREND[i]=getTrendAnalysis(ts(groupedResults_GINI_AIS[i,5:25]))
    groupedResults_GINI_AIS$PVALUE[i]=getTrendAnalysisPvalue(ts(groupedResults_GINI_AIS[i,5:25]))
  }
  #barplot(table(groupedResults_GINI_AIS$PERFORMANCE, groupedResults_GINI_AIS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índice Gini AIS médio")
  barplot(table(groupedResults_GINI_AIS$TREND, groupedResults_GINI_AIS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="Índice Gini AIS médio")
  
  groupedResults_SCF=transform(groupedResults_SCF, PERFORMANCE=getPerformance(groupedResults_SCF$SCF.V0, groupedResults_SCF$SCF.V20))
  groupedResults_SCF$TREND=NA
  groupedResults_SCF$PVALUE=NA
  for(i in 1:dim(groupedResults_SCF)[1]){
    groupedResults_SCF$TREND[i]=getTrendAnalysis(ts(groupedResults_SCF[i,5:25]))
    groupedResults_SCF$PVALUE[i]=getTrendAnalysisPvalue(ts(groupedResults_SCF[i,5:25]))
  }
  #barplot(table(groupedResults_SCF$PERFORMANCE, groupedResults_SCF$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="SCF médio")
  barplot(table(groupedResults_SCF$TREND, groupedResults_SCF$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="SCF médio")
  
  groupedResults_ADCS=transform(groupedResults_ADCS, PERFORMANCE=getPerformance(groupedResults_ADCS$ADCS.V0, groupedResults_ADCS$ADCS.V20))
  groupedResults_ADCS$TREND=NA
  groupedResults_ADCS$PVALUE=NA
  for(i in 1:dim(groupedResults_ADCS)[1]){
    groupedResults_ADCS$TREND[i]=getTrendAnalysis(ts(groupedResults_ADCS[i,5:25]))
    groupedResults_ADCS$PVALUE[i]=getTrendAnalysisPvalue(ts(groupedResults_ADCS[i,5:25]))
  }
  #barplot(table(groupedResults_ADCS$PERFORMANCE, groupedResults_ADCS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="ADCS médio")
  barplot(table(groupedResults_ADCS$TREND, groupedResults_ADCS$EVOLUÇÃO), legend=T, xlab = "CENÁRIO DE EVOLUÇÃO", main="ADCS médio")
  
  
  # Transforma coluna TIPO em factor
  groupedResults_GINI_ADS$TIPO=as.factor(groupedResults_GINI_ADS$TIPO)
  groupedResults_GINI_AIS$TIPO=as.factor(groupedResults_GINI_AIS$TIPO)
  groupedResults_SCF$TIPO=as.factor(groupedResults_SCF$TIPO)
  groupedResults_ADCS$TIPO=as.factor(groupedResults_ADCS$TIPO)
  # Alteração de nomes de níveis para legendas de gráficos ficarem menores
  levels(groupedResults_GINI_ADS$TIPO)[levels(groupedResults_GINI_ADS$TIPO)=="RANDOM_GRAPH"] = "RD_G"
  levels(groupedResults_GINI_ADS$TIPO)[levels(groupedResults_GINI_ADS$TIPO)=="BARABASI_ALBERT_GRAPH"] = "BA_G"
  levels(groupedResults_GINI_AIS$TIPO)[levels(groupedResults_GINI_AIS$TIPO)=="RANDOM_GRAPH"] = "RD_G"
  levels(groupedResults_GINI_AIS$TIPO)[levels(groupedResults_GINI_AIS$TIPO)=="BARABASI_ALBERT_GRAPH"] = "BA_G"
  levels(groupedResults_SCF$TIPO)[levels(groupedResults_SCF$TIPO)=="RANDOM_GRAPH"] = "RD_G"
  levels(groupedResults_SCF$TIPO)[levels(groupedResults_SCF$TIPO)=="BARABASI_ALBERT_GRAPH"] = "BA_G"
  levels(groupedResults_ADCS$TIPO)[levels(groupedResults_ADCS$TIPO)=="RANDOM_GRAPH"] = "RD_G"
  levels(groupedResults_ADCS$TIPO)[levels(groupedResults_ADCS$TIPO)=="BARABASI_ALBERT_GRAPH"] = "BA_G"
  
  # Criação de matrizes do tipo de dados time series (ts) 
  ts_groupedResults_GINI_ADS=ts(t(groupedResults_GINI_ADS[,5:25]), start=0, names=paste(groupedResults_GINI_ADS$TIPO, groupedResults_GINI_ADS$TAMANHO, groupedResults_GINI_ADS$EVOLUÇÃO, sep="-" ))
  ts_groupedResults_GINI_AIS=ts(t(groupedResults_GINI_AIS[,5:25]), start=0, names=paste(groupedResults_GINI_AIS$TIPO, groupedResults_GINI_AIS$TAMANHO, groupedResults_GINI_AIS$EVOLUÇÃO, sep="-" ))
  ts_groupedResults_SCF=ts(t(groupedResults_SCF[,5:25]), start=0, names=paste(groupedResults_SCF$TIPO, groupedResults_SCF$TAMANHO, groupedResults_SCF$EVOLUÇÃO, sep="-" ))
  ts_groupedResults_ADCS=ts(t(groupedResults_ADCS[,5:25]), start=0, names=paste(groupedResults_ADCS$TIPO, groupedResults_ADCS$TAMANHO, groupedResults_ADCS$EVOLUÇÃO, sep="-" ))
  
  # Gerar gráfico para séries de dados Gini-ADS
  # Gera paleta de cores distintivas
  col_vector<-c('#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4', '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1', '#000075', '#808080', '#ffffff', '#000000')
  # Dimensiona área gráfica do R, para ter espaço à direita para a legenda do gráfico
  par(mar=c(4,4,4,12))
  
  # Plota o gráfico das séries de dados Gini-ADS
  ts.plot(ts_groupedResults_GINI_ADS, gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector, type="o"), main="Gini-ADS Evolution by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_GINI_ADS), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados Gini-AIS
  ts.plot(ts_groupedResults_GINI_AIS, gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector, type="o"), main="Gini-AIS Evolution by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_GINI_AIS), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados SCF
  ts.plot(ts_groupedResults_SCF, gpars=list(xlab="Release", ylab="SCF", col=col_vector, type="o"), main="SCF Evolution by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_SCF), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados ADCS
  ts.plot(ts_groupedResults_ADCS, gpars=list(xlab="Release", ylab="ADCS", col=col_vector, type="o"), main="ADCS Evolution by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_ADCS), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Incluir colunas para soma cumulativa da diferença das métricas
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,1])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,2])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,3])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,4])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,5])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_RD_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,6])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,7])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,8])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,9])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,10])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,11])), cumsum))))
  ts_groupedResults_GINI_ADS=transform(ts_groupedResults_GINI_ADS, CUMSUM_BA_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_ADS[,12])), cumsum))))
  
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,1])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,2])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,3])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,4])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,5])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_RD_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,6])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,7])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,8])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,9])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,10])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,11])), cumsum))))
  ts_groupedResults_GINI_AIS=transform(ts_groupedResults_GINI_AIS, CUMSUM_BA_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_GINI_AIS[,12])), cumsum))))
  
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,1])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,2])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,3])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,4])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,5])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_RD_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,6])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,7])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,8])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,9])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,10])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,11])), cumsum))))
  ts_groupedResults_SCF=transform(ts_groupedResults_SCF, CUMSUM_BA_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_SCF[,12])), cumsum))))
  
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,1])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,2])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,3])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,4])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,5])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_RD_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,6])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.SMALL.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,7])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.SMALL.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,8])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.MEDIUM.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,9])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.MEDIUM.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,10])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.BIG.IMPROVE=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,11])), cumsum))))
  ts_groupedResults_ADCS=transform(ts_groupedResults_ADCS, CUMSUM_BA_G.BIG.WORSEN=c(0,ts(sapply(as.data.frame(diff(ts_groupedResults_ADCS[,12])), cumsum))))
  
  # Gerar gráficos para as diferenças acumuladas das métricas
  par(mar=c(4,4,4,12))
  
  # Plota o gráfico das séries de dados de diferenças cumulativas para a métrica Gini-ADS
  ts.plot(ts_groupedResults_GINI_ADS[,13:24], gpars=list(xlab="Release", ylab="Cumulative Diff Gini-ADS", col=col_vector, type="o"), main="Cumulative Diff Gini-ADS by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_GINI_ADS[,1:12]), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados de diferenças cumulativas para a métrica Gini-AIS
  ts.plot(ts_groupedResults_GINI_AIS[,13:24], gpars=list(xlab="Release", ylab="Cumulative Diff Gini-AIS", col=col_vector, type="o"), main="Cumulative Diff Gini-AIS by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_GINI_AIS[,1:12]), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados de diferenças cumulativas para a métrica SCF
  ts.plot(ts_groupedResults_SCF[,13:24], gpars=list(xlab="Release", ylab="Cumulative Diff SCF", col=col_vector, type="o"), main="Cumulative Diff SCF by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_SCF[,1:12]), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados de diferenças cumulativas para a métrica ADCS
  ts.plot(ts_groupedResults_ADCS[,13:24], gpars=list(xlab="Release", ylab="Cumulative Diff ADCS", col=col_vector, type="o"), main="Cumulative Diff ADCS by Treatment")
  # Adiciona legenda ao graáfico
  legend("topleft", colnames(ts_groupedResults_ADCS[,1:12]), col=col_vector, lty=1, cex=.65, inset = c(1,0), xpd = TRUE, bty = "n")
  
  # Plota o gráfico das séries de dados Gini-ADS divididos pelos níveis diferentes de cada fator do experimento
  par(mar=c(5,4,4,2)+0.1)
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_GINI_ADS[starts_with(match = "RD", vars=colnames(ts_groupedResults_GINI_ADS))], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="o"), main="Gini-ADS Evolution for RD", ylim=c(0.2,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[starts_with(match = "RD", vars=colnames(ts_groupedResults_GINI_ADS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[starts_with(match = "BA", vars=colnames(ts_groupedResults_GINI_ADS))], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[7:13], type="o"), main="Gini-ADS Evolution for BA", ylim=c(0.2,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[starts_with(match = "BA", vars=colnames(ts_groupedResults_GINI_ADS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_ADS[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_GINI_ADS))], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-ADS for RD", ylim=c(-0.04,0.04))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_GINI_ADS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_GINI_ADS))], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[7:13], type="s"), main="Cumulative Diff Gini-ADS for BA", ylim=c(-0.04,0.04))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_GINI_ADS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  
  par(mfrow=c(1,3))
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_ADS))][1:4], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="o"), main="Gini-ADS Evolution for SMALL APPS", ylim=c(0.22,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_ADS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_ADS))][1:4], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="o"), main="Gini-ADS Evolution for MEDIUM APPS", ylim=c(0.22,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_ADS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_ADS))][1:4], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="o"), main="Gini-ADS Evolution for BIG APPS", ylim=c(0.22,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_ADS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_ADS))][5:8], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="s"), main="Cumulative Diff Gini-ADS for SMALL APPS", ylim=c(-0.03,0.05))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_ADS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_ADS))][5:8], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="s"), main="Cumulative Diff Gini-ADS for MEDIUM APPS", ylim=c(-0.03,0.05))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_ADS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_ADS))][5:8], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:4], type="s"), main="Cumulative Diff Gini-ADS for BIG APPS", ylim=c(-0.03,0.05))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_ADS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_ADS))][1:6], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="o"), main="Gini-ADS Evolution for IMPROVED APPS", ylim=c(0.22,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_ADS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_ADS))][1:6], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="o"), main="Gini-ADS Evolution for WORSEN APPS", ylim=c(0.22,0.4))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_ADS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_ADS))][7:12], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-ADS for IMPROVED APPS", ylim=c(-0.04,0.05))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_ADS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_ADS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_ADS))][7:12], gpars=list(xlab="Release", ylab="Gini-ADS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-ADS for WORSEN APPS", ylim=c(-0.04,0.05))
  legend("bottomright", colnames(ts_groupedResults_GINI_ADS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_ADS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Plota o gráfico das séries de dados Gini-AIS divididos pelos níveis diferentes de cada fator do experimento
  par(mar=c(5,4,4,2)+0.1)
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_GINI_AIS[starts_with(match = "RD", vars=colnames(ts_groupedResults_GINI_AIS))], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="o"), main="Gini-AIS Evolution for RD", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[starts_with(match = "RD", vars=colnames(ts_groupedResults_GINI_AIS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[starts_with(match = "BA", vars=colnames(ts_groupedResults_GINI_AIS))], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[7:13], type="o"), main="Gini-AIS Evolution for BA", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[starts_with(match = "BA", vars=colnames(ts_groupedResults_GINI_AIS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_AIS[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_GINI_AIS))], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-AIS for RD", ylim=c(-0.1,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[starts_with(match = "RD", vars=colnames(ts_groupedResults_GINI_AIS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_GINI_AIS))], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[7:13], type="s"), main="Cumulative Diff Gini-AIS for BA", ylim=c(-0.1,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[starts_with(match = "BA", vars=colnames(ts_groupedResults_GINI_AIS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  
  par(mfrow=c(1,3))
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_AIS))][1:4], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="o"), main="Gini-AIS Evolution for SMALL APPS", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_AIS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_AIS))][1:4], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="o"), main="Gini-AIS Evolution for MEDIUM APPS", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_AIS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_AIS))][1:4], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="o"), main="Gini-AIS Evolution for BIG APPS", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_AIS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_AIS))][5:8], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="s"), main="Cumulative Diff Gini-AIS for SMALL APPS", ylim=c(-0.05,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "SMALL", vars=colnames(ts_groupedResults_GINI_AIS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_AIS))][5:8], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="s"), main="Cumulative Diff Gini-AIS for MEDIUM APPS", ylim=c(-0.05,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_GINI_AIS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_AIS))][5:8], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:4], type="s"), main="Cumulative Gini-AIS for BIG APPS", ylim=c(-0.05,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "BIG", vars=colnames(ts_groupedResults_GINI_AIS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_AIS))][1:6], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="o"), main="Gini-AIS Evolution for IMPROVED APPS", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_AIS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_AIS))][1:6], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="o"), main="Gini-AIS Evolution for WORSEN APPS", ylim=c(0.4,0.75))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_AIS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_AIS))][7:12], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-AIS for IMPROVED APPS", ylim=c(-0.1,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_GINI_AIS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_GINI_AIS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_AIS))][7:12], gpars=list(xlab="Release", ylab="Gini-AIS", col=col_vector[1:6], type="s"), main="Cumulative Diff Gini-AIS for WORSEN APPS", ylim=c(-0.1,0.16))
  legend("bottomright", colnames(ts_groupedResults_GINI_AIS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_GINI_AIS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Plota o gráfico das séries de dados SCF divididos pelos níveis diferentes de cada fator do experimento
  par(mar=c(5,4,4,2)+0.1)
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_SCF[starts_with(match = "RD", vars=colnames(ts_groupedResults_SCF))], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="o"), main="SCF Evolution for RD", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[starts_with(match = "RD", vars=colnames(ts_groupedResults_SCF))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[starts_with(match = "BA", vars=colnames(ts_groupedResults_SCF))], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[7:13], type="o"), main="SCF Evolution for BA", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[starts_with(match = "BA", vars=colnames(ts_groupedResults_SCF))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_SCF[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_SCF))], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff SCF for RD", ylim=c(-0.15,0.05))
  legend("bottomright", colnames(ts_groupedResults_SCF[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_SCF))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_SCF))], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[7:13], type="s"), main="Cumulative Diff SCF for BA", ylim=c(-0.15,0.05))
  legend("bottomright", colnames(ts_groupedResults_SCF[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_SCF))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,3))
  ts.plot(ts_groupedResults_SCF[contains(match = "SMALL", vars=colnames(ts_groupedResults_SCF))][1:4], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="o"), main="SCF Evolution for SMALL APPS", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "SMALL", vars=colnames(ts_groupedResults_SCF))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_SCF))][1:4], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="o"), main="SCF Evolution for MEDIUM APPS", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_SCF))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "BIG", vars=colnames(ts_groupedResults_SCF))][1:4], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="o"), main="SCF Evolution for BIG APPS", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "BIG", vars=colnames(ts_groupedResults_SCF))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_SCF[contains(match = "SMALL", vars=colnames(ts_groupedResults_SCF))][5:8], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff SCF for SMALL APPS", ylim=c(-0.13,0.03))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "SMALL", vars=colnames(ts_groupedResults_SCF))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_SCF))][5:8], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff SCF for MEDIUM APPS", ylim=c(-0.13,0.03))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_SCF))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "BIG", vars=colnames(ts_groupedResults_SCF))][5:8], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff SCF for BIG APPS", ylim=c(-0.13,0.03))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "BIG", vars=colnames(ts_groupedResults_SCF))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_SCF[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_SCF))][1:6], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="o"), main="SCF Evolution for IMPROVED APPS", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_SCF))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "WORSEN", vars=colnames(ts_groupedResults_SCF))][1:6], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="o"), main="SCF Evolution for WORSEN APPS", ylim=c(-0.05,0.3))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "WORSEN", vars=colnames(ts_groupedResults_SCF))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_SCF[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_SCF))][7:12], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff SCF for IMPROVED APPS", ylim=c(-0.15,0.05))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_SCF))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_SCF[contains(match = "WORSEN", vars=colnames(ts_groupedResults_SCF))][7:12], gpars=list(xlab="Release", ylab="SCF Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff SCF for WORSEN APPS", ylim=c(-0.15,0.05))
  legend("bottomright", colnames(ts_groupedResults_SCF[contains(match = "WORSEN", vars=colnames(ts_groupedResults_SCF))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Plota o gráfico das séries de dados ADCS divididos pelos níveis diferentes de cada fator do experimento
  par(mar=c(5,4,4,2)+0.1)
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_ADCS[starts_with(match = "RD", vars=colnames(ts_groupedResults_ADCS))], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="o"), main="ADCS Evolution for RD", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[starts_with(match = "RD", vars=colnames(ts_groupedResults_ADCS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[starts_with(match = "BA", vars=colnames(ts_groupedResults_ADCS))], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[7:13], type="o"), main="ADCS Evolution for BA", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[starts_with(match = "BA", vars=colnames(ts_groupedResults_ADCS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_ADCS[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_ADCS))], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff ADCS for RD", ylim=c(-1.3,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[starts_with(match = "CUMSUM_RD", vars=colnames(ts_groupedResults_ADCS))]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_ADCS))], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[7:13], type="s"), main="Cumulative Diff ADCS for BA", ylim=c(-1.3,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[starts_with(match = "CUMSUM_BA", vars=colnames(ts_groupedResults_ADCS))]), col=col_vector[7:13], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,3))
  ts.plot(ts_groupedResults_ADCS[contains(match = "SMALL", vars=colnames(ts_groupedResults_ADCS))][1:4], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="o"), main="ADCS Evolution for SMALL APPS", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "SMALL", vars=colnames(ts_groupedResults_ADCS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_ADCS))][1:4], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="o"), main="ADCS Evolution for MEDIUM APPS", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_ADCS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "BIG", vars=colnames(ts_groupedResults_ADCS))][1:4], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="o"), main="ADCS Evolution for BIG APPS", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "BIG", vars=colnames(ts_groupedResults_ADCS))][1:4]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_ADCS[contains(match = "SMALL", vars=colnames(ts_groupedResults_ADCS))][5:8], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff ADCS for SMALL APPS", ylim=c(-1,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "SMALL", vars=colnames(ts_groupedResults_ADCS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_ADCS))][5:8], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff ADCS for MEDIUM APPS", ylim=c(-1,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "MEDIUM", vars=colnames(ts_groupedResults_ADCS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "BIG", vars=colnames(ts_groupedResults_ADCS))][5:8], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:4], type="s"), main="Cumulative Diff ADCS for BIG APPS", ylim=c(-1,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "BIG", vars=colnames(ts_groupedResults_ADCS))][5:8]), col=col_vector[1:4], lty=1, cex=.65, bty = "n")
  
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_ADCS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_ADCS))][1:6], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="o"), main="ADCS Evolution for IMPROVED APPS", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_ADCS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_ADCS))][1:6], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="o"), main="ADCS Evolution for WORSEN APPS", ylim=c(1,5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_ADCS))][1:6]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Para diferenças acumuladas
  ts.plot(ts_groupedResults_ADCS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_ADCS))][7:12], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff ADCS for IMPROVED APPS", ylim=c(-1,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "IMPROVE", vars=colnames(ts_groupedResults_ADCS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  ts.plot(ts_groupedResults_ADCS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_ADCS))][7:12], gpars=list(xlab="Release", ylab="ADCS Metric", col=col_vector[1:6], type="s"), main="Cumulative Diff ADCS for WORSEN APPS", ylim=c(-1,0.5))
  legend("bottomright", colnames(ts_groupedResults_ADCS[contains(match = "WORSEN", vars=colnames(ts_groupedResults_ADCS))][7:12]), col=col_vector[1:6], lty=1, cex=.65, bty = "n")
  
  # Graphics for Comparision between scenarios
  # Graphics for SMALL scenarios
  par(mfrow=c(2,4))
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  legend(bg="transparent", 1, max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  
  ts.plot(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN),max(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  legend(bg="transparent", 8, max(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN),max(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  
  ts.plot(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  legend(bg="transparent", 5, max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  
  ts.plot(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN),max(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  legend(bg="transparent", 0.5, 0.2, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN),max(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  
  ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  
  par(mfrow=c(2,4))
  #Graphics for MEDIUM scenarios
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  legend(bg="transparent", 1, max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  legend(bg="transparent", 1, max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  legend(bg="transparent", 1, max(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  
  ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  legend(bg="transparent", 0.5, 0.12, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  
  ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, 0), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  
  ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  legend(bg="transparent", 0.5, 0.11, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  
  ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  legend(bg="transparent", 10, 2.83, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  
  
  par(mfrow=c(2,4))
  #Graphics for LARGE scenarios
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN))), main="RANDOM-LARGE")
  legend(bg="transparent", 0.5, max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN))), main="RANDOM-LARGE")
  
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN))), main="RANDOM-LARGE")
  legend(bg="transparent", 0.5, max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN))), main="RANDOM-LARGE")
  
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  legend(bg="transparent", 8, 0.33, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  legend(bg="transparent", 0.5, max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  
  ts.plot(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN),max(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  legend(bg="transparent", 0.5, min(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, 10), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN),max(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  
  ts.plot(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN),max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN),max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  
  ts.plot(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN),max(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  legend(bg="transparent", 0.5, 0.055, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_SCF$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN),max(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  legend(bg="transparent", 5.5, 3.19, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  
  
  #***********************
  
  # Graphics for RANDOM SMALL scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # 
  # ts.plot(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN),max(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # legend(bg="transparent", 5.5, 0.18, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN),max(ts_groupedResults_SCF$RD_G.SMALL.IMPROVE, ts_groupedResults_SCF$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # 
  # ts.plot(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$RD_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$RD_G.SMALL.IMPROVE, ts_groupedResults_ADCS$RD_G.SMALL.WORSEN))), main="RANDOM-SMALL")
  # 
  # Graphics for RANDOM MEDIUM scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # legend(bg="transparent", 12, max(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, 0), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN))), main="RANDOM-MEDIUM")
  # 
  # Graphics for RANDOM BIG scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # 
  # ts.plot(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN),max(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # legend(bg="transparent", 12, max(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN),max(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, ts_groupedResults_SCF$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # 
  # ts.plot(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN),max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, 0), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$RD_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN),max(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, ts_groupedResults_ADCS$RD_G.BIG.WORSEN))), main="RANDOM-BIG")

  # Graphics for BARABASI-ALBERT SMALL scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # 
  # ts.plot(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN),max(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # legend(bg="transparent", 12, max(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN),max(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, ts_groupedResults_SCF$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # 
  # ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN),max(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, ts_groupedResults_ADCS$BA_G.SMALL.WORSEN))), main="BARABASI-SMALL")
  # 
  # Graphics for BARABASI-ALBERT MEDIUM scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # legend(bg="transparent", 1, 0.33, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # legend(bg="transparent", 12, max(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # 
  # ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # legend(bg="transparent", 10, 2.85, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN),max(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN))), main="BARABASI-MEDIUM")
  # 
  # Graphics for BARABASI-ALBERT BIG scenarios
  # par(mfrow=c(2,2))
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # legend(bg="transparent", 10, 0.33, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini ADS (DD)",  type="o", ylim=c(min(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # 
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # legend(bg="transparent", 10, 0.648, legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="Gini AIS (RD)",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # 
  # ts.plot(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN),max(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # legend(bg="transparent", 12, max(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_SCF$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="SCF",  type="o", ylim=c(min(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN),max(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, ts_groupedResults_SCF$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # 
  # ts.plot(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # legend(bg="transparent", 5.5, max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  # par(new=TRUE)
  # ts.plot(ts_groupedResults_ADCS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BARABASI-LARGE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="RD-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="RD-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="RD-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="RD-MEDIUM-IMPROVE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="RD-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="RD-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_SCF$RD_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="RD-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_ADCS$RD_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="RD-MEDIUM-WORSEN")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="RD-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="RD-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_SCF$RD_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="RD-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_ADCS$RD_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="RD-BIG-IMPROVE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$RD_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="RD-BIG-WORSEN")
  # ts.plot(ts_groupedResults_GINI_AIS$RD_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="RD-BIG-WORSEN")
  # ts.plot(ts_groupedResults_SCF$RD_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="RD-BIG-WORSEN")
  # ts.plot(ts_groupedResults_ADCS$RD_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="RD-BIG-WORSEN")
 
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.IMPROVE, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-SMALL-IMPROVE")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-SMALL-IMPROVE")
  # ts.plot(ts_groupedResults_SCF$BA_G.SMALL.IMPROVE, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-SMALL-IMPROVE")
  # ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-SMALL-IMPROVE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.SMALL.WORSEN, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-SMALL-WORSEN")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.SMALL.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-SMALL-WORSEN")
  # ts.plot(ts_groupedResults_SCF$BA_G.SMALL.WORSEN, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-SMALL-WORSEN")
  # ts.plot(ts_groupedResults_ADCS$BA_G.SMALL.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-SMALL-WORSEN")
  
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-MEDIUM-IMPROVE")
  # ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-MEDIUM-IMPROVE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_SCF$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-MEDIUM-WORSEN")
  # ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-MEDIUM-WORSEN")
  
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_SCF$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-BIG-IMPROVE")
  # ts.plot(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-BIG-IMPROVE")
  # 
  # ts.plot(ts_groupedResults_GINI_ADS$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="Gini ADS (DD)",  type="o"), main="BA-BIG-WORSEN")
  # ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o"), main="BA-BIG-WORSEN")
  # ts.plot(ts_groupedResults_SCF$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="SCF",  type="o"), main="BA-BIG-WORSEN")
  # ts.plot(ts_groupedResults_ADCS$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o"), main="BA-BIG-WORSEN")
  # 
  # Graphic to be exported to paper
  #ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o", ylim=c(2.74,3.05)), main="BA-MEDIUM-IMPROVE - ADCS METRIC")
  #ts.plot(ts_groupedResults_ADCS$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o", ylim=c(2.74,3.05)), main="BA-MEDIUM-WORSEN - ADCS METRIC")
  #ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.IMPROVE, gpars=list(xlab="Release", ylab="Gini AIS",  type="o", ylim=c(0.54, 0.63)), main="BA-MEDIUM-IMPROVE - GINI AIS")
  #ts.plot(ts_groupedResults_GINI_AIS$BA_G.MEDIUM.WORSEN, gpars=list(xlab="Release", ylab="Gini AIS",  type="o", ylim=c(0.54, 0.63)), main="BA-MEDIUM-WORSEN - GINI AIS")
  
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE))), main="BA-LARGE-IMPROVE - ADCS METRIC")
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.WORSEN), max(ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BA-LARGE-WORSEN - ADCS METRIC")
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, gpars=list(xlab="Release", ylab="RD",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE), max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE))), main="BA-LARGE-IMPROVE - RD METRIC")
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN, gpars=list(xlab="Release", ylab="RD",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN), max(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BA-LARGE-WORSEN - RD METRIC")
  
  # Join the previous graphics with the same metric
  par(cex=1)
  par(mar=c(5,4,2,4)+0.1)
  par(mfrow=c(1,2))
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN),max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BA-LARGE - ADCS METRIC")
  legend(3.5, max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN), legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, inset = .08)
  par(new=TRUE)
  ts.plot(ts_groupedResults_ADCS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="ADCS",  type="o", ylim=c(min(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN), max(ts_groupedResults_ADCS$BA_G.BIG.IMPROVE, ts_groupedResults_ADCS$BA_G.BIG.WORSEN))), main="BA-LARGE - ADCS METRIC")
  
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, gpars=list(col="blue", xlab="Release", ylab="RD",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN),max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BA-LARGE - RD METRIC")
  legend("bottomright", legend=c("Improve Evolution", "Erosion Evolution"), col=c("blue", "red"), lty=1:1, cex=0.8, box.lty=0, box.lwd = 0, box.col = 0)
  par(new=TRUE)
  ts.plot(ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN, gpars=list(col="red", xlab="Release", ylab="RD",  type="o", ylim=c(min(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN), max(ts_groupedResults_GINI_AIS$BA_G.BIG.IMPROVE, ts_groupedResults_GINI_AIS$BA_G.BIG.WORSEN))), main="BA-LARGE - RD METRIC")
  
  
  # Geração de tabelas com principais dados para colocar nos resultados de artigo
  cbind(groupedResults_GINI_AIS[,c(1:2, 4)], v0=round(groupedResults_GINI_AIS[,5], digits = 4), v20=round(groupedResults_GINI_AIS[,25], digits = 4), trend=groupedResults_GINI_AIS[,27], pvalue=round(groupedResults_GINI_AIS[,28], digits = 4))
  cbind(groupedResults_GINI_ADS[,c(1:2, 4)], v0=round(groupedResults_GINI_ADS[,5], digits = 4), v20=round(groupedResults_GINI_ADS[,25], digits = 4), trend=groupedResults_GINI_ADS[,27], pvalue=round(groupedResults_GINI_ADS[,28], digits = 4))
  cbind(groupedResults_ADCS[,c(1:2, 4)], v0=round(groupedResults_ADCS[,5], digits = 4), v20=round(groupedResults_ADCS[,25], digits = 4), trend=groupedResults_ADCS[,27], pvalue=round(groupedResults_ADCS[,28], digits = 4))
  cbind(groupedResults_SCF[,c(1:2, 4)], v0=round(groupedResults_SCF[,5], digits = 4), v20=round(groupedResults_SCF[,25], digits = 4), trend=groupedResults_SCF[,27], pvalue=round(groupedResults_SCF[,28], digits = 4))
  
  #dev.off()