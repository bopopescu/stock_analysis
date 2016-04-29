package preti.stock.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import preti.stock.coremodel.BuyOrder;
import preti.stock.coremodel.Order;
import preti.stock.coremodel.SellOrder;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;

public class TradeSystem {
    private static final Log log = LogFactory.getLog(TradeSystem.class);
    private long accountId;
    private double accountBalance;
    private Map<Long, Trade> openTrades;
    private Map<Long, TradingStrategy> tradingStrategies;
    private List<Stock> stocksToAnalyse;
    private Map<Long, Trade> closedTrades;

    public TradeSystem(Collection<Trade> trades, Collection<Stock> stocks, Map<Long, TradingStrategy> strategies,
            double balance, long accountId) {
        this.accountBalance = balance;
        this.tradingStrategies = strategies;
        this.accountId = accountId;
        this.stocksToAnalyse = new ArrayList<>();
        this.stocksToAnalyse.addAll(stocks);
        this.stocksToAnalyse.sort(new Comparator<Stock>() {

            @Override
            public int compare(Stock o1, Stock o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        openTrades = new HashMap<>();
        for (Trade t : trades) {
            if (!t.isOpen())
                throw new IllegalStateException();

            openTrades.put(t.getStockId(), t);
        }

        this.closedTrades = new HashMap<>();
    }

    public Map<Long, Trade> getOpenTrades() {
        return openTrades;
    }

    public Collection<Trade> getAllOpenTrades() {
        return openTrades.values();
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public Map<Long, Trade> getClosedTrades() {
        return closedTrades;
    }

    private SellOrder createSellOrder(Trade trade, Date d, double sellValue) {
        log.info(String.format("Creating sell order for stock %s at date %s", trade.getStockId(), d));

        if (!trade.isOpen())
            throw new IllegalArgumentException(
                    String.format("No open trade to close for stock %s.", trade.getStockId()));

        TradingStrategy strategy = this.tradingStrategies.get(trade.getStockId());
        return new SellOrder(trade.getStock(), trade.getAccountId(), strategy.getModelId(), trade.getSize(), d,
                sellValue);
    }

    // FIXME: nesse método estou assumindo que o valor executado do trade é sempre igual ao da ordem.
    private Trade closeTrade(Trade trade, SellOrder order) {
        log.info(String.format("Closing trade for stock %s at date %s", trade.getStockId(), order.getDate()));

        trade.close(order.getOrderId(), order.getDate(), order.getValue());
        this.accountBalance += trade.getSize() * trade.getSellValue();
        openTrades.remove(trade.getStockId());
        closedTrades.put(trade.getStockId(), trade);
        return trade;
    }

    private BuyOrder createBuyOrder(Stock stock, Date d) {
        log.info(String.format("Creating buy order for stock %s at date %s", stock.getCode(), d));

        double openTradesValue = calculateOpenTradesValue(d);
        TradingStrategy strategy = this.tradingStrategies.get(stock.getId());
        double size = strategy.calculatePositionSize(d, openTradesValue + accountBalance);
        if (size < 1) {
            log.info("Postion size<1: not enough balance to enter position");
            return null;
        }

        double stockValue = stock.getCloseValueAtDate(d);
        while ((size * stockValue) > this.accountBalance && size > 1) {
            size--;
        }
        if (size < 1) {
            log.warn(String.format("Not enough balance to enter position for stock %s at date %d", stock.getCode(), d));
            return null;
        }

        if (isInOpenPosition(stock)) {
            throw new IllegalArgumentException(
                    String.format("Can't open a new trade for stock %s with one already opened.", stock.getCode()));
        }
        return new BuyOrder(stock, accountId, strategy.getModelId(), size, d, stockValue,
                strategy.calculateStopLossPoint(d));

    }

    private Trade openNewTrade(BuyOrder order, Date d) {
        log.info(String.format("Opening new trade for stock %s at date %s", order.getStock().getCode(), d));

        Trade newTrade = new Trade(order.getStock(), order.getAccountId(), order.getSize(), order.getStopPos(),
                order.getOrderId(), d, order.getValue());
        this.accountBalance -= newTrade.getSize() * newTrade.getBuyValue();
        openTrades.put(newTrade.getStockId(), newTrade);
        return newTrade;
    }

    private boolean isInOpenPosition(Stock s) {
        return openTrades.containsKey(s.getId());
    }

    public List<Order> analyzeStocks(Date recomendationDate) {
        List<Order> orders = new ArrayList<>();

        for (Stock stock : stocksToAnalyse) {
            log.info(String.format("Analysing stock %s at date %s", stock.getCode(), recomendationDate));
            if (!stock.hasHistoryAtDate(recomendationDate)) {
                log.info("No data to analyze");
                continue;
            }

            TradingStrategy strategy = this.tradingStrategies.get(stock.getId());
            if (strategy == null) {
                continue;
            }

            if (isInOpenPosition(stock)) {
                Trade openTrade = openTrades.get(stock.getId());
                boolean profittable = openTrade.isProfitable(recomendationDate);
                if ((profittable && strategy.exitPosition(recomendationDate))
                        || (!profittable && openTrade.hasReachedStopPosition(recomendationDate))) {
                    SellOrder order = createSellOrder(openTrade, recomendationDate,
                            stock.getCloseValueAtDate(recomendationDate));
                    closeTrade(openTrade, order);
                    orders.add(order);
                }

            } else {
                if (strategy.enterPosition(recomendationDate)) {
                    BuyOrder order = createBuyOrder(stock, recomendationDate);

                    if (order != null) {
                        openNewTrade(order, recomendationDate);
                        orders.add(order);
                    }
                }
            }
        }

        return orders;
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
        Long[] stockIds = openTrades.keySet().toArray(new Long[] {});
        for (Long stockId : stockIds) {
            Trade t = openTrades.get(stockId);
            if (t.isOpen()) {
                SellOrder order = createSellOrder(t, d, t.getStock().getCloseValueAtDate(d));
                closeTrade(t, order);
            }
        }
    }

}
