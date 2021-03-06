package preti.stock.client.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Order implements Serializable {

    private long orderId;
    private OperationType type;
    private long stockId;
    private long modelId;
    private double size;
    private Date creationDate;
    private double value;
    private double stopPos;

    public Order() {

    }
    
    public Order(OperationType type, long stockId, long modelId, double size, Date creationDate, double value) {
        this(0, type, stockId, modelId, size, creationDate, value, 0);
    }

    public Order(OperationType type, long stockId, long modelId, double size, Date creationDate, double value,
            double stopPos) {
        this(0, type, stockId, modelId, size, creationDate, value, stopPos);
    }

    public Order(long orderId, OperationType type, long stockId, long modelId, double size, Date creationDate,
            double value, double stopPos) {
        super();
        this.orderId = orderId;
        this.type = type;
        this.stockId = stockId;
        this.modelId = modelId;
        this.size = size;
        this.creationDate = creationDate;
        this.value = value;
        this.stopPos = stopPos;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
