package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(value = { "profitable" }, ignoreUnknown = true)
public class Trade implements Serializable {
	private Stock stock;
	private double size;
	private double stopPos;
	private Date buyDate;
	private Date sellDate;
	private double buyValue;
	private double sellValue;

	public Trade() {

	}

	public Trade(Stock stock, double size, double stopPos, Date buyDate, double buyValue) {
		this(stock, size, stopPos, buyDate, null, buyValue, 0);
	}

	public Trade(Stock stock, double size, double stopPos, Date buyDate, Date sellDate, double buyValue,
			double sellValue) {
		super();
		this.stock = stock;
		this.size = size;
		this.stopPos = stopPos;
		this.buyDate = buyDate;
		this.sellDate = sellDate;
		this.buyValue = buyValue;
		this.sellValue = sellValue;
	}

	public void setStock(Stock s) {
		this.stock = s;
	}

	public Stock getStock() {
		return stock;
	}

	@JsonIgnore
	public String getStockCode() {
		return stock.getCode();
	}

	public double getSize() {
		return size;
	}

	public double getStopPos() {
		return stopPos;
	}

	public Date getBuyDate() {
		return buyDate;
	}

	public Date getSellDate() {
		return sellDate;
	}

	public void setBuyValue(double buyValue) {
		this.buyValue = buyValue;
	}

	public void setSellValue(double sellValue) {
		this.sellValue = sellValue;
	}

	public boolean isOpen() {
		return sellDate == null;
	}

	public boolean isOpen(Date d) {
		return (buyDate != null && sellDate != null && d.compareTo(buyDate) >= 0 && d.compareTo(sellDate) < 0);
	}

	public void close(Date d, double sellValue) {
		this.sellDate = d;
		this.sellValue = sellValue;
	}

	public double getProfit(Date d) {
		if (buyDate == null) {
			throw new IllegalArgumentException("Trade invalid: buyDate is empty.");
		}

		if (d == null && isOpen()) {
			throw new IllegalArgumentException("Trade not closed and no date informed to calculate profits.");
		}

		if (d != null) {
			return size * (stock.getCloseValueAtDate(d) - buyValue);
		} else {
			return size * (sellValue - buyValue);
		}
	}

	@JsonIgnore
	public boolean isProfitable(Date d) {
		return getProfit(d) > 0;
	}

	@JsonIgnore
	public boolean isProfitable() {
		return getProfit(null) > 0;
	}

	public double getBuyValue() {
		return this.buyValue;
	}

	public double getSellValue() {
		return this.sellValue;
	}

	public boolean hasReachedStopPosition(Date d) {
		if (!isOpen())
			throw new IllegalArgumentException("Trade is not opened.");

		return stock.getCloseValueAtDate(d) <= stopPos;
	}

	public double getTotalValue(Date d) {
		return stock.getCloseValueAtDate(d) * size;
	}

	public String toString() {
		if (isOpen()) {
			return String.format("stock=%s size=%s buyValue=%s stopPos=%s buy=Date%s", stock.getCode(), size,
					getBuyValue(), stopPos, buyDate);
		} else {
			return String.format("stock=%s size=%s buyValue=%s stopPos=%s buy=Date%s sellDate=%s sellValue=%s",
					stock.getCode(), size, getBuyValue(), stopPos, buyDate, sellDate,
					stock.getCloseValueAtDate(sellDate));
		}

	}

}
