package preti.spark.stock.run.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class ModelGenerationConfig implements Serializable {
	private final String DATE_FORMAT = "yyyy-MM-dd";

	private String stockHistoryFile;
	private Date start;
	private Date end;
	private int minDonchianEntrySize, maxDonchianEntrySize, minDonchianExitSize, maxDonchianExitSize;
	private List<String> stockCodesToAnalyze;
	private double accountInitialValue;
	private double riskRate;
	private String outputFile;

	public String getStockHistoryFile() {
		return stockHistoryFile;
	}

	public void setStockHistoryFile(String inputFile) {
		this.stockHistoryFile = inputFile;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(String start) {
		try {
			this.start = new SimpleDateFormat(DATE_FORMAT).parse(start);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(String end) {
		try {
			this.end = new SimpleDateFormat(DATE_FORMAT).parse(end);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public int getMinDonchianEntrySize() {
		return minDonchianEntrySize;
	}

	public void setMinDonchianEntrySize(int minDonchianEntrySize) {
		this.minDonchianEntrySize = minDonchianEntrySize;
	}

	public int getMaxDonchianEntrySize() {
		return maxDonchianEntrySize;
	}

	public void setMaxDonchianEntrySize(int maxDonchianEntrySize) {
		this.maxDonchianEntrySize = maxDonchianEntrySize;
	}

	public int getMinDonchianExitSize() {
		return minDonchianExitSize;
	}

	public void setMinDonchianExitSize(int minDonchianExitSize) {
		this.minDonchianExitSize = minDonchianExitSize;
	}

	public int getMaxDonchianExitSize() {
		return maxDonchianExitSize;
	}

	public void setMaxDonchianExitSize(int maxDonchianExitSize) {
		this.maxDonchianExitSize = maxDonchianExitSize;
	}

	public List<String> getStockCodesToAnalyze() {
		return stockCodesToAnalyze;
	}

	public void setStockCodesToAnalyze(List<String> stockCodesToAnalyze) {
		this.stockCodesToAnalyze = stockCodesToAnalyze;
		if (this.stockCodesToAnalyze != null)
			Collections.sort(this.stockCodesToAnalyze);
	}

	public double getAccountInitialValue() {
		return accountInitialValue;
	}

	public void setAccountInitialValue(double accountInitialValue) {
		this.accountInitialValue = accountInitialValue;
	}

	public double getRiskRate() {
		return riskRate;
	}

	public void setRiskRate(double riskRate) {
		this.riskRate = riskRate;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	

}
