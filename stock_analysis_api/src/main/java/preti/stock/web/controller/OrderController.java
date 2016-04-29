package preti.stock.web.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.coremodel.Order;
import preti.stock.coremodel.Trade;
import preti.stock.web.exception.ApiValidationException;
import preti.stock.web.service.OrderService;
import preti.stock.web.service.TradeService;

@RestController
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    @RequestMapping(path = "/order/create", headers = "Accept=application/json")
    public List<Order> createOrders(@RequestBody List<Order> orders) {
        return orderService.createOrders(orders);
    }

    @RequestMapping(path = "/order/execute", headers = "Accept=application/json")
    public List<Trade> executeOrders(@RequestBody List<Order> orders,
            @RequestParam(name = "executionDate", required = true) String executionDate,
            @RequestParam(name = "accountId", required = true) long accountId)
            throws ParseException, ApiValidationException {
        logger.info(String.format("Executing orders accountId=%s executionDate=%s", accountId, executionDate));
        return tradeService.executeOrders(accountId, ControllerTools.parseDate(executionDate), orders);
    }

    // @RequestMapping(path = "/trade/realize", headers = "Accept=application/json")
    // public void realizetrades(@RequestBody List<Trade> trades,
    // @RequestParam(name = "accountId", required = true) long accountId) throws ApiValidationException {
    // logger.info(String.format("Realizing trades for account [%s]: %s ", accountId, trades));
    //
    // tradeService.realizeTrades(accountId, trades);
    // }

}
