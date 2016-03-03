package preti.spark.stock.run;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DonchianParametersOptimizationResult implements Serializable {

	private String stock;
	private int entryDonchianSize, exitDonchianSize;
	private double riskRate;
	
	public DonchianParametersOptimizationResult() {
		
	}

	public DonchianParametersOptimizationResult(String stock, int entryDonchianSize, int exitDonchianSize,
			double riskRate) {
		super();
		this.stock = stock;
		this.entryDonchianSize = entryDonchianSize;
		this.exitDonchianSize = exitDonchianSize;
		this.riskRate = riskRate;
	}

	public String getStock() {
		return stock;
	}

	public int getEntryDonchianSize() {
		return entryDonchianSize;
	}

	public int getExitDonchianSize() {
		return exitDonchianSize;
	}

	public double getRiskRate() {
		return riskRate;
	}

}
