package preti.stock.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.db.model.AccountDBEntity;
import preti.stock.db.model.OperationDBEntity;
import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.OrderExecutionData;
import preti.stock.db.model.WalletDBEntity;
import preti.stock.web.ApiError;
import preti.stock.web.exception.ApiValidationException;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.OperationRepository;
import preti.stock.web.repository.OrderRepository;
import preti.stock.web.repository.WalletRepository;

@Service
public class TradeService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ApiValidationException.class)
    public List<OperationDBEntity> executeOrders(long accountId, List<OrderExecutionData> ordersExecData)
            throws ApiValidationException {
        List<OperationDBEntity> operations = new ArrayList<>();

        double balanceChange = 0;
        for (OrderExecutionData orderData : ordersExecData) {
            OperationDBEntity op;
            OrderDBEntity dbOrder = orderRepository.getOrder(orderData.getOrderId());
            switch (dbOrder.getType()) {
            case BUY:
                op = executeBuyOrder(accountId, dbOrder, orderData.getExecutionDate(), orderData.getExecutionValue());
                balanceChange -= op.getSize() * op.getValue();
                break;
            case SELL:
                op = executeSellOrder(accountId, dbOrder, orderData.getExecutionDate(), orderData.getExecutionValue());
                balanceChange += op.getSize() * op.getValue();
                break;
            default:
                throw new RuntimeException(String.format("Invalid order type: %s ", dbOrder.getType()));
            }
            operations.add(op);
        }

        AccountDBEntity account = accountRepository.getAccount(accountId);
//        if (account.getBalance() + balanceChange < 0) {
//            throw new ApiValidationException(ApiError.TRADE_INSUFICIENT_BALANCE);
//        }

        accountRepository.updateBalance(accountId, balanceChange);

        return operations;
    }

    public OperationDBEntity executeBuyOrder(long accountId, OrderDBEntity order, Date executionDate,
            double executionValue) throws ApiValidationException {
        if (!order.isBuyOrder())
            throw new IllegalArgumentException(String.format("Can't execute buy order: order %s is of type %s",
                    order.getOrderId(), order.getType()));

        validateNewTrade(accountId, order.getStockId());

        WalletDBEntity wallet = getOrCreateWallet(accountId, order.getStockId());
        OperationDBEntity op = new OperationDBEntity(order.getOrderId(), executionDate, order.getSize(), executionValue,
                order.getStopPos());
        long opId = operationRepository.createOperation(op);
        op = operationRepository.getOperation(opId);

        wallet.incrementSize(op.getSize());
        walletRepository.updateWalletSize(wallet.getWalletId(), wallet.getSize());

        return op;
    }

    private WalletDBEntity getOrCreateWallet(long accountId, long stockId) {
        WalletDBEntity w = walletRepository.getWalletForAccountAndStock(accountId, stockId);
        if (w != null)
            return w;

        Date now = new Date();
        long walletId = walletRepository.createWallet(new WalletDBEntity(stockId, accountId, 0, now, now));
        return walletRepository.getWallet(walletId);
    }

    public OperationDBEntity executeSellOrder(long accountId, OrderDBEntity order, Date executionDate,
            double executionValue) throws ApiValidationException {
        if (!order.isSellOrder())
            throw new IllegalArgumentException(String.format("Can't execute sell order: order %s is of type %s",
                    order.getOrderId(), order.getType()));
        // Validação
        List<OperationDBEntity> openOperations = operationRepository.getOpenOperations(accountId, order.getStockId());
        if (openOperations.size() != 1)
            throw new IllegalStateException(String.format("Illegal operations state for account %s and stock %s",
                    accountId, order.getStockId()));
        OperationDBEntity op = openOperations.get(0);
        if (op.getSize() != order.getSize())
            throw new IllegalStateException(String.format(
                    "Illegal operations state for account %s and stock %s: size of order %s and operation %s does not match ",
                    accountId, order.getStockId(), order.getModelId(), order.getOrderId()));

        // Execução da ordem;
        WalletDBEntity wallet = walletRepository.getWalletForAccountAndStock(accountId, order.getStockId());
        op = new OperationDBEntity(order.getOrderId(), executionDate, order.getSize(), executionValue,
                order.getStopPos());
        long opId = operationRepository.createOperation(op);
        op = operationRepository.getOperation(opId);
        wallet.decrementSize(op.getSize());
        walletRepository.updateWalletSize(wallet.getWalletId(), wallet.getSize());

        return op;

    }

    private void validateNewTrade(long accountId, long stockId) throws ApiValidationException {
        List<OperationDBEntity> existentOperations = operationRepository.getOpenOperations(accountId, stockId);
        if (!existentOperations.isEmpty()) {
            throw new ApiValidationException(ApiError.TRADE_ALREADY_OPEN);
        }
    }

    public List<OperationDBEntity> getOpenTrades(long accountId, long stockId) {
        return operationRepository.getOpenOperations(accountId, stockId);
    }
}
