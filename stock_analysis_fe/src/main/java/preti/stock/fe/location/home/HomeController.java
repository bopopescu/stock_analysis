package preti.stock.fe.location.home;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import preti.stock.client.RemoteApiException;
import preti.stock.fe.location.AbstractController;

@Controller
public class HomeController extends AbstractController {

    @Autowired
    private HomeService homeService;

    @RequestMapping("/home")
    public String home(Map<String, Object> model, @RequestParam(name = "accountId", required = true) long accountId)
            throws RemoteApiException {
        HomeVO homeVO = homeService.getHomeData(accountId);
        model.put("homeVO", homeVO);
        return "home";
    }

}
