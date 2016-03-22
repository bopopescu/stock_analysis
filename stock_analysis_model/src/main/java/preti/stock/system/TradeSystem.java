package preti.stock.system;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;

public class TradeSystem {
	private static final Log log = LogFactory.getLog(TradeSystem.class);

	private double accountBalance;
	private Map<String, Trade> openTrades;
	private Map<String, TradingStrategy> tradingStrategies;
	private Collection<Stock> stocksToAnalyse;
	private Map<String, Trade> closedTrades;

	public TradeSystem(Collection<Trade> trades, Collection<Stock> stocks, Map<String, TradingStrategy> strategies,
			double balance) {
		this.accountBalance = balance;
		this.tradingStrategies = strategies;
		this.stocksToAnalyse = stocks;

		openTrades = new HashMap<>();
		for (Trade t : trades) {
			if (!t.isOpen())
				throw new IllegalStateException();

			openTrades.put(t.getStock().getCode(), t);
		}

		this.closedTrades = new HashMap<>();
	}

	public Map<String, Trade> getOpenTrades() {
		return openTrades;
	}

	public Collection<Trade> getAllOpenTrades() {
		return openTrades.values();
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public Map<String, Trade> getClosedTrades() {
		return closedTrades;
	}

	private void closeTrade(Trade trade, Date d, double sellValue) {
		log.info(String.format("Closing trade for stock %s at date %s", trade.getStockCode(), d));

		if (!trade.isOpen())
			throw new IllegalArgumentException(
					String.format("No open trade to close for stock %s.", trade.getStockCode()));

		trade.close(d, sellValue);
		this.accountBalance += trade.getSize() * trade.getSellValue();
		openTrades.remove(trade.getStockCode());
		closedTrades.put(trade.getStockCode(), trade);
	}

	private void openNewTrade(Stock stock, Date d) {
		log.info(String.format("Opening new trade for stock %s at date %s", stock.getCode(), d));

		double openTradesValue = calculateOpenTradesValue(d);
		TradingStrategy strategy = this.tradingStrategies.get(stock.getCode());
		double size = strategy.calculatePositionSize(d, openTradesValue + accountBalance);
		if (size < 1) {
			log.info("Postion size<1: not enough balance to enter position");
			return;
		}

		double stockValue = stock.getCloseValueAtDate(d);
		while ((size * stockValue) > this.accountBalance && size > 1) {
			size--;
		}
		if (size < 1) {
			log.warn(String.format("Not enough balance to enter position for stock %s at date %d", stock.getCode(), d));
			return;
		}

		if (isInOpenPosition(stock)) {
			throw new IllegalArgumentException(
					String.format("Can't open a new trade for stock %s with one already opened.", stock.getCode()));
		}
		Trade newTrade = new Trade(stock, size, strategy.calculateStopLossPoint(d), d, stockValue);
		this.accountBalance -= newTrade.getSize() * newTrade.getBuyValue();
		openTrades.put(newTrade.getStockCode(), newTrade);
	}

	private boolean isInOpenPosition(Stock s) {
		return openTrades.containsKey(s.getCode());
	}

	public void analyzeStocks(Date recomendationDate) {
		for (Stock stock : stocksToAnalyse) {
			log.debug(String.format("Analysing stock %s at date %s", stock.getCode(), recomendationDate));
			if (!stock.hasHistoryAtDate(recomendationDate)) {
				log.debug("No data to analyze");
				continue;
			}

			TradingStrategy strategy = this.tradingStrategies.get(stock.getCode());
			if (strategy == null) {
				continue;
			}

			if (isInOpenPosition(stock)) {
				Trade openTrade = openTrades.get(stock.getCode());
				boolean profittable = openTrade.isProfitable(recomendationDate);
				if ((profittable && strategy.exitPosition(recomendationDate))
						|| (!profittable && openTrade.hasReachedStopPosition(recomendationDate))) {
					closeTrade(openTrade, recomendationDate, stock.getCloseValueAtDate(recomendationDate));
				}

			} else {
				if (strategy.enterPosition(recomendationDate)) {
					openNewTrade(stock, recomendationDate);
				}
			}
		}
	}

	private double calculateOpenTradesValue(Date d) {
		double value = 0;
		for (Trade t : getAllOpenTrades()) {
			if (!t.isOpen())
				continue;

			value += t.getTotalValue(d);
		}

		return value;
	}

	public void closeAllOpenTrades(Date d) {
		String[] stockCodes = openTrades.keySet().toArray(new String[] {});
		for (String stockCode : stockCodes) {
			Trade t = openTrades.get(stockCode);
			if (t.isOpen())
				closeTrade(t, d, t.getStock().getCloseValueAtDate(d));
		}
	}

}
