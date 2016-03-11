package preti.spark.stock.run.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import preti.spark.stock.run.DonchianStrategyOptimizer;
import preti.spark.stock.run.StocksRepository;
import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.coremodel.Stock;

public class ModelGeneration {
	private static final Log log = LogFactory.getLog(ModelGeneration.class);
	private static JavaSparkContext sc;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if (args.length != 1) {
			System.out.println("Must specify config file");
			System.exit(-1);
		}

		log.info("Reading config ...");
		ModelGenerationConfig config = new ObjectMapper().readValue(new File(args[0]), ModelGenerationConfig.class);

		SparkConf conf = new SparkConf();
		sc = new JavaSparkContext(conf);

		log.info("Loading stock data ...");
		StocksRepository stocksRepository = new StocksRepository(sc);
		List<Stock> stocks = stocksRepository.loadStocks(config.getStockHistoryFile(), config.getStockCodesToAnalyze());

		DonchianStrategyOptimizer optimizer = new DonchianStrategyOptimizer(sc);
		List<DonchianModel> results = new ArrayList<>();
		for (Stock s : stocks) {
			DonchianModel result = optimizer.optimizeParameters(s, config.getAccountInitialValue(),
					config.getParsedStart(), config.getParsedEnd(), config.getMinDonchianEntrySize(),
					config.getMaxDonchianEntrySize(), config.getMinDonchianExitSize(), config.getMaxDonchianExitSize(),
					config.getRiskRate());
			if (result != null) {
				results.add(result);
			}
		}

		new ObjectMapper().writeValue(new File(config.getOutputFile()), results);

	}
}
