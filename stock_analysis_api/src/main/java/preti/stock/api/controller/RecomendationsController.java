package preti.stock.api.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.api.model.db.OrderDBEntity;
import preti.stock.api.service.RecomendationsService;
import preti.stock.client.model.OperationType;
import preti.stock.client.model.Order;

@RestController
public class RecomendationsController {
    private Logger logger = LoggerFactory.getLogger(RecomendationsController.class);

    @Autowired
    private RecomendationsService recomendationsService;

    @RequestMapping(path = "/recomendation/generate", headers = "Accept=application/json")
    public List<Order> generateRecomendations(@RequestParam(name = "accountId", required = true) long accountId,
            @RequestParam(name = "date", required = true) String date) throws ParseException {
        logger.info(String.format("Generating recomendations accountId=%s date=%s", accountId, date));

        List<OrderDBEntity> recomendations = recomendationsService.generateRecomendations(1,
                ControllerTools.parseDate(date));
        List<Order> result = new ArrayList<>();
        recomendations.forEach(r -> result.add(new Order(r.getOrderId(), OperationType.getFromValue(r.getType().type),
                r.getStockId(), r.getModelId(), r.getSize(), r.getCreationDate(), r.getValue(), r.getStopPos())));

        return result;

    }
}
