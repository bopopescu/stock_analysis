package preti.stock.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.web.service.TradeService;

@RestController
public class TradeController {
    private Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService orderService;

//    @RequestMapping(path = "/trade/realize", headers = "Accept=application/json")
//    public void realizetrades(@RequestBody List<Trade> trades,
//            @RequestParam(name = "accountId", required = true) long accountId) throws ApiValidationException {
//        logger.info(String.format("Realizing trades for account [%s]: %s ", accountId, trades));
//
//        tradeService.realizeTrades(accountId, trades);
//    }

}
