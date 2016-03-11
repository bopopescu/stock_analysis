package preti.spark.stock;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class ConfigContext implements Serializable {
	private final String DATE_FORMAT = "yyyy-MM-dd";
	private String stockHistoryFile;
	private List<String> stockCodesToAnalyze;
	private int minDochianEntryValue, maxDonchianEntryValue, minDonchianExitValue, maxDonchianExitValue;
	private Date initialDate, finalDate;
	private double riskRate;
	private int trainingSizeInMonths, windowSizeInMonths;
	private double accountInitialValue;

	public String getStockHistoryFile() {
		return stockHistoryFile;
	}

	public void setStockHistoryFile(String stockHistoryFile) {
		this.stockHistoryFile = stockHistoryFile;
	}

	public List<String> getStockCodesToAnalyze() {
		return stockCodesToAnalyze;
	}

	public void setStockCodesToAnalyze(List<String> stockCodesToAnalyze) {
		this.stockCodesToAnalyze = stockCodesToAnalyze;
		if (this.stockCodesToAnalyze != null)
			Collections.sort(this.stockCodesToAnalyze);
	}

	public int getMinDochianEntryValue() {
		return minDochianEntryValue;
	}

	public void setMinDochianEntryValue(int minDochianEntryValue) {
		this.minDochianEntryValue = minDochianEntryValue;
	}

	public int getMaxDonchianEntryValue() {
		return maxDonchianEntryValue;
	}

	public void setMaxDonchianEntryValue(int maxDonchianEntryValue) {
		this.maxDonchianEntryValue = maxDonchianEntryValue;
	}

	public int getMinDonchianExitValue() {
		return minDonchianExitValue;
	}

	public void setMinDonchianExitValue(int minDonchianExitValue) {
		this.minDonchianExitValue = minDonchianExitValue;
	}

	public int getMaxDonchianExitValue() {
		return maxDonchianExitValue;
	}

	public void setMaxDonchianExitValue(int maxDonchianExitValue) {
		this.maxDonchianExitValue = maxDonchianExitValue;
	}

	public Date getInitialDate() {
		return initialDate;
	}
	
	public String getFormatedInitialDate() {
		return new SimpleDateFormat(DATE_FORMAT).format(initialDate);
	}

	public void setInitialDate(String initialDate) {
		try {
			this.initialDate = new SimpleDateFormat(DATE_FORMAT).parse(initialDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getFinalDate() {
		return finalDate;
	}
	
	public String getFormatedFinalDate() {
		return new SimpleDateFormat(DATE_FORMAT).format(finalDate);
	}

	public void setFinalDate(String finalDate) {
		try {
			this.finalDate = new SimpleDateFormat(DATE_FORMAT).parse(finalDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public double getRiskRate() {
		return riskRate;
	}

	public void setRiskRate(double riskRate) {
		this.riskRate = riskRate;
	}

	public int getTrainingSizeInMonths() {
		return trainingSizeInMonths;
	}

	public void setTrainingSizeInMonths(int trainingSizeInMonths) {
		this.trainingSizeInMonths = trainingSizeInMonths;
	}

	public int getWindowSizeInMonths() {
		return windowSizeInMonths;
	}

	public void setWindowSizeInMonths(int windowSizeInMonths) {
		this.windowSizeInMonths = windowSizeInMonths;
	}

	public double getAccountInitialValue() {
		return accountInitialValue;
	}

	public void setAccountInitialValue(double accountInitialValue) {
		this.accountInitialValue = accountInitialValue;
	}

	public List<Integer> getEntryDonchianSizes() {
		List<Integer> entrySizes = new ArrayList<>();
		for (int i = getMinDochianEntryValue(); i <= getMaxDonchianEntryValue(); i++) {
			entrySizes.add(i);
		}
		return entrySizes;
	}

	public List<Integer> getExitDonchianSizes() {
		List<Integer> exitSizes = new ArrayList<>();
		for (int i = getMinDonchianExitValue(); i <= getMaxDonchianExitValue(); i++) {
			exitSizes.add(i);
		}
		return exitSizes;
	}
}
