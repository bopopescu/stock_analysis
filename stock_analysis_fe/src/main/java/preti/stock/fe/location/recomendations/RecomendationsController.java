package preti.stock.fe.location.recomendations;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import preti.stock.fe.location.AbstractController;

@Controller
public class RecomendationsController extends AbstractController {

    @RequestMapping(path="/recomendations")
    public String recomendations(Map<String, Object> model, @RequestParam(name="accountId", required=true) long accountId) {
        model.put("accountId", accountId);
        return "recomendations";
    }

}
