package preti.stock.system.dbimpl;

import java.util.Date;

import preti.stock.db.model.StockDBEntity;
import preti.stock.db.model.TradeDBEntity;
import preti.stock.system.Stock;
import preti.stock.system.Trade;

public class TradeDBImpl implements Trade<TradeDBEntity> {
    private TradeDBEntity target;
    private Stock<StockDBEntity> stock;

    public TradeDBImpl(TradeDBEntity t, Stock<StockDBEntity> s) {
        this.target = t;
        this.stock = s;
    }

    public TradeDBImpl(long id, long stockId, double size, double stopPos, long buyOrderId, long sellOrderId,
            Date buyDate, Date sellDate, double buyValue, double sellValue) {
        this.target = new TradeDBEntity(id, stockId, size, stopPos, buyOrderId, sellOrderId, buyDate, sellDate,
                buyValue, sellValue);
    }

    public void setStock(StockDBImpl stock) {
        this.stock = stock;
    }

    @Override
    public boolean isOpen() {
        return target.getSellOrderId() == 0;
    }

    @Override
    public double getStopLossPosition() {
        return target.getStopPos();
    }

    @Override
    public Stock<StockDBEntity> getStock() {
        return stock;
    }

    @Override
    public double getTotalValue(Date date) {
        return stock.getCloseValueAtDate(date) * getSize();
    }

    @Override
    public double getSize() {
        return target.getSize();
    }

    @Override
    public TradeDBEntity getTarget() {
        return target;
    }

    @Override
    public double getProfit(Date date) {
        if (target.getBuyDate() == null) {
            throw new IllegalArgumentException("Trade invalid: buyDate is empty.");
        }

        if (date == null && isOpen()) {
            throw new IllegalArgumentException("Trade not closed and no date informed to calculate profits.");
        }

        double buyValue = target.getBuyValue();
        double size = getSize();
        if (date != null) {
            return size * (stock.getCloseValueAtDate(date) - buyValue);
        } else {
            return size * (target.getSellValue() - buyValue);
        }
    }

    @Override
    public boolean isProfitable(Date d) {
        return getProfit(d) > 0;
    }

}
