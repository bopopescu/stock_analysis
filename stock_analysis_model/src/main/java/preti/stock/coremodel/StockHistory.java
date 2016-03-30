package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockHistory implements Serializable {
	private long id;
	private long stockId;
	private Date date;
	private double high, low, close, volume;

	public StockHistory() {

	}

	public StockHistory(long id, long stockId, Date date, double high, double low, double close, double volume) {
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

	public long getStockId() {
		return stockId;
	}

	public Date getDate() {
		return date;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public double getVolume() {
		return volume;
	}

	public String toString() {
		return String.format("stockId=%s date=%s high=%s low=%s close=%s volume=%s", stockId, date, high, low, close,
				volume);
	}

}
