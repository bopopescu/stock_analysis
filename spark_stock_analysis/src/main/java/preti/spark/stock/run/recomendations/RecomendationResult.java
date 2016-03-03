package preti.spark.stock.run.recomendations;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import preti.spark.stock.model.Trade;

@SuppressWarnings("serial")
public class RecomendationResult implements Serializable {
	private final String DATE_FORMAT = "yyyy-MM-dd";

	private String stockCode;
	private double size;
	private double stopPos;
	private String buyDate;
	private String sellDate;

	public RecomendationResult(Trade t) {
		this.stockCode = t.getStock().getCode();
		this.size = t.getSize();
		this.stopPos = t.getStopPos();

		DateFormat format = new SimpleDateFormat(DATE_FORMAT);
		this.buyDate = format.format(t.getBuyDate());
		this.sellDate = format.format(t.getSellDate());
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
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

	public String getBuyDate() {
		return this.buyDate;
	}

	public void setBuyDate(String buyDate) {
		this.buyDate = buyDate;
	}

	public String getSellDate() {
		return sellDate;
	}

	public void setSellDate(String sellDate) {
		this.sellDate = sellDate;
	}

}
