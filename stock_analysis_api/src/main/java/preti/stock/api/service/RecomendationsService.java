package preti.stock.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.api.model.db.OperationDBEntity;
import preti.stock.api.model.db.OrderDBEntity;
import preti.stock.api.model.db.StockDBEntity;
import preti.stock.api.model.db.wrapper.AccountWrapper;
import preti.stock.api.model.db.wrapper.TradeWrapper;
import preti.stock.api.model.trading.DonchianStrategyDBImpl;
import preti.stock.api.model.trading.StockDBImpl;
import preti.stock.api.model.trading.TradeDBImpl;
import preti.stock.api.model.trading.TradingSystemDBImpl;
import preti.stock.api.repository.DonchianModelRepository;
import preti.stock.api.repository.OperationRepository;
import preti.stock.system.Recomendation;

@Service
public class RecomendationsService {
    private Logger logger = LoggerFactory.getLogger(RecomendationsService.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private DonchianModelRepository modelRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private StocksService stocksService;

    public List<OrderDBEntity> generateRecomendations(long accountId, Date recomendationDate) {
        AccountWrapper account = accountService.loadCompleteAccount(accountId, recomendationDate);

        Date beginDate = identifyBeginDate(accountId, recomendationDate);
        logger.debug("Begin date is " + beginDate + " end date is " + recomendationDate);

        Map<Long, StockDBImpl> stocksMap = loadStocksMap(account.getStockCodesToAnalyze(), beginDate,
                recomendationDate);
        List<TradeDBImpl> trades = generateTrades(account, stocksMap);

        Map<String, DonchianStrategyDBImpl> tradingStrategies = createTradingStrategies(account, stocksMap);

        TradingSystemDBImpl system = new TradingSystemDBImpl(trades, stocksMap.values(), tradingStrategies,
                account.getTarget().getInitialPosition(), account.getTarget().getBalance());

        List<Recomendation<OrderDBEntity>> recomendations = system.analyze(recomendationDate);
        List<OrderDBEntity> ordersResult = new ArrayList<>();
        recomendations.forEach(rec -> ordersResult.add(rec.getTarget()));

        return ordersResult;

    }

    private Date identifyBeginDate(long accountId, Date recomendationDate) {
        int maxDonchianChannelSize = modelRepository.getMaxDonchianChannelSize();

        Date oldestOpenTradeBuyDate = operationRepository.getOldestOpenOperationForAccount(accountId);
        long diffBetweenDates = oldestOpenTradeBuyDate != null
                ? recomendationDate.getTime() - oldestOpenTradeBuyDate.getTime() : 0;
        int diffInDays = (int) TimeUnit.DAYS.convert(diffBetweenDates, TimeUnit.MILLISECONDS);

        return new DateTime(recomendationDate).minusDays(Math.max(diffInDays, maxDonchianChannelSize)).toDate();
    }

    private List<TradeDBImpl> generateTrades(AccountWrapper account, Map<Long, StockDBImpl> stocksMap) {
        List<OperationDBEntity> openOperations = operationRepository.getOpenOperations(account.getTarget().getId());

        List<TradeDBImpl> trades = new ArrayList<>();
        for (OperationDBEntity op : openOperations) {
            TradeWrapper wrapper = new TradeWrapper(op, null);
            long stockId = operationRepository.getStockIdForOperation(op.getOperationId());
            TradeDBImpl t = new TradeDBImpl(wrapper, stocksMap.get(stockId));
            trades.add(t);
        }

        return trades;
    }

    private Map<String, DonchianStrategyDBImpl> createTradingStrategies(AccountWrapper account,
            Map<Long, StockDBImpl> stocksMap) {
        Map<String, DonchianStrategyDBImpl> tradingStrategies = new HashMap<>();
        for (DonchianModel parameter : account.getModel()) {
            StockDBImpl st = stocksMap.get(parameter.getStockId());
            tradingStrategies.put(st.getCode(), new DonchianStrategyDBImpl(st, parameter.getId()));
        }
        return tradingStrategies;
    }

    private Map<Long, StockDBImpl> loadStocksMap(List<String> stockCodes, Date beginDate, Date endDate) {
        List<StockDBEntity> dbStockEntities = stocksService.loadStocks(stockCodes);
        Map<Long, StockDBImpl> stocksMap = new HashMap<>();
        for (StockDBEntity st : dbStockEntities) {
            StockDBImpl stImpl = new StockDBImpl(st);
            stImpl.setHistory(stocksService.getStockHistory(st.getId(), beginDate, endDate));
            stocksMap.put(st.getId(), stImpl);
        }
        return stocksMap;
    }
}
