package preti.stock.fe.location.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import preti.stock.client.RemoteApiException;
import preti.stock.fe.location.AbstractController;

@Controller
public class OrderController extends AbstractController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(path = "/orders")
    public String recomendations(Map<String, Object> model,
            @RequestParam(name = "accountId", required = true) long accountId) throws RemoteApiException {
        model.put("accountId", accountId);
        model.put("orders", orderService.getAllOpenOrders(accountId));

        return "orders";
    }

}
