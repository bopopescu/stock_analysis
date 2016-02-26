package preti.spark.stock.run.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ModelGenerationResult implements Serializable {

	private String stock;
	private int entryDonchianSize, exitDonchianSize;
	private double riskRate;

	public ModelGenerationResult(String stock, int entryDonchianSize, int exitDonchianSize, double riskRate) {
		super();
		this.stock = stock;
		this.entryDonchianSize = entryDonchianSize;
		this.exitDonchianSize = exitDonchianSize;
		this.riskRate = riskRate;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public int getEntryDonchianSize() {
		return entryDonchianSize;
	}

	public void setEntryDonchianSize(int entryDonchianSize) {
		this.entryDonchianSize = entryDonchianSize;
	}

	public int getExitDonchianSize() {
		return exitDonchianSize;
	}

	public void setExitDonchianSize(int exitDonchianSize) {
		this.exitDonchianSize = exitDonchianSize;
	}

	public double getRiskRate() {
		return riskRate;
	}

	public void setRiskRate(double riskRate) {
		this.riskRate = riskRate;
	}

}
