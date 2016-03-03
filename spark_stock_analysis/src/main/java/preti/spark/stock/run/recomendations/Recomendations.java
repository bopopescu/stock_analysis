package preti.spark.stock.run.recomendations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import preti.spark.stock.model.Stock;
import preti.spark.stock.model.Trade;
import preti.spark.stock.run.DonchianParametersOptimizationResult;
import preti.spark.stock.run.StocksRepository;
import preti.spark.stock.run.model.ModelGeneration;
import preti.spark.stock.system.TradeSystem;
import preti.spark.stock.system.TradingStrategy;
import preti.spark.stock.system.TradingStrategyImpl;

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

		log.info("Loading stock data ...");
		StocksRepository stocksRepository = new StocksRepository(sc);
		List<Stock> stocks = stocksRepository.loadStocks(config.getStockHistoryFile(), config.getStockCodesToAnalyze());
		Map<String, Stock> stocksMap = new HashMap<>();
		for (Stock st : stocks) {
			stocksMap.put(st.getCode(), st);
		}

		Map<String, TradingStrategy> tradingStrategies = new HashMap<>();
		for (DonchianParametersOptimizationResult parameter : config.getModel()) {
			tradingStrategies.put(parameter.getStock(),
					new TradingStrategyImpl(stocksMap.get(parameter.getStock()), parameter.getEntryDonchianSize(),
							parameter.getExitDonchianSize(), config.getAccountInitialPosition(),
							parameter.getRiskRate()));
		}

		// TODO: Ter que repopular esses trades aqui não é um bom sinal, acho
		// que meu modelo não está bom
		// FIXME: rever isso aqui
		for (Trade t : config.getTrades()) {
			t.setStock(stocksMap.get(t.getStock().getCode()));
		}

		TradeSystem system = new TradeSystem(config.getTrades(), stocks, config.getAccountInitialPosition(),
				tradingStrategies);
		List<Trade> trades = system.analyzeStocks(config.getRecomendationDate());

		List<RecomendationResult> recomendations = new ArrayList<>();
		for (Trade t : trades) {
			recomendations.add(new RecomendationResult(t));
		}
		jsonMapper.writeValue(new File(config.getOutputFile()), recomendations);

	}

}
