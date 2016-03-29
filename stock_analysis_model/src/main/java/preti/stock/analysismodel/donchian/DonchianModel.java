package preti.stock.analysismodel.donchian;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DonchianModel implements Serializable {

	private long id;
	private String stock;
	private int entryDonchianSize, exitDonchianSize;
	private double riskRate;

	public DonchianModel() {

	}

	public DonchianModel(String stock, long id, int entryDonchianSize, int exitDonchianSize, double riskRate) {
		super();
		this.stock = stock;
		this.id = id;
		this.entryDonchianSize = entryDonchianSize;
		this.exitDonchianSize = exitDonchianSize;
		this.riskRate = riskRate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
