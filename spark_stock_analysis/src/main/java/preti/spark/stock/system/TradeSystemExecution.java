package preti.spark.stock.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.stock.system.TradeSystem;
import preti.stock.system.TradingStrategy;

@SuppressWarnings("serial")
public class TradeSystemExecution implements Serializable {
	@SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(TradeSystemExecution.class);

	private Collection<StockContext> wallet;
	private double accountInitialPosition;
	private double accountBalance;
	private Map<Date, Double> balanceHistory;

	private Map<Long, TradingStrategy> tradingStrategies;

	public TradeSystemExecution(Stock stock, double accountInitialPosition, TradingStrategy strategy) {
		this(Arrays.asList(stock), accountInitialPosition, accountInitialPosition, null);
		this.tradingStrategies = new HashMap<>();
		tradingStrategies.put(stock.getId(), strategy);
	}

	public TradeSystemExecution(Collection<Stock> stocks, double accountInitialPosition, double accountBalance,
			Map<Long, TradingStrategy> tradingStrategies) {
		this.accountInitialPosition = accountInitialPosition;
		this.accountBalance = accountBalance;

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

	public void setTradingStrategies(Map<Long, TradingStrategy> strategies) {
		this.tradingStrategies = strategies;
	}

	public void applyTradingStrategy(Long stockId, TradingStrategy strategy) {
		this.tradingStrategies.put(stockId, strategy);
	}

	public double getAccountInitialPosition() {
		return accountInitialPosition;
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	private Collection<Trade> identifyOpenTrades() {
		Set<Trade> openTrades = new HashSet<>();
		for (StockContext sc : wallet) {
			if (sc.isInOpenPosition()) {
				openTrades.add(sc.getLastTrade());
			}
		}
		return openTrades;
	}

	private void updateWallet(Map<Long, Trade> openTrades, Map<Long, Trade> closedTrades) {
		for (StockContext sc : wallet) {
			Long stockId = sc.getStock().getId();
			if (!sc.isInOpenPosition() && openTrades.containsKey(stockId)) {
				sc.addTrade(openTrades.get(stockId));
			}
		}
	}

	public void analyzeStocks(Date initialDate, Date finalDate) {
		// Identifica todas as datas, de forma unica e ordenada
		TreeSet<Date> allDates = new TreeSet<>();
		for (StockContext st : this.wallet) {
			allDates.addAll(st.getStock().getAllHistoryDates());
		}

		// Identifica todas as a��es
		Collection<Stock> stocks = getStocks();

		// Verifica se foi especificado uma data inicial
		if (initialDate != null) {
			allDates = new TreeSet<Date>(allDates.tailSet(initialDate));
		}

		// Verifica se foi especificado uma data final
		if (finalDate != null) {
			allDates = new TreeSet<Date>(allDates.headSet(finalDate, true));
		}

		for (Date date : allDates) {
			TradeSystem tradeSystem = new TradeSystem(identifyOpenTrades(), stocks, tradingStrategies, accountBalance, 1l);
			tradeSystem.analyzeStocks(date);
			updateWallet(tradeSystem.getOpenTrades(), tradeSystem.getClosedTrades());

			this.accountBalance = tradeSystem.getAccountBalance();
			balanceHistory.put(date, this.accountBalance);
		}
	}

	public void closeAllOpenTrades(Date d) {
		TradeSystem system = new TradeSystem(identifyOpenTrades(), getStocks(), this.tradingStrategies,
				this.accountBalance, 1l);
		system.closeAllOpenTrades(d);
		this.accountBalance = system.getAccountBalance();
		balanceHistory.put(d, this.accountBalance);
	}

}
