package preti.spark.stock.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import preti.spark.stock.model.Stock;
import preti.spark.stock.system.TradeSystem;
import preti.spark.stock.system.TradingStrategy;
import preti.spark.stock.system.TradingStrategyImpl;
import scala.Tuple2;

public class DonchianStrategyParametersOptimizer {
	private static final Log log = LogFactory.getLog(DonchianStrategyParametersOptimizer.class);

	private JavaSparkContext sc;

	public DonchianStrategyParametersOptimizer(JavaSparkContext sc) {
		super();
		this.sc = sc;
	}

	public DonchianParametersOptimizationResult optimizeParameters(Stock stock, double initialPosition,
			Date initialDate, Date finalDate, int minDonchianEntrySize, int maxDonchianEntrySize,
			int minDonchianExitSize, int maxDonchianExitSize, double riskRate) {
		List<Integer> entryDonchianSizes = new ArrayList<>();
		for (int i = minDonchianEntrySize; i <= maxDonchianEntrySize; i++) {
			entryDonchianSizes.add(i);
		}

		final int NO_ENTRY_FOUND = -1;

		JavaRDD<Integer> entryDonchianRDD = sc.parallelize(entryDonchianSizes);
		Map<Integer, Number[]> gains = entryDonchianRDD.mapToPair(entryDonchianSize -> {
			double bestGain = 0;
			int selectedExitSize = NO_ENTRY_FOUND;
			for (int exitDonchianSize = minDonchianExitSize; exitDonchianSize <= maxDonchianExitSize
					&& exitDonchianSize <= entryDonchianSize; exitDonchianSize++) {
				TradingStrategy strategy = new TradingStrategyImpl(stock, entryDonchianSize, exitDonchianSize,
						initialPosition, riskRate);
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
			return new DonchianParametersOptimizationResult(stock.getCode(), selectedEntry, selectedExit, riskRate);
		else
			return null;
	}

}
