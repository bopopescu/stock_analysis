package preti.stock.fe.location.recomendations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.location.AbstractController;

@RestController
public class RecomendationsRestController extends AbstractController {
    private Logger logger = LoggerFactory.getLogger(RecomendationsRestController.class);

    @Autowired
    private RecomendationsService recomendationsService;

    @RequestMapping(path = "/recomendations/generate", produces = "application/json")
    public RecomendationsVO generateRecomendations(@RequestParam(name = "accountId", required = true) long accountId,
            @RequestParam(name = "recDate", required = true) String recDate) throws RemoteApiException {
        logger.info(String.format("Generating recomendations account=%s date=%s", accountId, recDate));
        return recomendationsService.generateRecomendations(accountId, parseDate(recDate));
    }

}
