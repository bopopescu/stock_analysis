package preti.stock.fe.location.home;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TradeVO implements Serializable {

	private long tradeId;
	private long stockId;
	private String stockCode;
	private String stockName;
	private double size;
	private double stopPos;
	private Date buyDate;
	private Date sellDate;
	private double buyValue;
	private double sellValue;

	public TradeVO(long tradeId, long stockId, String stockCode, String stockName, double size, double stopPos,
			Date buyDate, Date sellDate, double buyValue, double sellValue) {
		super();
		this.tradeId = tradeId;
		this.stockId = stockId;
		this.stockCode = stockCode;
		this.stockName = stockName;
		this.size = size;
		this.stopPos = stopPos;
		this.buyDate = buyDate;
		this.sellDate = sellDate;
		this.buyValue = buyValue;
		this.sellValue = sellValue;
	}

	public long getTradeId() {
		return tradeId;
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

	public double getBuyValue() {
		return buyValue;
	}

	public double getSellValue() {
		return sellValue;
	}

	public double getTotalBuyValue() {
		return buyValue * size;
	}

	public double getTotalSellValue() {
		return sellValue * size;
	}

}
