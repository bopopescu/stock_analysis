package preti.stock.web.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.db.model.OrderDBEntity;
import preti.stock.web.service.RecomendationsService;

@RestController
public class RecomendationsController {
    private Logger logger = LoggerFactory.getLogger(RecomendationsController.class);

    @Autowired
    private RecomendationsService recomendationsService;

    @RequestMapping(path = "/recomendation/generate", headers = "Accept=application/json")
    public List<OrderDBEntity> generateRecomendations(@RequestParam(name = "accountId", required = true) long accountId,
            @RequestParam(name = "date", required = true) String date) throws ParseException {
        logger.info(String.format("Generating recomendations accountId=%s date=%s", accountId, date));
        return recomendationsService.generateRecomendations(1, ControllerTools.parseDate(date));

    }
}
