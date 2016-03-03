package preti.spark.stock.run.recomendations;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import preti.spark.stock.model.Trade;
import preti.spark.stock.run.DonchianParametersOptimizationResult;

@SuppressWarnings("serial")
public class RecomendationsConfig implements Serializable {
	private final String DATE_FORMAT = "yyyy-MM-dd";

	private String stockHistoryFile;
	private List<DonchianParametersOptimizationResult> model;
	private double accountInitialPosition;
	private List<Trade> trades;
	private List<String> stockCodesToAnalyze;
	private Date recomendationDate;
	private String outputFile;

	public String getStockHistoryFile() {
		return stockHistoryFile;
	}

	public void setStockHistoryFile(String stockHistoryFile) {
		this.stockHistoryFile = stockHistoryFile;
	}

	public List<DonchianParametersOptimizationResult> getModel() {
		return model;
	}

	public void setModel(List<DonchianParametersOptimizationResult> model) {
		this.model = model;
	}

	public double getAccountInitialPosition() {
		return accountInitialPosition;
	}

	public void setAccountInitialPosition(double accountInitialPosition) {
		this.accountInitialPosition = accountInitialPosition;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	public List<String> getStockCodesToAnalyze() {
		return stockCodesToAnalyze;
	}

	public void setStockCodesToAnalyze(List<String> stockCodesToAnalyze) {
		this.stockCodesToAnalyze = stockCodesToAnalyze;
	}

	public Date getRecomendationDate() {
		return recomendationDate;
	}

	public void setRecomendationDate(String recomendationDate) {
		try {
			this.recomendationDate = new SimpleDateFormat(DATE_FORMAT).parse(recomendationDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

}
