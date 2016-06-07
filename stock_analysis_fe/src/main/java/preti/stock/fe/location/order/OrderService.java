package preti.stock.fe.location.order;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.coremodel.Order;
import preti.stock.coremodel.OrderExecutionData;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.stock.fe.facade.OrderFacade;
import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.facade.StockFacade;
import preti.stock.fe.location.OrderVO;

@Service
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private StockFacade stockFacade;

    public List<OrderVO> getAllOpenOrders(long accountId) throws RemoteApiException {
        logger.info(String.format("Finding open orders for account %s", accountId));
        List<Order> orders = orderFacade.getAllOpenOrders(accountId);
        logger.info(String.format("Found %s open orders for account %s", orders.size(), accountId));

        List<OrderVO> ordersVO = new ArrayList<>();
        for (Order o : orders) {
            Stock stock = stockFacade.getStock(o.getStockId());
            ordersVO.add(new OrderVO(o.getOrderId(), o.getType(), o.getStockId(), stock.getCode(),
                    stock.getName(), o.getModelId(), o.getSize(), o.getCreationDate(), o.getValue(), o.getStopPos()));
        }

        return ordersVO;
    }

    public List<Trade> executeOrders(List<OrderVO> ordersVO, long accountId) throws RemoteApiException {
        logger.info(String.format("Execution %s orders for account %s", ordersVO.size(), accountId));
        
        List<OrderExecutionData> ordersExecData = new ArrayList<>();
        for(OrderVO o : ordersVO) {
            ordersExecData.add(new OrderExecutionData(o.getOrderId(), o.getDate(), o.getValue()));
        }
        return orderFacade.executeOrders(ordersExecData, accountId);
    }

}
