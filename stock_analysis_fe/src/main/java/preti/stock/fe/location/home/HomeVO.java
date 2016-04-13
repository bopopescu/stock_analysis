package preti.stock.fe.location.home;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HomeVO implements Serializable {

	private long accountId;
	private double accountInitialPosition;
	private double accountBalance;
	private List<TradeVO> trades;

	public HomeVO(long accountId, double accountInitialPosition, double accountBalance, List<TradeVO> trades) {
		super();
		this.accountId = accountId;
		this.accountInitialPosition = accountInitialPosition;
		this.accountBalance = accountBalance;
		this.trades = trades;
	}

	public long getAccountId() {
		return accountId;
	}

	public double getAccountInitialPosition() {
		return accountInitialPosition;
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public List<TradeVO> getTrades() {
		return trades;
	}

}
