package preti.stock.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.coremodel.Trade;
import preti.stock.web.service.TradesService;

@RestController
public class TradeController {
	private Logger logger = LoggerFactory.getLogger(TradeController.class);

	@Autowired
	private TradesService tradeService;

	@RequestMapping(path = "/trades/realize", headers = "Accept=application/json")
	public void realizetrades(@RequestBody List<Trade> trades) {
		logger.info("Realizing trades " + trades);
		tradeService.realizeTrades(1, trades);
	}

}
