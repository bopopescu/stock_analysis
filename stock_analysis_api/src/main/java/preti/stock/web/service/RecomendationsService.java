package preti.stock.web.service;

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
import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.StockDBEntity;
import preti.stock.db.model.TradeDBEntity;
import preti.stock.db.model.wrapper.AccountWrapper;
import preti.stock.system.Recomendation;
import preti.stock.system.dbimpl.DonchianStrategyDBImpl;
import preti.stock.system.dbimpl.StockDBImpl;
import preti.stock.system.dbimpl.TradeDBImpl;
import preti.stock.system.dbimpl.TradingSystemDBImpl;
import preti.stock.web.repository.DonchianModelRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class RecomendationsService {
    private Logger logger = LoggerFactory.getLogger(RecomendationsService.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private DonchianModelRepository modelRepository;
    @Autowired
    private TradeRepository tradeRepository;

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

        Date oldestOpenTradeBuyDate = tradeRepository.getOldestOpenTradeBuyDateForAccount(accountId);
        long diffBetweenDates = oldestOpenTradeBuyDate != null
                ? recomendationDate.getTime() - oldestOpenTradeBuyDate.getTime() : 0;
        int diffInDays = (int) TimeUnit.DAYS.convert(diffBetweenDates, TimeUnit.MILLISECONDS);

        return new DateTime(recomendationDate).minusDays(Math.max(diffInDays, maxDonchianChannelSize)).toDate();
    }

    private List<TradeDBImpl> generateTrades(AccountWrapper account, Map<Long, StockDBImpl> stocksMap) {
        List<TradeDBImpl> trades = new ArrayList<>();
        for (TradeDBEntity t : account.getWallet()) {
            trades.add(new TradeDBImpl(t, stocksMap.get(t.getStockId())));
        }
        return trades;
    }

    private Map<String, DonchianStrategyDBImpl> createTradingStrategies(AccountWrapper account,
            Map<Long, StockDBImpl> stocksMap) {
        Map<String, DonchianStrategyDBImpl> tradingStrategies = new HashMap<>();
        for (DonchianModel parameter : account.getModel()) {
            StockDBImpl st = stocksMap.get(parameter.getStockId());
            tradingStrategies.put(st.getCode(), new DonchianStrategyDBImpl(st, parameter.getId(),
                    parameter.getEntryDonchianSize(), parameter.getExitDonchianSize(), parameter.getRiskRate()));
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
