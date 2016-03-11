package preti.stock.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.stock.system.TradeSystem;
import preti.stock.system.TradingStrategy;
import preti.stock.system.TradingStrategyImpl;
import preti.stock.web.service.StocksService;

@RestController
public class RecomendationsController {
	private Logger logger = LoggerFactory.getLogger(RecomendationsController.class);
	private final String DATE_FORMAT = "yyyy-MM-dd";

	@Autowired
	private StocksService stocksService;

	public void setStocksService(StocksService stocksService) {
		this.stocksService = stocksService;
	}

	@RequestMapping(path = "/recomendations/generate", headers = "Accept=application/json")
	public Account generateRecomendations(@RequestBody Account account, @RequestParam String date)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date recomendationDate = dateFormat.parse(date);

		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(recomendationDate);
		beginDate.add(Calendar.MONTH, -6);
		logger.debug("Begin date is " + beginDate.getTime() + " end date is " + recomendationDate);

		List<Stock> stocks = stocksService.loadStocks(account.getStockCodesToAnalyze(), beginDate.getTime(),
				recomendationDate);

		Map<String, Stock> stocksMap = new HashMap<>();
		for (Stock st : stocks) {
			stocksMap.put(st.getCode(), st);
		}
		System.out.println("Stocks read.");

		Map<String, TradingStrategy> tradingStrategies = new HashMap<>();
		for (DonchianModel parameter : account.getModel()) {
			tradingStrategies.put(parameter.getStock(),
					new TradingStrategyImpl(stocksMap.get(parameter.getStock()), parameter.getEntryDonchianSize(),
							parameter.getExitDonchianSize(), account.getInitialPosition(), parameter.getRiskRate()));
		}

		// TODO: Ter que repopular esses trades aqui não é um bom sinal, acho
		// que meu modelo não está bom
		// FIXME: rever isso aqui
		for (Trade t : account.getWallet()) {
			t.setStock(stocksMap.get(t.getStockCode()));
		}
		TradeSystem system = new TradeSystem(account.getWallet(), stocks, tradingStrategies, account.getBalance());

		system.analyzeStocks(recomendationDate);

		account.setWallet(system.getAllOpenTrades());
		account.setBalance(system.getAccountBalance());

		return account;
	}
}
