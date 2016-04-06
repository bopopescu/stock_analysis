package preti.stock.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.coremodel.Trade;
import preti.stock.web.service.RecomendationsService;

@RestController
public class RecomendationsController {
	private Logger logger = LoggerFactory.getLogger(RecomendationsController.class);
	private final String DATE_FORMAT = "yyyy-MM-dd";

	@Autowired
	private RecomendationsService recomendationsService;

	@RequestMapping(path = "/recomendation/generate", headers = "Accept=application/json")
	public List<Trade> generateRecomendations(@RequestParam String date) throws ParseException {
		logger.info("Executing recomendations for date " + date);
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date recomendationDate = dateFormat.parse(date);
		return recomendationsService.generateRecomendations(1, recomendationDate);

	}
}
