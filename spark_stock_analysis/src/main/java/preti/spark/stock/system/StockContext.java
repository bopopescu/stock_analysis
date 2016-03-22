package preti.spark.stock.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockContext implements Serializable {
	protected static final Log log = LogFactory.getLog(StockContext.class);

	private Stock stock;
	private List<Trade> trades;

	public StockContext() {

	}

	public StockContext(Stock stock) {
		super();
		this.stock = stock;
		trades = new ArrayList<>();
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public void applyStocksToAllTrades(Stock s) {
		setStock(s);
		if (trades == null || trades.isEmpty()) {
			return;
		}

		for (Trade t : trades) {
			t.setStock(s);
		}
	}

	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public void addTrade(Trade t) {
		trades.add(t);
		Collections.sort(trades, (Trade t1, Trade t2) -> t1.getBuyDate().compareTo(t2.getBuyDate()));
	}

	@JsonIgnore
	public Trade getLastTrade() {
		if (trades.size() == 0)
			return null;

		return trades.get(trades.size() - 1);
	}

	@JsonIgnore
	public boolean isInOpenPosition() {
		Trade t = getLastTrade();
		return t != null && t.isOpen();
	}

	public boolean hasReachedStopPosition(Date d) {
		Trade t = getLastTrade();
		return t != null && t.isOpen() && t.hasReachedStopPosition(d);
	}

	@JsonIgnore
	public boolean isProfittable(Date d) {
		Trade t = getLastTrade();
		return t != null && t.isProfitable(d);
	}

	public boolean hasAnyTrade() {
		return trades.size() > 0;
	}

	@JsonIgnore
	public Trade getTradeOpenAt(Date d) {
		Trade tradeOpen = null;
		for (Trade t : trades) {
			if (t.getBuyDate().equals(d)) {
				tradeOpen = t;
				break;
			}
		}
		return tradeOpen;
	}

	@JsonIgnore
	public Trade getTradeClosedAt(Date d) {
		Trade tradeClosed = null;
		for (Trade t : trades) {
			if (t.getSellDate().equals(d)) {
				tradeClosed = t;
				break;
			}
		}
		return tradeClosed;
	}

	@JsonIgnore
	public double getOpenPositionsValueAtDate(Date d) {
		if (!hasAnyTrade()) {
			return 0;
		}

		double value = 0;
		for (Trade t : trades) {
			if (t.isOpen(d)) {
				value += t.getTotalValue(d);
			}
		}

		return value;
	}

}
