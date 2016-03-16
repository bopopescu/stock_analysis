package preti.stock.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.InputDataEntry;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;
import preti.stock.web.repository.StocksRepository;

@Service
public class StocksService {
	private Logger logger = LoggerFactory.getLogger(StocksService.class);
	private List<InputDataEntry> allDataEntries;

	@Autowired
	private StocksRepository stocksRepository;

	public void setStocksRepository(StocksRepository stocksRepository) {
		this.stocksRepository = stocksRepository;
	}

	// public StocksService() throws IOException {
	// try (Stream<String> lines = Files.lines(Paths.get("/var/stock_analysis/",
	// "cotacoes.txt"))) {
	// Stream<InputDataEntry> inputData = lines.filter(s -> !s.trim().isEmpty())
	// .map(InputDataEntry::parseFromLine);
	//
	// allDataEntries = new ArrayList<>();
	// Iterator<InputDataEntry> inputDataIterator = inputData.iterator();
	// while (inputDataIterator.hasNext()) {
	// allDataEntries.add(inputDataIterator.next());
	// }
	//
	// }
	// }

	public List<Stock> loadStocks(List<String> stockCodes, Date initialDate, Date finalDate) {
		logger.info("Loading stocks " + stockCodes);

		List<Stock> stocks = new ArrayList<>();

		for (String stockCode : stockCodes) {
			logger.info("Parsing " + stockCode);
			Stream<InputDataEntry> localData = Arrays.stream(allDataEntries.toArray(new InputDataEntry[] {}));
			localData = localData.filter(sd -> sd.getCode().equals(stockCode));

			if (initialDate != null) {
				localData = localData.filter(sd -> sd.getDate().compareTo(initialDate) >= 0);
			}
			if (finalDate != null) {
				localData = localData.filter(sd -> sd.getDate().compareTo(finalDate) <= 0);
			}

			Iterator<InputDataEntry> stockEntries = localData.iterator();
			Stock stock = new Stock(stockCode);
			stocks.add(stock);

			while (stockEntries.hasNext()) {
				InputDataEntry data = stockEntries.next();
				stock.addHistory(new StockHistory(data.getDate(), data.getHigh(), data.getLow(), data.getClose(),
						data.getVolume()));

			}
		}

		return stocks;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void loadStockData(String stockFile) throws IOException {
		logger.info(String.format("Loading stocks data from file %s ...", stockFile));

		Date lastStockDate = stocksRepository.getLastStocksDate();
		String.format("Last stock date is %s", lastStockDate);

		Stream<String> lines = Files.lines(Paths.get(stockFile));
		Stream<InputDataEntry> inputData;
		if (lastStockDate != null) {
			inputData = lines.filter(s -> !s.trim().isEmpty()).map(InputDataEntry::parseFromLine)
					.filter(entry -> entry.getDate().after(lastStockDate));
		} else {
			inputData = lines.filter(s -> !s.trim().isEmpty()).map(InputDataEntry::parseFromLine);
		}

		inputData.forEach(entry -> {

			if (!stocksRepository.existsStock(entry.getCode())) {
				logger.info(String.format("Creating stock %s ...", entry.getCode()));
				stocksRepository.createStock(entry.getCode(), entry.getName());
			}

			Integer stockId = stocksRepository.getStockId(entry.getCode());
			logger.info(String.format("Creating history code=%s date=%s close=%s open=%s volume=%s", entry.getCode(),
					entry.getDate(), entry.getClose(), entry.getOpen(), entry.getVolume()));
			stocksRepository.createHistory(stockId, entry.getDate(), entry.getClose(), entry.getOpen(),
					entry.getVolume());
		});

	}
}
