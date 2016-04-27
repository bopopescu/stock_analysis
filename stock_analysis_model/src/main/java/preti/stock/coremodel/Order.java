package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public abstract class Order implements Serializable {
    private long orderId;
    private OrderType type;
    private long stockId;
    private long modelId;
    private double size;
    private Date date;

    private Stock stock;

    public Order(long orderId, OrderType type, long stockId, long modelId, double size, Date date) {
        super();
        this.orderId = orderId;
        this.type = type;
        this.stockId = stockId;
        this.modelId = modelId;
        this.size = size;
        this.date = date;
    }

    public Order(Stock stock, OrderType type, long modelId, double size, Date date) {
        super();
        this.stock = stock;
        this.type = type;
        this.stockId = stock.getId();
        this.modelId = modelId;
        this.size = size;
        this.date = date;
    }

    public void applyStock(Stock s) {
        this.stock = s;
    }

    @JsonIgnore
    public Stock getStock() {
        return this.stock;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
