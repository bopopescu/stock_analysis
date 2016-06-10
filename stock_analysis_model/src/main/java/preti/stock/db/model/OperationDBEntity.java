package preti.stock.db.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class OperationDBEntity implements Serializable {

    private long operationId;
    private long orderId;
    private Date creationDate;
    private double size;
    private double value;
    private double stopLoss;

    public OperationDBEntity() {
        super();
    }

    public OperationDBEntity(long orderId, Date creationDate, double size, double value, double stopLoss) {
        this.orderId = orderId;
        this.creationDate = creationDate;
        this.size = size;
        this.value = value;
        this.stopLoss = stopLoss;
    }

    public OperationDBEntity(long operationId, long orderId, Date creationDate, double size,
            double value, double stopLoss) {
        this(orderId, creationDate, size, value, stopLoss);
        this.operationId = operationId;
    }

    public long getOperationId() {
        return operationId;
    }

    public void setOperationId(long operationId) {
        this.operationId = operationId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

}
