package preti.stock.api.model.trading;

import java.util.Date;

import preti.stock.api.model.db.StockDBEntity;
import preti.stock.api.model.db.wrapper.TradeWrapper;
import preti.stock.system.Stock;
import preti.stock.system.Trade;

public class TradeDBImpl implements Trade<TradeWrapper> {
    private TradeWrapper target;
    private Stock<StockDBEntity> stock;

    public TradeDBImpl(TradeWrapper t, Stock<StockDBEntity> s) {
        this.target = t;
        this.stock = s;
    }

    public void setStock(StockDBImpl stock) {
        this.stock = stock;
    }

    @Override
    public boolean isOpen() {
        return target.getSellOp() == null;
    }

    @Override
    public double getStopLossPosition() {
        return target.getBuyOp().getStopLoss();
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
        return target.getBuyOp().getSize();
    }

    @Override
    public TradeWrapper getTarget() {
        return target;
    }

    @Override
    public double getProfit(Date date) {

        double buyValue = target.getBuyOp().getValue();
        double size = getSize();
        if (date != null) {
            return size * (stock.getCloseValueAtDate(date) - buyValue);
        } else {
            return size * (target.getSellOp().getValue() - buyValue);
        }
    }

    @Override
    public boolean isProfitable(Date d) {
        return getProfit(d) > 0;
    }

}
