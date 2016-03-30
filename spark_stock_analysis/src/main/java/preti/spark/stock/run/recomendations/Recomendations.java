package preti.spark.stock.run.recomendations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import preti.spark.stock.run.StocksRepository;
import preti.spark.stock.run.model.ModelGeneration;
import preti.stock.analysismodel.donchian.Account;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.spark.stock.system.StockContext;
import preti.spark.stock.system.TradeSystemExecution;
import preti.stock.system.TradeSystem;
import preti.stock.system.TradingStrategy;
import preti.stock.system.TradingStrategyImpl;

public class Recomendations {
	private static final Log log = LogFactory.getLog(ModelGeneration.class);
	private static JavaSparkContext sc;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if (args.length != 1) {
			System.out.println("Must specify config file");
			System.exit(-1);
		}

		ObjectMapper jsonMapper = new ObjectMapper();

		RecomendationsConfig config = jsonMapper.readValue(new File(args[0]), RecomendationsConfig.class);

		SparkConf conf = new SparkConf();
		sc = new JavaSparkContext(conf);

		Account account = config.getAccount();

		log.info("Loading stock data ...");
		StocksRepository stocksRepository = new StocksRepository(sc);
		List<Stock> stocks = stocksRepository.loadStocks(config.getStockHistoryFile(),
				account.getStockCodesToAnalyze());
		Map<String, Stock> stocksMap = new HashMap<>();
		for (Stock st : stocks) {
			stocksMap.put(st.getCode(), st);
		}

		Map<Long, TradingStrategy> tradingStrategies = new HashMap<>();
		for (DonchianModel parameter : account.getModel()) {
			tradingStrategies.put(parameter.getStockId(), new TradingStrategyImpl(stocksMap.get(parameter.getStockId()), 0,
					parameter.getEntryDonchianSize(), parameter.getExitDonchianSize(), parameter.getRiskRate()));
		}

		// TODO: Ter que repopular esses trades aqui não é um bom sinal, acho
		// que meu modelo não está bom
		// FIXME: rever isso aqui
		for (Trade t : account.getWallet()) {
			t.applyStock(stocksMap.get(t.getStock().getCode()));
		}

		// TradeSystemExecution system = new
		// TradeSystemExecution(account.getWallet(), stocks,
		// account.getInitialPosition(),
		// account.getBalance(), tradingStrategies);
		// system.analyzeStocks(config.getParsedRecomendationDate());
		//
		// account.setWallet(system.getWallet());
		// account.setBalance(system.getAccountBalance());
		// jsonMapper.writeValue(new File(config.getOutputFile()), account);

	}

}
