package preti.stock.web.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.stock.system.TradeSystem;
import preti.stock.system.TradingStrategy;
import preti.stock.system.TradingStrategyImpl;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.DonchianModelRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class RecomendationsService {
	private Logger logger = LoggerFactory.getLogger(RecomendationsService.class);

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private DonchianModelRepository modelRepository;
	@Autowired
	private TradeRepository tradeRepository;

	@Autowired
	private StocksService stocksService;

	public List<Trade> generateRecomendations(long accountId, Date recomendationDate) {
		Account account = loadCompleteAccount(accountId, recomendationDate);

		// FIXME: preciso encontrar uma forma mais inteligente de identificar a
		// data de inicio da procura. Preciso levar em consideração o tempo
		// necessário para o algoritmo de análise (os canais de Donchian) e a
		// data de compra do Trade mais antigo do Account
		DateTime beginDate = new DateTime(recomendationDate);
		beginDate = beginDate.minusMonths(12);
		logger.debug("Begin date is " + beginDate.toDate() + " end date is " + recomendationDate);

		Map<Long, Stock> stocksMap = loadStocksMap(account.getStockCodesToAnalyze(), beginDate.toDate(),
				recomendationDate);

		Map<Long, TradingStrategy> tradingStrategies = createTradingStrategies(account, stocksMap);

		// FIXME: rever isso aqui
		populateTradesWithStocks(account, stocksMap);
		TradeSystem system = new TradeSystem(account.getWallet(), stocksMap.values(), tradingStrategies,
				account.getBalance());

		return system.analyzeStocks(recomendationDate);

	}

	private void populateTradesWithStocks(Account account, Map<Long, Stock> stocksMap) {
		for (Trade t : account.getWallet()) {
			t.applyStock(stocksMap.get(t.getStockId()));
		}
	}

	private Map<Long, TradingStrategy> createTradingStrategies(Account account, Map<Long, Stock> stocksMap) {
		Map<Long, TradingStrategy> tradingStrategies = new HashMap<>();
		for (DonchianModel parameter : account.getModel()) {
			tradingStrategies.put(parameter.getStockId(),
					new TradingStrategyImpl(stocksMap.get(parameter.getStockId()), parameter.getId(),
							parameter.getEntryDonchianSize(), parameter.getExitDonchianSize(),
							parameter.getRiskRate()));
		}
		return tradingStrategies;
	}

	private Map<Long, Stock> loadStocksMap(List<String> stockCodes, Date beginDate, Date endDate) {
		List<Stock> stocks = stocksService.loadStocks(stockCodes, beginDate, endDate);
		Map<Long, Stock> stocksMap = new HashMap<>();
		for (Stock st : stocks) {
			stocksMap.put(st.getId(), st);
		}
		return stocksMap;
	}

	private Account loadCompleteAccount(long accountId, Date recomendationDate) {
		Account account = accountRepository.getAccount(accountId);
		account.setModel(modelRepository.getActiveModel(accountId, recomendationDate));
		account.setStockCodesToAnalyze(accountRepository.getStocksToAnalyse(accountId));
		account.setWallet(tradeRepository.getTrades(accountId));

		return account;
	}
}
