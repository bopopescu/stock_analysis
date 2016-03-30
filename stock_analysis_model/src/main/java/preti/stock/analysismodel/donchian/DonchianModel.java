package preti.stock.analysismodel.donchian;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DonchianModel implements Serializable {

	private long id;
	private long stockId;
	private int entryDonchianSize, exitDonchianSize;
	private double riskRate;

	public DonchianModel() {

	}

	public DonchianModel(long id, long stockId, int entryDonchianSize, int exitDonchianSize, double riskRate) {
		super();
		this.stockId = stockId;
		this.id = id;
		this.entryDonchianSize = entryDonchianSize;
		this.exitDonchianSize = exitDonchianSize;
		this.riskRate = riskRate;
	}

	public long getId() {
		return id;
	}

	public long getStockId() {
		return stockId;
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
