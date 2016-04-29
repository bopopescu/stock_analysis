package preti.stock.coremodel;

import java.util.Date;

@SuppressWarnings("serial")
public class SellOrder extends Order {

    private double value;

    public SellOrder() {

    }

    public SellOrder(Stock stock, long accountId, long modelId, double size, Date date, double value) {
        super(stock, OrderType.SELL, accountId, modelId, size, date);
        this.value = value;
    }

    public SellOrder(long orderId, long accountId, long stockId, long modelId, double size, Date date, double value) {
        super(orderId, OrderType.SELL, accountId, stockId, modelId, size, date);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
