package preti.stock.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.db.model.TradeDBEntity;
import preti.stock.web.service.TradeService;

@RestController
public class TradeController {
    private Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService tradeService;

    @RequestMapping(path = "/trade/open", method = RequestMethod.GET)
    public List<TradeDBEntity> getOpenTrades(@RequestParam(name = "accountId", required = true) long accountId,
            @RequestParam(name = "stockId", required = true) long stockId) {
        logger.debug(String.format("Finding open trades for account %s and stock %s", accountId, stockId));
        List<TradeDBEntity> trades = tradeService.getOpenTrades(accountId, stockId);
        logger.info(
                String.format("Found %s open trades for account %s and stock %s", trades.size(), accountId, stockId));
        return trades;
    }

}
