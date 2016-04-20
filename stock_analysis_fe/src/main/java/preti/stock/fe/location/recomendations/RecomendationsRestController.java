package preti.stock.fe.location.recomendations;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.location.AbstractController;
import preti.stock.fe.location.TradeVO;

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

    @RequestMapping(path = "/recomendations/realizeTrades", headers = "Accept=application/json", method = RequestMethod.POST)
    public void executeTrades(@RequestBody List<TradeVO> trades,
            @RequestParam(name = "accountId", required = true) long accountId) {
        recomendationsService.executeTrades(accountId, trades);
    }

}
