package preti.spark.stock.run.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import preti.spark.stock.InputDataEntry;
import preti.spark.stock.model.Stock;
import preti.spark.stock.model.StockHistory;

public class ModelGeneration {
	private static final Log log = LogFactory.getLog(ModelGeneration.class);
	private static JavaSparkContext sc;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if (args.length != 1) {
			System.out.println("Must specify config file");
			System.exit(-1);
		}

		ModelGenerationConfig config = new ObjectMapper().readValue(new File(args[0]), ModelGenerationConfig.class);

		SparkConf conf = new SparkConf();
		sc = new JavaSparkContext(conf);

		JavaRDD<InputDataEntry> inputData = sc.textFile(config.getStockHistoryFile()).filter(s -> !s.trim().isEmpty())
				.map(InputDataEntry::parseFromLine);
		inputData.persist(StorageLevel.MEMORY_ONLY());

		List<String> stockCodes = config.getStockCodesToAnalyze();
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

		DonchianStrategyParametersOptimizer optimizer = new DonchianStrategyParametersOptimizer(sc);
		List<DonchianParametersOptimizationResult> results = new ArrayList<>();
		for (Stock s : stocks) {
			DonchianParametersOptimizationResult result = optimizer.optimizeParameters(s,
					config.getAccountInitialValue(), config.getStart(), config.getEnd(),
					config.getMinDonchianEntrySize(), config.getMaxDonchianEntrySize(), config.getMinDonchianExitSize(),
					config.getMaxDonchianExitSize(), config.getRiskRate());
			if (result != null) {
				results.add(result);
			}
		}

		new ObjectMapper().writeValue(new File(config.getOutputFile()), results);

	}
}
