package preti.stock.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.client.model.Stock;
import preti.stock.client.model.StockHistory;
import preti.stock.db.model.StockDBEntity;
import preti.stock.db.model.StockHistoryDBEntity;
import preti.stock.web.service.StocksService;

@RestController
public class StocksController {
    @Autowired
    private StocksService stocksService;

    public void setStocksService(StocksService stocksService) {
        this.stocksService = stocksService;
    }

    // curl -H "Content-Type: application/json" -d '["PETR4", "CMIG4"]'
    // localhost:8080/loadStocks
    @RequestMapping(path = "/Sstock", headers = "Accept=application/json")
    public List<Stock> loadStocks(@RequestBody List<String> stockCodes,
            @RequestParam(name = "begin", required = true) String initialDate,
            @RequestParam(name = "end", required = true) String finalDate) throws IOException, ParseException {

        List<StockDBEntity> stocks = stocksService.loadStocks(stockCodes);
        List<Stock> result = new ArrayList<>();
        for (StockDBEntity sdb : stocks) {
            result.add(new Stock(sdb.getId(), sdb.getCode(), sdb.getName()));
        }

        return result;
    }

    @RequestMapping(path = "/stock/loadData", headers = "Accept=application/json")
    public void loadStockData(@RequestParam(name = "file", required = true) String stocksFile) throws IOException {
        stocksService.loadStockData(stocksFile);
    }

    @RequestMapping(path = "/stock", headers = "Accept=application/json", method = RequestMethod.GET)
    public Stock getStock(@RequestParam(name = "stockId", required = true) long stockId) {
        StockDBEntity sde = stocksService.getStock(stockId);
        if (sde == null)
            return null;

        return new Stock(sde.getId(), sde.getCode(), sde.getName());
    }

    @RequestMapping(path = "/stock/history", headers = "Accept=application/json", method = RequestMethod.GET)
    public List<StockHistory> getStockHistory(@RequestParam(name = "stockId", required = true) long stockId,
            @RequestParam(name = "begin", required = true) String initialDate,
            @RequestParam(name = "end", required = true) String finalDate) throws IOException, ParseException {
        List<StockHistoryDBEntity> histories = stocksService.getStockHistory(stockId,
                ControllerTools.parseDate(initialDate), ControllerTools.parseDate(finalDate));

        List<StockHistory> result = new ArrayList<>();
        for (StockHistoryDBEntity s : histories) {
            result.add(new StockHistory(s.getId(), s.getStockId(), s.getDate(), s.getHigh(), s.getLow(), s.getClose(),
                    s.getVolume()));
        }
        return result;
    }

}
