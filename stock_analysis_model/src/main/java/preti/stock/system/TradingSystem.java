package preti.stock.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class TradingSystem<R, S, T> {
    private static final Log log = LogFactory.getLog(TradingSystem.class);

    public TradingSystem() {
        // TODO Auto-generated constructor stub
    }

    protected abstract List<? extends Stock<S>> getStocksToAnalyze();

    protected abstract TradingStrategy<S, T> getStrategyForStock(Stock<S> stock);

    protected abstract Trade<T> getOpenTradeForStock(Stock<S> stock);

    protected abstract Recomendation<R> createSellRecomendation(Trade<T> openTrade, Date recomendationDate);

    protected abstract Recomendation<R> createBuyRecomendation(Stock<S> stock, double size, Date date);

    protected abstract double getInitialBalance();

    protected abstract double getAvailableMoney();

    protected abstract double getOpenTradesValue(Date date);
    
    protected abstract void incrementAvailableMoney(double d);
    
    protected abstract void decrementAvailableMoney(double d);

    private boolean shouldExitPosition(Trade<T> trade, TradingStrategy<S, T> strategy, Date date) {
        boolean proffitable = trade.isProfitable(date);
        return (proffitable && strategy.hasReachedMaxGain(trade, date) || (!proffitable && strategy.hasReachedStopLoss(trade, date)));
    }

    private boolean shouldAnalyzeStock(Stock<S> stock, Date date) {
        TradingStrategy<S, T> strategy = getStrategyForStock(stock);
        return strategy != null && stock.hasHistoryAtDate(date);
    }

    public List<Recomendation<R>> analyze(Date recomendationDate) {
        List<Recomendation<R>> recomendations = new ArrayList<>();

        for (Stock<S> stock : getStocksToAnalyze()) {
            if (!shouldAnalyzeStock(stock, recomendationDate)) {
                log.info(String.format("Skipping stock %s at date %s", stock.getCode(), recomendationDate));
                continue;
            }
            log.info(String.format("Analysing stock %s at date %s", stock.getCode(), recomendationDate));

            TradingStrategy<S, T> strategy = getStrategyForStock(stock);
            Trade<T> openTrade = getOpenTradeForStock(stock);

            if (openTrade != null) {
                if (shouldExitPosition(openTrade, strategy, recomendationDate)){
                    Recomendation<R> rec = createSellRecomendation(openTrade, recomendationDate);
                    recomendations.add(rec);
                    incrementAvailableMoney(rec.getSize()*rec.getValue());
                }
            } else if (strategy.shouldBuyStock(recomendationDate)) {
                double size = strategy.calculatePositionSize(recomendationDate, getInitialBalance(),
                        getAvailableMoney(), getOpenTradesValue(recomendationDate));
                if (size >= 1) {
                    Recomendation<R> rec = createBuyRecomendation(stock, size, recomendationDate);
                    recomendations.add(rec);
                    decrementAvailableMoney(rec.getSize()*rec.getValue());
                } else {
                    log.warn(String.format("Not enough balance to enter position for stock %s at date %s",
                            stock.getCode(), recomendationDate));
                }
            }
        }

        return recomendations;
    }
    
    public static void main(String[] args){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(format.format(new Date()));
    }

}
