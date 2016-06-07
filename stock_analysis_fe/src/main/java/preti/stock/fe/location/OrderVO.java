package preti.stock.fe.location;

import java.io.Serializable;
import java.util.Date;

import preti.stock.coremodel.OrderType;

@SuppressWarnings("serial")
public class OrderVO implements Serializable {

    private long orderId;
    private long accountId;
    private OrderType type;
    private long stockId;
    private String stockCode;
    private String stockName;
    private long modelId;
    private double size;
    private Date date;

    private double value;
    private double stopPos;

    private double previousBuyValue;
    private Date previousBuyDate;

    public OrderVO() {

    }

    public OrderVO(long orderId, long accountId, OrderType type, long stockId, String stockCode, String stockName,
            long modelId, double size, Date date, double value, double stopPos) {
        super();
        this.orderId = orderId;
        this.accountId = accountId;
        this.type = type;
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.modelId = modelId;
        this.size = size;
        this.date = date;
        this.value = value;
        this.stopPos = stopPos;
    }

    public OrderVO(long orderId, long accountId, OrderType type, long stockId, String stockCode, String stockName,
            long modelId, double size, Date creationDate, double value, double previousBuyValue, Date previousBuyDate) {
        this(orderId, accountId, type, stockId, stockCode, stockName, modelId, size, creationDate, value, 0);
        this.previousBuyValue = previousBuyValue;
        this.previousBuyDate = previousBuyDate;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setTypeStr(String type) {
        setType(OrderType.valueOf(type));
    }

    public long getStockId() {
        return stockId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
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

    public String getFormattedDate() {
        if (date != null)
            return PresentationTools.formatDate(date);
        else
            return "";

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFormattedDate(String date) {
        setDate(PresentationTools.parseDate(date));
    }

    public double getValue() {
        return value;
    }

    public String getFormattedValue() {
        return PresentationTools.formatCurrency(value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getStopPos() {
        return stopPos;
    }

    public String getFormattedStopPos() {
        return PresentationTools.formatCurrency(stopPos);
    }

    public void setStopPos(double stopPos) {
        this.stopPos = stopPos;
    }

    public boolean isBuyOrder() {
        return OrderType.BUY.equals(type);
    }

    public boolean isSellOrder() {
        return OrderType.SELL.equals(type);
    }

    public String getFormattedPreviousBuyValue() {
        if (previousBuyValue != 0)
            return PresentationTools.formatCurrency(previousBuyValue);
        else
            return "";
    }

    public String getFormattedPreviousBuyDate() {
        if (previousBuyDate != null)
            return PresentationTools.formatDate(previousBuyDate);
        else
            return "";
    }

}
