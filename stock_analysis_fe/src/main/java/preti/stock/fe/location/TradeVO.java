package preti.stock.fe.location;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TradeVO implements Serializable {

    private long tradeId;
    private long stockId;
    private long buyOrderId, sellOrderId;
    private String stockCode;
    private String stockName;
    private double size;
    private double stopPos;
    private Date buyDate;
    private Date sellDate;
    private double buyValue;
    private double sellValue;

    public TradeVO() {

    }

    public TradeVO(long tradeId, long stockId, long buyOrderId, long sellOrderId, String stockCode, String stockName,
            double size, double stopPos, Date buyDate, Date sellDate, double buyValue, double sellValue) {
        super();
        this.tradeId = tradeId;
        this.stockId = stockId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
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

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
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

    public String getFormattedStopPos() {
        return PresentationTools.formatCurrency(stopPos);
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public String getFormattedBuyDate() {
        if (buyDate != null)
            return PresentationTools.formatDate(buyDate);
        else
            return "";

    }

    public String getFormattedSellDate() {
        if (sellDate != null)
            return PresentationTools.formatDate(sellDate);
        else
            return "";
    }

    public Date getSellDate() {
        return sellDate;
    }

    public double getBuyValue() {
        return buyValue;
    }

    public String getFormattedBuyValue() {
        return PresentationTools.formatCurrency(buyValue);
    }

    public String getFormattedSellValue() {
        return PresentationTools.formatCurrency(sellValue);
    }

    public double getSellValue() {
        return sellValue;
    }

    public double getTotalBuyValue() {
        return buyValue * size;
    }

    public String getFormatedTotalBuyValue() {
        return PresentationTools.formatCurrency(getTotalBuyValue());
    }

    public double getTotalSellValue() {
        return sellValue * size;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public void setBuyOrderId(long buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public void setSellOrderId(long sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setStopPos(double stopPos) {
        this.stopPos = stopPos;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public void setFormattedBuyDate(String buyDate) {
        this.buyDate = PresentationTools.parseDate(buyDate);
    }

    public void setFormattedSellDate(String sellDate) {
        this.sellDate = PresentationTools.parseDate(sellDate);
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public void setBuyValue(double buyValue) {
        this.buyValue = buyValue;
    }

    public void setSellValue(double sellValue) {
        this.sellValue = sellValue;
    }

    public boolean isBuyTrade() {
        return tradeId == 0;
    }

}
