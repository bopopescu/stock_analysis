package preti.stock.fe.location.recomendations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.coremodel.Order;
import preti.stock.coremodel.OrderType;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;
import preti.stock.coremodel.Trade;
import preti.stock.fe.facade.OrderFacade;
import preti.stock.fe.facade.RecomendationFacade;
import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.facade.StockFacade;
import preti.stock.fe.facade.TradeFacade;
import preti.stock.fe.location.OrderVO;

@Service
public class RecomendationsService {
    private Logger logger = LoggerFactory.getLogger(RecomendationsService.class);

    @Autowired
    private RecomendationFacade recomendationFacade;

    @Autowired
    private StockFacade stockFacade;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private TradeFacade tradeFacade;

    public RecomendationsVO generateRecomendations(long accountId, Date recomendationsDate) throws RemoteApiException {
        List<Order> recOrders = recomendationFacade.generateRecomendations(accountId, recomendationsDate);
        if (recOrders == null || recOrders.isEmpty()) {
            return new RecomendationsVO(accountId, new ArrayList<>());
        }

        // FIXME: 20 dias fixo
        Date initialHistoryDate = new DateTime(recomendationsDate.getTime()).minusDays(20).toDate();
        List<OrderVO> orders = new ArrayList<>();
        for (Order o : recOrders) {
            Stock stock = stockFacade.getStock(o.getStockId());
            StockHistory[] history = stockFacade.getStockHistory(o.getStockId(), initialHistoryDate,
                    recomendationsDate);
            stock.setHistory(Arrays.asList(history));
            switch (o.getType()) {
            case BUY:
                orders.add(new OrderVO(o.getOrderId(), o.getAccountId(), OrderType.BUY, o.getStockId(), stock.getCode(),
                        stock.getName(), o.getModelId(), o.getSize(), o.getCreationDate(), o.getValue(),
                        o.getStopPos()));
                break;
            case SELL:
                List<Trade> openTrades = tradeFacade.getOpenTrades(accountId, o.getStockId());
                Trade lastOpenTrade = openTrades.get(0);
                orders.add(new OrderVO(o.getOrderId(), o.getAccountId(), OrderType.SELL, o.getStockId(),
                        stock.getCode(), stock.getName(), o.getModelId(), o.getSize(), o.getCreationDate(),
                        o.getValue(), lastOpenTrade.getBuyValue(), lastOpenTrade.getBuyDate()));
                break;
            }
        }

        return new RecomendationsVO(accountId, orders);
    }

    public void createOrders(long accountId, List<OrderVO> ordersVO) {
        logger.info(String.format("Creating %s orders for account %s", ordersVO.size(), accountId));
        List<Order> orders = new ArrayList<>();
        for (OrderVO oVo : ordersVO) {
            switch (oVo.getType()) {
            case SELL:
                orders.add(Order.createSellOrder(oVo.getOrderId(), oVo.getAccountId(), oVo.getStockId(),
                        oVo.getModelId(), oVo.getSize(), oVo.getCreationDate(), oVo.getValue()));
                break;
            case BUY:
                orders.add(Order.createBuyOrder(oVo.getOrderId(), oVo.getAccountId(), oVo.getStockId(),
                        oVo.getModelId(), oVo.getSize(), oVo.getCreationDate(), oVo.getValue(), oVo.getStopPos()));
                break;
            }
        }
        orderFacade.createOrders(orders);
    }
}
