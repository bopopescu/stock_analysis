package preti.stock.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.coremodel.BuyOrder;
import preti.stock.coremodel.Order;
import preti.stock.coremodel.SellOrder;
import preti.stock.coremodel.Trade;
import preti.stock.web.ApiError;
import preti.stock.web.exception.ApiValidationException;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.OrderRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ApiValidationException.class)
    public List<Trade> executeOrders(long accountId, Date executionDate, List<Order> orders)
            throws ApiValidationException {
        List<Trade> trades = new ArrayList<>();

        double balanceChange = 0;
        for (Order order : orders) {
            Trade t;
            switch (order.getType()) {
            case BUY:
                t = executeBuyOrder(accountId, order.getOrderId(), executionDate);
                balanceChange -= t.getSize() * t.getBuyValue();
                break;
            case SELL:
                t = executeSellOrder(accountId, order.getOrderId(), executionDate);
                balanceChange += t.getSize() * t.getSellValue();
                break;
            default:
                throw new RuntimeException(String.format("Invalid order type: %s ", order.getType()));
            }
            trades.add(t);
        }

        Account account = accountRepository.getAccount(accountId);
        if (account.getBalance() + balanceChange < 0) {
            throw new ApiValidationException(ApiError.TRADE_INSUFICIENT_BALANCE);
        }

        accountRepository.updateBalance(accountId, balanceChange);

        return trades;
    }

    public Trade executeBuyOrder(long accountId, long orderId, Date executionDate) throws ApiValidationException {
        BuyOrder order = orderRepository.getBuyOrder(orderId);

        Trade t = new Trade(order.getStockId(), accountId, order.getSize(), order.getStopPos(), order.getOrderId(),
                executionDate, order.getValue());
        validateNewTrade(accountId, t);
        long tradeId = tradeRepository.createTrade(t);

        return tradeRepository.getTrade(tradeId);
    }

    public Trade executeSellOrder(long accountId, long orderId, Date executionDate) throws ApiValidationException {
        SellOrder order = orderRepository.getSellOrder(orderId);
        Trade t = tradeRepository.getOpenTradeForSellOrder(orderId);
        validateExistentTrade(t.getId());
        tradeRepository.closeTrade(t.getId(), executionDate, order.getValue(), order.getOrderId());
        return tradeRepository.getTrade(t.getId());
    }

    private void validateNewTrade(long accountId, Trade t) throws ApiValidationException {
        List<Trade> existentTrades = tradeRepository.getOpenTrades(accountId, t.getStockId());
        if (!existentTrades.isEmpty()) {
            throw new ApiValidationException(ApiError.TRADE_ALREADY_OPEN);
        }
    }

    private void validateExistentTrade(long tradeId) throws ApiValidationException {
        Trade existentTrade = tradeRepository.getTrade(tradeId);
        if (existentTrade == null)
            throw new ApiValidationException(ApiError.TRADE_NOT_FOUND);

        if (!existentTrade.isOpen())
            throw new ApiValidationException(ApiError.TRADE_ALREADY_CLOSED);
    }

    public List<Trade> getOpenTrades(long accountId, long stockId) {
        return tradeRepository.getOpenTrades(accountId, stockId);
    }
}
