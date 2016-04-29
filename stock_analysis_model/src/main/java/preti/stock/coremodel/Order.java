package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = BuyOrder.class, name = "BUY"), @Type(value = SellOrder.class, name = "SELL") })
public abstract class Order implements Serializable {
    private long orderId;
    private OrderType type;
    private long accountId;
    private long stockId;
    private long modelId;
    private double size;
    private Date date;

    private Stock stock;
    
    public Order() {
        
    }

    public Order(long orderId, OrderType type, long accountId, long stockId, long modelId, double size, Date date) {
        super();
        this.orderId = orderId;
        this.type = type;
        this.accountId = accountId;
        this.stockId = stockId;
        this.modelId = modelId;
        this.size = size;
        this.date = date;
    }

    public Order(Stock stock, OrderType type, long accountId, long modelId, double size, Date date) {
        super();
        this.stock = stock;
        this.type = type;
        this.accountId = accountId;
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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
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
