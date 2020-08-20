  # Configurar os parâmetros para o cálculo estatístico do número de réplicas "ideal"
  # Número de réplicas inicial
  rep.number=data.frame(table(results$TIPO, results$TAMANHO, results$EVOLUÇÃO))[1,4]
  print(rep.number)
  # alpha = confidence interval. 90% => 0.1, 95% => 0.05
  alpha=0.05
  # gam ma = error relative objective. 10% => 0.01, 5% => 0.05
  gamma=0.05
  # Calcula o erro relativo "ajustado"
    relative_error_adjusted=gamma/(1+gamma)
  print(relative_error_adjusted)
  
  # Calcula o ponto crítico de uma variável aleatória para um desvio padrão
  critical_point=qt(1-(alpha/2), rep.number-1)
  print(critical_point)
  
  # Lê os dados das métricas SCF, ADCS, Gini-ADS e Gini-AIS para todas as aplicações do experimento
  results=read.csv("/home/daniel/Documentos/mestrado-2018/projeto/grafos-dependencia/gini-results-summary.csv")
      
  # Cria dataframe para armazenar todas as médias e outro para armazenar todas as variâncias
  mean_df=aggregate(x=results[,7:90], by=list(results$TIPO, results$TAMANHO, results$EVOLUÇÃO), mean)
  variance_df=aggregate(x=results[,7:90], by=list(results$TIPO, results$TAMANHO, results$EVOLUÇÃO), var)
  
  # Ordena os 2 dataframes para garantir que a média e a variância estejam sempre na mesma posição nos 2 dataframes
  mean_df=mean_df[order(mean_df$Group.1, mean_df$Group.2, mean_df$Group.3), ]
  variance_df=variance_df[order(variance_df$Group.1, variance_df$Group.2, variance_df$Group.3), ]
  
  # Função para calcular o erro relativo de cada um das releases para cada cenário
  calculate_relative_error = function(x, y){
    confidence_interval = critical_point * sqrt(y/rep.number)
    return (confidence_interval/abs(x))
  }
  
  # Junta as colunas relativas aos nomes dos cenários e dos valores dos erros relativos calculados
  relative_error_calculated= cbind(mean_df[,1:3], mapply(calculate_relative_error, mean_df[,4:87], variance_df[,4:87]))
  
  par(mfrow=c(1,1))
  # Plota um gráfico para visualmente sabermos se os erros relativos estão abaixo do erro relativo ajustado
  data_points=unlist(relative_error_calculated[,4:87])
  min(relative_error_calculated[,4:87])
  
  plot(data_points, ylim = c(min(relative_error_calculated[,4:87]), pmax(max(relative_error_calculated[,4:87]), relative_error_adjusted)))
  # Traça uma linha com a referência do erro relativo ajustado (objetivo)
  abline(h=relative_error_adjusted, col="red")
  
  
  
  
