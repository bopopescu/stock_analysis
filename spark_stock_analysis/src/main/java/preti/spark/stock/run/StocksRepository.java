package preti.spark.stock.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import preti.stock.InputDataEntry;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;

public class StocksRepository {
	private JavaSparkContext sc;

	public StocksRepository(JavaSparkContext sc) {
		super();
		this.sc = sc;
	}

	public List<Stock> loadStocks(String stockHistoryFile, List<String> stockCodesToAnalyze) {
		JavaRDD<InputDataEntry> inputData = sc.textFile(stockHistoryFile).filter(s -> !s.trim().isEmpty())
				.map(InputDataEntry::parseFromLine);
		inputData.persist(StorageLevel.MEMORY_ONLY());

		List<Stock> stocks = new ArrayList<>();

		for (String stockCode : stockCodesToAnalyze) {
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
		return stocks;
	}

}
