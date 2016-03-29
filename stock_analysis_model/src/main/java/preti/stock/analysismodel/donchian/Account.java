package preti.stock.analysismodel.donchian;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import preti.stock.coremodel.Trade;

@SuppressWarnings("serial")
public class Account implements Serializable {
	private long id;
	private List<DonchianModel> model;
	private double balance;
	private double initialPosition;
	private Collection<Trade> wallet;
	private List<String> stockCodesToAnalyze;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<DonchianModel> getModel() {
		return model;
	}

	public void setModel(List<DonchianModel> model) {
		this.model = model;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Collection<Trade> getWallet() {
		return wallet;
	}

	public void setWallet(Collection<Trade> wallet) {
		this.wallet = wallet;
	}

	public List<String> getStockCodesToAnalyze() {
		return stockCodesToAnalyze;
	}

	public void setStockCodesToAnalyze(List<String> stockCodesToAnalyze) {
		this.stockCodesToAnalyze = stockCodesToAnalyze;
	}

	public double getInitialPosition() {
		return initialPosition;
	}

	public void setInitialPosition(double initialPosition) {
		this.initialPosition = initialPosition;
	}

}
