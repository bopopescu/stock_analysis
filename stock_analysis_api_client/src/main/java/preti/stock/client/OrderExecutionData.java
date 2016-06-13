package preti.stock.client;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class OrderExecutionData implements Serializable {
    private long orderId;
    private Date executionDate;
    private double executionValue;

    public OrderExecutionData() {

    }

    public OrderExecutionData(long orderId, Date executionDate, double executionValue) {
        super();
        this.orderId = orderId;
        this.executionDate = executionDate;
        this.executionValue = executionValue;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public double getExecutionValue() {
        return executionValue;
    }

    public void setExecutionValue(double executionValue) {
        this.executionValue = executionValue;
    }

}
