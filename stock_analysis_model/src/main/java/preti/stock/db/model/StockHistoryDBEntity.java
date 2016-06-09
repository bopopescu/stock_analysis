package preti.stock.db.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockHistoryDBEntity implements Serializable {
    private long id;
    private long stockId;
    private Date date;
    private double high, low, close, volume;

    public StockHistoryDBEntity() {

    }

    public StockHistoryDBEntity(long id, long stockId, Date date, double high, double low, double close, double volume) {
        super();
        this.id = id;
        this.stockId = stockId;
        this.date = date;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

}
