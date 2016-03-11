package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown=true)
public class StockHistory implements Serializable {
	private Date date;
	private double high, low, close, volume;
	
	public StockHistory() {
		
	}

	public StockHistory(Date date, double high, double low, double close, double volume) {
		super();
		this.date = date;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
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
		return String.format("date=%s high=%s low=%s close=%s volume=%s", date, high, low, close, volume);
	}

}
