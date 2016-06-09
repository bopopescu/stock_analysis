package preti.stock.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.InputDataEntry;
import preti.stock.db.model.StockDBEntity;
import preti.stock.db.model.StockHistoryDBEntity;
import preti.stock.web.repository.StocksRepository;

@Service
public class StocksService {
    private Logger logger = LoggerFactory.getLogger(StocksService.class);

    @Autowired
    private StocksRepository stocksRepository;

    public void setStocksRepository(StocksRepository stocksRepository) {
        this.stocksRepository = stocksRepository;
    }

    public StockDBEntity getStock(long stockId) {
        return stocksRepository.getStock(stockId);
    }

    public List<StockDBEntity> loadStocks(List<String> stockCodes) {
        logger.info("Loading stocks " + stockCodes);

        List<StockDBEntity> stocks = new ArrayList<>();
        for (String stockCode : stockCodes) {
            logger.debug("Parsing " + stockCode);
            stocks.add(stocksRepository.getStock(stockCode));
        }

        return stocks;
    }

    public List<StockHistoryDBEntity> getStockHistory(long stockId, Date initialDate, Date finalDate) {
        return stocksRepository.getStockHistory(stockId, initialDate, finalDate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loadStockData(String stockFile) throws IOException {
        logger.info(String.format("Loading stocks data from file %s ...", stockFile));

        Date lastStockDate = stocksRepository.getLastStocksDate();
        String.format("Last stock date is %s", lastStockDate);

        // FIXME: verificar se de fato não há leak aqui.
        @SuppressWarnings("resource")
        Stream<String> lines = Files.lines(Paths.get(stockFile));

        Stream<InputDataEntry> inputData;
        if (lastStockDate != null) {
            inputData = lines.filter(s -> !s.trim().isEmpty()).map(InputDataEntry::parseFromLine)
                    .filter(entry -> entry.getDate().after(lastStockDate));
        } else {
            inputData = lines.filter(s -> !s.trim().isEmpty()).map(InputDataEntry::parseFromLine);
        }

        inputData.parallel().forEach(entry -> {

            if (!stocksRepository.existsStock(entry.getCode())) {
                logger.info(String.format("Creating stock %s ...", entry.getCode()));
                stocksRepository.createStock(entry.getCode(), entry.getName());
            }

            Integer stockId = stocksRepository.getStockId(entry.getCode());
            if (stocksRepository.existsHistoryAtDate(stockId, entry.getDate())) {
                logger.info(String.format("History for stock %s at date %s alread exists", entry.getCode(),
                        entry.getDate()));
                return;
            }

            logger.info(String.format("Creating history code=%s date=%s close=%s open=%s volume=%s", entry.getCode(),
                    entry.getDate(), entry.getClose(), entry.getOpen(), entry.getVolume()));
            stocksRepository.createHistory(stockId, entry.getDate(), entry.getHigh(), entry.getLow(), entry.getClose(),
                    entry.getOpen(), entry.getVolume());
        });

    }
}
