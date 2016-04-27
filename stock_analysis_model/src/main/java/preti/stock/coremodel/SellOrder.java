package preti.stock.coremodel;

import java.util.Date;

@SuppressWarnings("serial")
public class SellOrder extends Order {

    private double value;
    
    public SellOrder(Stock stock,  long modelId, double size, Date date, double value) {
        super(stock, OrderType.SELL, modelId, size, date);
        this.value = value;
    }

    public SellOrder(long orderId,  long stockId, long modelId, long size, Date date, double value) {
        super(orderId, OrderType.SELL, stockId, modelId, size, date);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
