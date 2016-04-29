package preti.stock.coremodel;

import java.util.Date;

@SuppressWarnings("serial")
public class BuyOrder extends Order {

    private double value;
    private double stopPos;
    
    public BuyOrder(){
        
    }

    public BuyOrder(Stock stock, long accountId, long modelId, double size, Date date, double value, double stopPos) {
        super(stock, OrderType.BUY, accountId, modelId, size, date);
        this.value = value;
        this.stopPos = stopPos;
    }

    public BuyOrder(long orderId, long accountId, long stockId, long modelId, double size, Date date, double value, double stopPos) {
        super(orderId, OrderType.BUY, accountId, stockId, modelId, size, date);
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
