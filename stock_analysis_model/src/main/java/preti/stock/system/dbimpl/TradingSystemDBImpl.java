package preti.stock.system.dbimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.StockDBEntity;
import preti.stock.db.model.wrapper.TradeWrapper;
import preti.stock.system.Recomendation;
import preti.stock.system.Stock;
import preti.stock.system.Trade;
import preti.stock.system.TradingStrategy;
import preti.stock.system.TradingSystem;

public class TradingSystemDBImpl extends TradingSystem<OrderDBEntity, StockDBEntity, TradeWrapper> {
    private static final Log log = LogFactory.getLog(TradingSystemDBImpl.class);

    private double accountInitialPosition, accountBalance;
    private List<StockDBImpl> stocksToAnalyse;
    private Map<String, TradeDBImpl> openTrades;
    private Map<String, DonchianStrategyDBImpl> tradingStrategies;

    public TradingSystemDBImpl(Collection<TradeDBImpl> trades, Collection<StockDBImpl> stocks,
            Map<String, DonchianStrategyDBImpl> strategies, double initialPosition, double balance) {
        this.accountInitialPosition = initialPosition;
        this.accountBalance = balance;
        this.tradingStrategies = strategies;

        this.stocksToAnalyse = new ArrayList<>();
        this.stocksToAnalyse.addAll(stocks);
        this.stocksToAnalyse.sort(new Comparator<StockDBImpl>() {

            @Override
            public int compare(StockDBImpl o1, StockDBImpl o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        openTrades = new HashMap<>();
        for (TradeDBImpl t : trades) {
            if (!t.isOpen())
                throw new IllegalStateException();

            openTrades.put(t.getStock().getCode(), t);
        }
    }

    @Override
    protected List<StockDBImpl> getStocksToAnalyze() {
        return stocksToAnalyse;
    }

    @Override
    protected TradingStrategy<StockDBEntity, TradeWrapper> getStrategyForStock(Stock<StockDBEntity> stock) {
        return tradingStrategies.get(stock.getCode());
    }

    @Override
    protected Trade<TradeWrapper> getOpenTradeForStock(Stock<StockDBEntity> stock) {
        return openTrades.get(stock.getCode());
    }

    @Override
    protected Recomendation<OrderDBEntity> createSellRecomendation(Trade<TradeWrapper> trade, Date date) {
        StockDBImpl stock = (StockDBImpl) trade.getStock();
        log.info(String.format("Creating sell recomendation for stock %s at date %s", stock.getCode(), date));

        if (!trade.isOpen())
            throw new IllegalArgumentException(String.format("No open trade to close for stock %s.", stock.getCode()));

        TradingStrategy<StockDBEntity, TradeWrapper> strategy = this.tradingStrategies.get(stock.getCode());
        return RecomendationDBImpl.createSellRecomendation(stock, strategy.getId(), trade.getSize(), date,
                stock.getCloseValueAtDate(date));

    }

    @Override
    protected Recomendation<OrderDBEntity> createBuyRecomendation(Stock<StockDBEntity> stock, double size, Date date) {
        log.info(String.format("Creating buy recomendation for stock %s at date %s", stock.getCode(), date));

        double stockValue = stock.getCloseValueAtDate(date);
        TradingStrategy<StockDBEntity, TradeWrapper> strategy = getStrategyForStock(stock);
        double stopLoss = strategy.calculateStopLossPoint(date);

        return RecomendationDBImpl.createBuyRecomendation(stock, strategy.getId(), size, date, stockValue, stopLoss);
    }

    @Override
    protected double getInitialBalance() {
        return accountInitialPosition;
    }

    @Override
    protected double getAvailableMoney() {
        return accountBalance;
    }

    @Override
    protected double getOpenTradesValue(Date date) {
        double value = 0;
        for (TradeDBImpl t : openTrades.values()) {
            if (!t.isOpen())
                continue;

            value += t.getTotalValue(date);
        }

        return value;
    }

    @Override
    protected void incrementAvailableMoney(double value) {
        this.accountBalance += value;
    }

    @Override
    protected void decrementAvailableMoney(double value) {
        this.accountBalance -= value;
    }

}
