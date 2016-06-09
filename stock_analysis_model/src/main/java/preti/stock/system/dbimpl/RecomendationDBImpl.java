package preti.stock.system.dbimpl;

import java.util.Date;

import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.OperationType;
import preti.stock.db.model.StockDBEntity;
import preti.stock.system.Recomendation;
import preti.stock.system.Stock;

public class RecomendationDBImpl implements Recomendation<OrderDBEntity> {
    private OrderDBEntity target;

    public RecomendationDBImpl(OrderDBEntity order) {
        this.target = order;
    }

    public static RecomendationDBImpl createSellRecomendation(Stock<StockDBEntity> stock, long modelId, double size,
            Date date, double value) {
        return new RecomendationDBImpl(
                new OrderDBEntity(OperationType.SELL, stock.getTarget().getId(), modelId, size, date, value, 0));
    }

    public static RecomendationDBImpl createBuyRecomendation(Stock<StockDBEntity> stock, long modelId, double size,
            Date date, double value, double stopPos) {
        return new RecomendationDBImpl(new OrderDBEntity(OperationType.BUY, stock.getTarget().getId(), modelId,
                size, date, value, stopPos));
    }

    public OrderDBEntity getTarget() {
        return target;
    }

    @Override
    public double getSize() {
        return target.getSize();
    }

    @Override
    public double getValue() {
        return target.getValue();
    }

}
