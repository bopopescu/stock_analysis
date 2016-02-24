library("rjson")

EXECUTIONS_DIR="/git_repos/github/stock_analysis/spark_stock_analysis/executions/stock_analysis_report"
executionsData = data.frame()

for(fileName in list.files(EXECUTIONS_DIR, full.names = TRUE, pattern = "*.json")){
  lineNumber = nrow(executionsData) + 1
  jsonFile = fromJSON(file=fileName)

  executionsData[lineNumber, "minDochianEntryValue"] = as.integer(jsonFile[["minDochianEntryValue"]])
  executionsData[lineNumber, "maxDonchianEntryValue"] = as.integer(jsonFile[["maxDonchianEntryValue"]])
  executionsData[lineNumber, "minDonchianExitValue"] = as.integer(jsonFile[["minDonchianExitValue"]])
  executionsData[lineNumber, "maxDonchianExitValue"] = as.integer(jsonFile[["maxDonchianExitValue"]])
  executionsData[lineNumber, "initialDate"] = jsonFile[["initialDate"]]
  executionsData[lineNumber, "finalDate"] = jsonFile[["finalDate"]]
  executionsData[lineNumber, "riskRate"] = as.numeric(jsonFile[["riskRate"]])
  executionsData[lineNumber, "trainingSizeInMonths"] = as.integer(jsonFile[["trainingSizeInMonths"]])
  executionsData[lineNumber, "windowSizeInMonths"] = as.integer(jsonFile[["windowSizeInMonths"]])
  executionsData[lineNumber, "initialBalance"] = as.numeric(jsonFile[["initialBalance"]])
  executionsData[lineNumber, "finalBalance"] = as.numeric(jsonFile[["finalBalance"]])
  executionsData[lineNumber, "profit"] = executionsData[lineNumber, "finalBalance"] - executionsData[lineNumber, "initialBalance"]
  executionsData[lineNumber, "profitRate"] = executionsData[lineNumber, "profit"] / executionsData[lineNumber, "initialBalance"]
}

plotData = executionsData[
            executionsData$minDochianEntryValue==10 & executionsData$maxDonchianEntryValue==20 & executionsData$minDonchianExitValue==2
            & executionsData$maxDonchianExitValue==10 & executionsData$riskRate==0.02 & executionsData$trainingSizeInMonths==1 & executionsData$windowSizeInMonths==1, ]
plotData = plotData[order(plotData$initialBalance), c("initialBalance", "profitRate")]
plot(plotData, type="b")

plotData = executionsData[
  executionsData$minDochianEntryValue==10 & executionsData$maxDonchianEntryValue==20 & executionsData$minDonchianExitValue==2
  & executionsData$maxDonchianExitValue==10 & executionsData$trainingSizeInMonths==1 & executionsData$windowSizeInMonths==1 & executionsData$initialBalance==50000, ]
plotData = plotData[order(plotData$riskRate), c("riskRate", "profitRate")]
plot(plotData, type="b")

plotData = executionsData[
  executionsData$minDochianEntryValue==10 & executionsData$maxDonchianEntryValue==20 & executionsData$minDonchianExitValue==2
  & executionsData$maxDonchianExitValue==10 & executionsData$windowSizeInMonths==1 & executionsData$initialBalance==10000 & executionsData$riskRate==0.01, ]
plotData = plotData[order(plotData$trainingSizeInMonths), c("trainingSizeInMonths", "profitRate")]
plot(plotData, type="b")

plotData = executionsData[
  executionsData$minDochianEntryValue==10 & executionsData$maxDonchianEntryValue==20 & executionsData$minDonchianExitValue==2
  & executionsData$maxDonchianExitValue==10 & executionsData$trainingSizeInMonths==1 & executionsData$initialBalance==10000 & executionsData$riskRate==0.01, ]
plotData = plotData[order(plotData$windowSizeInMonths), c("windowSizeInMonths", "profitRate")]
plot(plotData, type="b")