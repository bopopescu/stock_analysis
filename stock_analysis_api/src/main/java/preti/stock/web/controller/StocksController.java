package preti.stock.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;
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
    @RequestMapping(path = "/stock", headers = "Accept=application/json")
    public List<Stock> loadStocks(@RequestBody List<String> stockCodes,
            @RequestParam(name = "begin", required = true) String initialDate,
            @RequestParam(name = "end", required = true) String finalDate) throws IOException, ParseException {

        return stocksService.loadStocks(stockCodes, ControllerTools.parseDate(initialDate),
                ControllerTools.parseDate(finalDate));
    }

    @RequestMapping(path = "/stock/loadData", headers = "Accept=application/json")
    public void loadStockData(@RequestParam(name = "file", required = true) String stocksFile) throws IOException {
        stocksService.loadStockData(stocksFile);
    }

    @RequestMapping(path = "/stock", headers = "Accept=application/json", method = RequestMethod.GET)
    public Stock getStock(@RequestParam(name = "stockId", required = true) long stockId) {
        return stocksService.getStock(stockId);
    }

    @RequestMapping(path = "/stock/history", headers = "Accept=application/json", method = RequestMethod.GET)
    public List<StockHistory> getStockHistory(@RequestParam(name = "stockId", required = true) long stockId,
            @RequestParam(name = "begin", required = true) String initialDate,
            @RequestParam(name = "end", required = true) String finalDate) throws IOException, ParseException {
        return stocksService.getStockHistory(stockId, ControllerTools.parseDate(initialDate),
                ControllerTools.parseDate(finalDate));
    }

}
