package preti.stock.fe.location.home;

import java.io.Serializable;
import java.util.Date;

import preti.stock.fe.location.PresentationTools;

@SuppressWarnings("serial")
public class WalletVO implements Serializable {

    private long walletId;
    private String stockName;
    private String stockCode;
    private double size;
    private double unitValue;
    private Date creationDate;
    private Date updateDate;

    public WalletVO() {

    }

    public WalletVO(long id, String name, String stockCode, double size, double unitValue, Date creationDate,
            Date updateDate) {
        super();
        this.walletId = id;
        this.stockName = name;
        this.stockCode = stockCode;
        this.size = size;
        this.unitValue = unitValue;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
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

    public double getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(double unitValue) {
        this.unitValue = unitValue;
    }

    public String getFormattedUnitValue() {
        return PresentationTools.formatCurrency(this.unitValue);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        if (creationDate != null)
            return PresentationTools.formatDate(creationDate);
        else
            return "";
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getFormattedUpdateDate() {
        if (updateDate != null)
            return PresentationTools.formatDate(updateDate);
        else
            return "";
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public double getTotalValue() {
        return size * unitValue;
    }

    public String getFormattedTotalValue() {
        return PresentationTools.formatCurrency(getTotalValue());
    }

}
