package preti.stock.db.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(value = { "profitable" }, ignoreUnknown = true)
public class TradeDBEntity implements Serializable {
    private long id;
    private long stockId;
    private double size;
    private double stopPos;
    private long buyOrderId;
    private long sellOrderId;
    private Date buyDate;
    private Date sellDate;
    private double buyValue;
    private double sellValue;

    public TradeDBEntity() {

    }

    public TradeDBEntity(long stockId, double size, double stopPos, long buyOrderId, Date buyDate, Date sellDate,
            double buyValue) {
        this(0, stockId, size, stopPos, buyOrderId, 0, buyDate, null, buyValue, 0);
    }

    public TradeDBEntity(long id, long stockId, double size, double stopPos, long buyOrderId, long sellOrderId,
            Date buyDate, Date sellDate, double buyValue, double sellValue) {
        super();
        this.id = id;
        this.stockId = stockId;
        this.size = size;
        this.stopPos = stopPos;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyDate = buyDate;
        this.sellDate = sellDate;
        this.buyValue = buyValue;
        this.sellValue = sellValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getStopPos() {
        return stopPos;
    }

    public void setStopPos(double stopPos) {
        this.stopPos = stopPos;
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(long buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public double getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(double buyValue) {
        this.buyValue = buyValue;
    }

    public double getSellValue() {
        return sellValue;
    }

    public void setSellValue(double sellValue) {
        this.sellValue = sellValue;
    }

    public boolean isOpen() {
        return sellOrderId == 0;
    }
}
