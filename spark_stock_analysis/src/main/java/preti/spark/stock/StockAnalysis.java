package preti.spark.stock;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import preti.spark.stock.model.Stock;
import preti.spark.stock.model.StockHistory;
import preti.spark.stock.reporting.BalanceReport;
import preti.spark.stock.reporting.OperationsReport;
import preti.spark.stock.reporting.StockReport;
import preti.spark.stock.system.ConfigContext;
import preti.spark.stock.system.TradeSystem;
import preti.spark.stock.system.TradingStrategy;
import preti.spark.stock.system.TradingStrategyImpl;
import scala.Tuple2;

public class StockAnalysis {
	private static final Log log = LogFactory.getLog(StockAnalysis.class);
	private static JavaSparkContext sc;

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 1) {
			System.out.println("Must specify config file");
			System.exit(-1);
		}
		
		ConfigContext configContext = new ObjectMapper().readValue(new File(args[0]), ConfigContext.class);
		
		SparkConf conf = new SparkConf();
		sc = new JavaSparkContext(conf);

		JavaRDD<InputDataEntry> inputData = sc.textFile(configContext.getStockHistoryFile())
				.filter(s -> !s.trim().isEmpty()).map(InputDataEntry::parseFromLine);
		inputData.persist(StorageLevel.MEMORY_ONLY());

		// List<String> stockCodes = sc.textFile(stockFilterFile).filter(s ->
		// !s.trim().isEmpty()).collect();
		List<String> stockCodes = configContext.getStockCodesToAnalyze();

		List<Stock> stocks = new ArrayList<>();

		for (String stockCode : stockCodes) {
			List<InputDataEntry> stockEntries = inputData.filter(sd -> sd.getCode().equals(stockCode)).collect();
			if (stockEntries.isEmpty())
				continue;

			Stock stock = new Stock(stockCode);
			stocks.add(stock);
			for (InputDataEntry data : stockEntries) {
				stock.addHistory(new StockHistory(data.getDate(), data.getHigh(), data.getLow(), data.getClose(),
						data.getVolume()));
			}
		}

		final double accountInitialPosition = configContext.getAccountInitialValue();
		final int trainingSize = configContext.getTrainingSizeInMonths();
		final int windowSize = configContext.getWindowSizeInMonths();

		Date initialDate = configContext.getInitialDate();
		Date finalDate = configContext.getFinalDate();
		TradeSystem system = new TradeSystem(stocks, accountInitialPosition, null);

		DateTime currentInitialDate = new DateTime(initialDate.getTime()).plusMonths(trainingSize);
		DateTime currentFinalDate = currentInitialDate.plusMonths(windowSize);
		Map<String, TradingStrategy> optimzedStrategies = new HashMap<>();
		while (currentFinalDate.isBefore(finalDate.getTime() + 1)) {
			Map<String, TradingStrategy> newStrategies = new HashMap<>();
			for (Stock s : stocks) {
				TradingStrategy strategy = optimizeParameters(s, accountInitialPosition,
						currentInitialDate.minusMonths(trainingSize).toDate(), currentInitialDate.minusDays(1).toDate(),
						configContext);
				if (strategy != null) {
					newStrategies.put(s.getCode(), strategy);
				}
			}
			log.info("Analyzing from " + currentInitialDate + " to " + currentFinalDate + " with training data from "
					+ currentInitialDate.minusMonths(trainingSize) + " to " + currentInitialDate.minusDays(1));
			optimzedStrategies = mergeStrategies(optimzedStrategies, newStrategies);

			system.setTradingStrategies(optimzedStrategies);
			system.analyzeStocks(currentInitialDate.toDate(), currentFinalDate.toDate());
			log.info("Analyze finished.");

			currentInitialDate = currentInitialDate.plusMonths(windowSize);
			currentFinalDate = currentFinalDate.plusMonths(windowSize);
		}
		system.closeAllOpenTrades(finalDate);
		System.out.println("Final balance: " + system.getAccountBalance());

		log.info("Generating reports ...");
		new StockReport(system, "localhost", 5001, "stock").generate();
		new OperationsReport(system, "localhost", 5002, "operations").generate();
		new BalanceReport(system, "localhost", 5003, "balance").generate();
		log.info("Reports generated");

	}

	private static Map<String, TradingStrategy> mergeStrategies(Map<String, TradingStrategy> oldStrategies,
			Map<String, TradingStrategy> newStrategies) {
		Map<String, TradingStrategy> mergedStrategies = new HashMap<>(newStrategies);

		for (String code : oldStrategies.keySet()) {
			if (!newStrategies.containsKey(code)) {
				TradingStrategyImpl oldStrategy = (TradingStrategyImpl) oldStrategies.get(code);
				mergedStrategies.put(code,
						new TradingStrategyImpl(oldStrategy.getStock(), 0, oldStrategy.getExitDonchianSize(),
								oldStrategy.getAccountInitialPosition(), oldStrategy.getRiskRate()));
			}
		}

		return mergedStrategies;
	}

	private static TradingStrategy optimizeParameters(Stock stock, double initialPosition, Date initialDate,
			Date finalDate, ConfigContext configContext) {
		List<Integer> entryDonchianSizes = configContext.getEntryDonchianSizes();
		List<Integer> exitDonchianSizes = configContext.getExitDonchianSizes();

		final int NO_ENTRY_FOUND = -1;

		JavaRDD<Integer> entryDonchianRDD = sc.parallelize(entryDonchianSizes);
		Map<Integer, Number[]> gains = entryDonchianRDD.mapToPair(entryDonchianSize -> {
			double bestGain = 0;
			int selectedExitSize = NO_ENTRY_FOUND;
			for (int exitDonchianSize : exitDonchianSizes) {
				TradingStrategy strategy = new TradingStrategyImpl(stock, entryDonchianSize, exitDonchianSize,
						initialPosition, configContext.getRiskRate());
				TradeSystem system = new TradeSystem(stock, initialPosition, strategy);
				system.analyzeStocks(initialDate, finalDate);
				system.closeAllOpenTrades(finalDate);

				double currentGain = system.getAccountBalance() - system.getAccountInitialPosition();
				if (currentGain > bestGain) {
					bestGain = currentGain;
					selectedExitSize = exitDonchianSize;
				}
			}
			return new Tuple2<>(entryDonchianSize, new Number[] { selectedExitSize, bestGain });
		}).collectAsMap();

		// Keeps ascending order when evaluating the entry sizes for the
		// Donchian Channel, so that I keep the results the same I got using the
		// R version.
		Integer[] entrySizes = gains.keySet().toArray(new Integer[] {});
		Arrays.sort(entrySizes);

		// find the best gain
		double bestGain = 0;
		int selectedEntry = 0;
		for (int entrySize : entrySizes) {
			Number[] entrySizeResult = gains.get(entrySize);
			if (entrySizeResult[1].doubleValue() > bestGain) {
				bestGain = entrySizeResult[1].doubleValue();
				selectedEntry = entrySize;
			}

		}
		if (selectedEntry == 0) {
			log.info(String.format("Optimization for stock %s initial date %s gain %s	 is null", stock.getCode(),
					initialDate, bestGain));
			return null;
		}

		int selectedExit = gains.get(selectedEntry)[0].intValue();
		log.info(String.format("Stock %s initial date %s entry size %s exit size	 %s gain %s", stock.getCode(),
				initialDate, selectedEntry, selectedExit, bestGain));

		// Verify if a positive result was found
		if (selectedExit != NO_ENTRY_FOUND)
			return new TradingStrategyImpl(stock, selectedEntry, selectedExit, initialPosition,
					configContext.getRiskRate());
		else
			return null;
	}
}
