package preti.stock.coremodel;

import java.util.Date;

@SuppressWarnings("serial")
public class BuyOrder extends Order {

    private double value;
    private double stopPos;

    public BuyOrder(Stock stock, long modelId, double size, Date date, double value, double stopPos) {
        super(stock, OrderType.BUY, modelId, size, date);
        this.value = value;
        this.stopPos = stopPos;
    }

    public BuyOrder(long orderId, long stockId, long modelId, long size, Date date, double value, double stopPos) {
        super(orderId, OrderType.BUY, stockId, modelId, size, date);
        this.value = value;
        this.stopPos = stopPos;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getStopPos() {
        return stopPos;
    }

    public void setStopPos(double stopPos) {
        this.stopPos = stopPos;
    }

}
