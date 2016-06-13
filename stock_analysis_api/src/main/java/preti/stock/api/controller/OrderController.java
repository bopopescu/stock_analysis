package preti.stock.api.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.api.exception.ApiValidationException;
import preti.stock.api.model.db.OperationDBEntity;
import preti.stock.api.model.db.OrderDBEntity;
import preti.stock.api.service.OrderService;
import preti.stock.api.service.TradeService;
import preti.stock.client.OrderExecutionData;
import preti.stock.client.model.Operation;
import preti.stock.client.model.OperationType;
import preti.stock.client.model.Order;

@RestController
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    @RequestMapping(path = "/order/create", headers = "Accept=application/json")
    public List<Order> createOrders(@RequestBody List<OrderDBEntity> orders) {

        List<OrderDBEntity> dbOrders = orderService.createOrders(orders);
        List<Order> result = new ArrayList<>();
        dbOrders.forEach(o -> result.add(new Order(o.getOrderId(), OperationType.getFromValue(o.getType().type),
                o.getStockId(), o.getModelId(), o.getSize(), o.getCreationDate(), o.getValue(), o.getStopPos())));
        return result;
    }

    @RequestMapping(path = "/order/execute", headers = "Accept=application/json")
    public List<Operation> executeOrders(@RequestBody List<OrderExecutionData> ordersExecData,
            @RequestParam(name = "accountId", required = true) long accountId)
            throws ParseException, ApiValidationException {
        logger.info(String.format("Executing orders for accountId=%s", accountId));

        List<OperationDBEntity> operations = tradeService.executeOrders(accountId, ordersExecData);
        List<Operation> result = new ArrayList<>();
        operations.forEach(op -> result.add(new Operation(op.getOperationId(), op.getOrderId(), op.getCreationDate(),
                op.getSize(), op.getValue(), op.getStopLoss())));

        return result;
    }

    @RequestMapping(path = "/order/getOpen", headers = "Accept=application/json", method = RequestMethod.GET)
    public List<OrderDBEntity> getAllOpenOrders(@RequestParam(name = "accountId", required = true) long accountId) {
        return orderService.getAllOpenOrders(accountId);
    }

    // @RequestMapping(path = "/trade/realize", headers = "Accept=application/json")
    // public void realizetrades(@RequestBody List<Trade> trades,
    // @RequestParam(name = "accountId", required = true) long accountId) throws ApiValidationException {
    // logger.info(String.format("Realizing trades for account [%s]: %s ", accountId, trades));
    //
    // tradeService.realizeTrades(accountId, trades);
    // }

}
