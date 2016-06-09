package preti.stock.system;

import java.util.Date;

public interface Trade<T> {
    
    @SuppressWarnings("rawtypes")
    Stock getStock();

    boolean isOpen();

    double getStopLossPosition();
    
    double getSize();
    
    double getTotalValue(Date date);
    
    double getProfit(Date date);
    
    boolean isProfitable(Date d);
    
    T getTarget();

}
