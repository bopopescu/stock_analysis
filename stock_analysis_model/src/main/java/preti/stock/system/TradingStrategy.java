package preti.stock.system;

import java.util.Date;

public interface TradingStrategy<S, T> {
    long getId();

    Stock<S> getStock();

    boolean hasReachedMaxGain(Trade<T> trade, Date date);

    boolean hasReachedStopLoss(Trade<T> trade, Date date);

    boolean shouldBuyStock(Date date);

    double calculatePositionSize(Date date, double accountInitialBalance, double accountCurrentBalance,
            double openPositionsValue);
    
    double calculateStopLossPoint(Date d);
}
