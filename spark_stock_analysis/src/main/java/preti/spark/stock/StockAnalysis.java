package preti.spark.stock;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.joda.time.DateTime;

import preti.spark.stock.model.Stock;
import preti.spark.stock.model.StockHistory;
import preti.spark.stock.reporting.AggregatedReport;
import preti.spark.stock.reporting.BalanceReport;
import preti.spark.stock.reporting.OperationsReport;
import preti.spark.stock.reporting.StockReport;
import preti.spark.stock.system.TradeSystem;
import preti.spark.stock.system.TradingStrategy;
import preti.spark.stock.system.TradingStrategyImpl;
import scala.Tuple2;

public class StockAnalysis {
	private static final Log log = LogFactory.getLog(StockAnalysis.class);
	private static JavaSparkContext sc;

	public static void main(String[] args) throws IOException, ParseException {
		SparkConf conf = new SparkConf();
		sc = new JavaSparkContext(conf);

		// Load the text file into Spark.
		if (args.length < 2) {
			System.out.println("Must specify input files");
			System.exit(-1);
		}
		String dataFile = args[0];
		String stockFilterFile = args[1];

		JavaRDD<InputDataEntry> inputData = sc.textFile(dataFile).filter(s -> !s.trim().isEmpty())
				.map(InputDataEntry::parseFromLine);
		inputData.persist(StorageLevel.MEMORY_ONLY());

		List<String> stockCodes = sc.textFile(stockFilterFile).filter(s -> !s.trim().isEmpty()).collect();
		// List<String> stockCodes = inputData.map(sd ->
		// sd.getCode()).distinct().collect();

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

		final double accountInitialPosition = 10000;
		Date initialDate = new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01");
		Date finalDate = new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01");
		TradeSystem system = new TradeSystem(stocks, accountInitialPosition, null);

		DateTime currentInitialDate = new DateTime(initialDate.getTime()).plusMonths(1);
		DateTime currentFinalDate = currentInitialDate.plusMonths(1);
		Map<String, TradingStrategy> optimzedStrategies = new HashMap<>();
		while (currentFinalDate.isBefore(finalDate.getTime() + 1)) {
			Map<String, TradingStrategy> newStrategies = new HashMap<>();
			for (Stock s : stocks) {
				TradingStrategy strategy = optimizeParameters(s, accountInitialPosition,
						currentInitialDate.minusMonths(1).toDate(), currentInitialDate.minusDays(1).toDate());
				if (strategy != null) {
					newStrategies.put(s.getCode(), strategy);
				}
			}
			log.info("Analyzing from " + currentInitialDate + " to " + currentFinalDate + " with training data from "
					+ currentInitialDate.minusMonths(1) + " to " + currentInitialDate.minusDays(1));
			optimzedStrategies = mergeStrategies(optimzedStrategies, newStrategies);

			system.setTradingStrategies(optimzedStrategies);
			system.analyzeStocks(currentInitialDate.toDate(), currentFinalDate.toDate());
			log.info("Analyze finished.");

			currentInitialDate = currentInitialDate.plusMonths(1);
			currentFinalDate = currentFinalDate.plusMonths(1);
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
				mergedStrategies.put(code, new TradingStrategyImpl(oldStrategy.getStock(), 0,
						oldStrategy.getExitDonchianSize(), oldStrategy.getAccountInitialPosition()));
			}
		}

		return mergedStrategies;
	}

	private static TradingStrategy optimizeParameters(Stock stock, double initialPosition, Date initialDate,
			Date finalDate) {
		Integer[] entryDonchianSizes = new Integer[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
		Integer[] exitDonchianSizes = new Integer[] { 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		final int NO_ENTRY_FOUND = -1;

		JavaRDD<Integer> entryDonchianRDD = sc.parallelize(Arrays.asList(entryDonchianSizes));
		Map<Integer, Number[]> gains = entryDonchianRDD.mapToPair(entryDonchianSize -> {
			double bestGain = 0;
			int selectedExitSize = NO_ENTRY_FOUND;
			for (int exitDonchianSize : exitDonchianSizes) {
				TradingStrategy strategy = new TradingStrategyImpl(stock, entryDonchianSize, exitDonchianSize,
						initialPosition);
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
			return new TradingStrategyImpl(stock, selectedEntry, selectedExit, initialPosition);
		else
			return null;
	}

	// private static TradingStrategy optimizeParameters(Stock stock, double
	// initialPosition, Date initialDate,
	// Date finalDate) {
	// TradingStrategyImpl selectedStrategy = null;
	//
	// double bestGain = 0;
	// for (int entryDonchianSize = 10; entryDonchianSize <= 20;
	// entryDonchianSize++) {
	// for (int exitDonchianSize = 2; exitDonchianSize <= 10;
	// exitDonchianSize++) {
	// TradingStrategyImpl strategy = new TradingStrategyImpl(stock,
	// entryDonchianSize, exitDonchianSize,
	// initialPosition);
	// TradeSystem system = new TradeSystem(stock, initialPosition, strategy);
	// system.analyzeStocks(initialDate, finalDate);
	// system.closeAllOpenTrades(finalDate);
	//
	// double currentGain = system.getAccountBalance() -
	// system.getAccountInitialPosition();
	// if (currentGain > bestGain) {
	// bestGain = currentGain;
	// selectedStrategy = strategy;
	// }
	// }
	// }
	// log.info(String.format("Optimization for stock %s initial date %s gain %s
	// entry %s exit %s", stock.getCode(),
	// initialDate, bestGain, selectedStrategy != null ?
	// selectedStrategy.getEntryDonchianSize() : null,
	// selectedStrategy != null ? selectedStrategy.getExitDonchianSize() :
	// null));
	// return selectedStrategy;
	// }

	private static void printStocks(List<Stock> stocks) throws IOException {
		System.out.println("Stocks: " + stocks);
		String outputFile = "/tmp/output.txt";
		PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
		for (Stock stock : stocks) {
			writer.println(stock.getCode());
			for (Date d : stock.getHistory().keySet()) {
				StockHistory h = stock.getHistory(d);
				writer.println("####" + h);
			}
		}
		writer.flush();
		writer.close();
	}

}
