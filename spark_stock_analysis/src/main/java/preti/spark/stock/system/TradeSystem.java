package preti.spark.stock.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import preti.spark.stock.model.Stock;
import preti.spark.stock.model.Trade;

@SuppressWarnings("serial")
public class TradeSystem implements Serializable {
	private static final Log log = LogFactory.getLog(TradeSystem.class);

	private Collection<StockContext> wallet;
	private double accountInitialPosition;
	private double accountBalance;
	private Map<Date, Double> balanceHistory;

	private Map<String, TradingStrategy> tradingStrategies;

	public TradeSystem(Stock stock, double accountInitialPosition, TradingStrategy strategy) {
		this(Arrays.asList(stock), accountInitialPosition, null);
		this.tradingStrategies = new HashMap<>();
		tradingStrategies.put(stock.getCode(), strategy);
	}

	public TradeSystem(Collection<Stock> stocks, double accountInitialPosition,
			Map<String, TradingStrategy> tradingStrategies) {
		this.accountInitialPosition = accountInitialPosition;
		this.accountBalance = accountInitialPosition;

		if (stocks == null || stocks.isEmpty()) {
			throw new IllegalArgumentException("No stocks to analyze.");
		}

		wallet = new ArrayList<>();
		for (Stock s : stocks) {
			wallet.add(new StockContext(s));
		}

		balanceHistory = new TreeMap<>();
		this.tradingStrategies = tradingStrategies;
	}

	// TODO: esse construtor foi criado para um caso muito específico, que é
	// gerar as recomendações para um dia em específico.
	// Vale verificar se não é melhor rever essa classe.
	public TradeSystem(List<Trade> trades, Collection<Stock> stocks, double accountInitialPosition,
			Map<String, TradingStrategy> tradingStrategies) {
		this(stocks, accountInitialPosition, tradingStrategies);

		// FIXME: está tosco, mas é um MVP
		for (Trade t : trades) {
			for (StockContext sc : wallet) {
				if (sc.getStock().getCode().equals(t.getStock().getCode())) {
					sc.addTrade(t);
					break;
				}
			}
		}
	}

	public Map<Date, Double> getBalanceHistory() {
		return balanceHistory;
	}

	public Collection<StockContext> getWallet() {
		return wallet;
	}

	public List<Stock> getStocks() {
		List<Stock> stocks = new ArrayList<>();
		for (StockContext st : wallet) {
			stocks.add(st.getStock());
		}
		return stocks;
	}

	public void setTradingStrategies(Map<String, TradingStrategy> strategies) {
		this.tradingStrategies = strategies;
	}

	public void applyTradingStrategy(String stockCode, TradingStrategy strategy) {
		this.tradingStrategies.put(stockCode, strategy);
	}

	private Trade openNewTrade(StockContext stockTrade, Date d) {
		TradingStrategy strategy = this.tradingStrategies.get(stockTrade.getStock().getCode());
		double size = strategy.calculatePositionSize(d);
		if (size < 1) {
			log.info("Postion size<1: not enough balance to enter position");
			return null;
		}

		double stockValue = stockTrade.getStock().getCloseValueAtDate(d);
		while ((size * stockValue) > this.accountBalance && size > 1) {
			size--;
		}
		if (size < 1) {
			log.warn("Not enough balance to enter position");
			return null;
		}

		Trade t = stockTrade.openNewTrade(size, d, strategy.calculateStopLossPoint(d));
		log.debug("Opening new trade: " + t);
		this.accountBalance -= t.getSize() * t.getBuyValue();
		return t;
	}

	private Trade closeLastTrade(StockContext stockTrade, Date d) {
		Trade t = stockTrade.closeLastTrade(d);
		this.accountBalance += t.getSize() * t.getSellValue();
		log.debug("Closing trade " + t);
		return t;
	}

	private double calculateTotalOpenPositions(Date d) {
		double total = 0;
		for (StockContext st : wallet) {
			if (st.isInOpenPosition()) {
				Trade openTrade = st.getLastTrade();
				total += openTrade.getSize() * openTrade.getStock().getCloseValueAtDate(d);
			}
		}
		return total;
	}

	public double getAccountInitialPosition() {
		return accountInitialPosition;
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public List<Trade> analyzeStocks(Date recomendationDate) {
		List<Trade> trades = new ArrayList<>();
		for (StockContext stockTrade : wallet) {
			if (!stockTrade.getStock().hasHistoryAtDate(recomendationDate)) {
				continue;
			}

			TradingStrategy strategy = this.tradingStrategies.get(stockTrade.getStock().getCode());
			if (strategy == null) {
				continue;
			}

			if (stockTrade.isInOpenPosition()) {
				boolean profittable = stockTrade.isProfittable(recomendationDate);
				if ((profittable && strategy.exitPosition(recomendationDate))
						|| (!profittable && stockTrade.hasReachedStopPosition(recomendationDate))) {
					trades.add(closeLastTrade(stockTrade, recomendationDate));
				}

			} else {
				if (strategy.enterPosition(recomendationDate)) {
					trades.add(openNewTrade(stockTrade, recomendationDate));
				}
			}
		}
		return trades;
	}

	public void analyzeStocks(Date initialDate, Date finalDate) {
		// Identifica todas as datas, de forma unica e ordenada
		TreeSet<Date> allDates = new TreeSet<>();
		for (StockContext st : this.wallet) {
			allDates.addAll(st.getStock().getAllHistoryDates());
		}

		// Verifica se foi especificado uma data inicial
		if (initialDate != null) {
			allDates = new TreeSet<Date>(allDates.tailSet(initialDate));
		}

		// Verifica se foi especificado uma data final
		if (finalDate != null) {
			allDates = new TreeSet<Date>(allDates.headSet(finalDate, true));
		}

		for (Date date : allDates) {
			for (StockContext stockTrade : wallet) {
				if (!stockTrade.getStock().hasHistoryAtDate(date)) {
					continue;
				}

				TradingStrategy strategy = this.tradingStrategies.get(stockTrade.getStock().getCode());
				if (strategy == null) {
					continue;
				}

				if (stockTrade.isInOpenPosition()) {
					boolean profittable = stockTrade.isProfittable(date);
					if ((profittable && strategy.exitPosition(date))
							|| (!profittable && stockTrade.hasReachedStopPosition(date))) {
						closeLastTrade(stockTrade, date);
					}

				} else {
					if (strategy.enterPosition(date)) {
						openNewTrade(stockTrade, date);
					}
				}
			}
			balanceHistory.put(date, this.accountBalance);
		}
	}

	public void closeAllOpenTrades(Date d) {
		for (StockContext st : wallet) {
			if (st.isInOpenPosition()) {
				this.closeLastTrade(st, d);
			}
		}
		balanceHistory.put(d, this.accountBalance);
	}

}
