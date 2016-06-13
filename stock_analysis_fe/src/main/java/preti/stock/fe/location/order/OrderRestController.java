package preti.stock.fe.location.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.client.RemoteApiException;
import preti.stock.fe.location.AbstractController;
import preti.stock.fe.location.OrderVO;

@RestController
public class OrderRestController extends AbstractController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(path = "/orders/executeOrders", headers = "Accept=application/json", method = RequestMethod.POST)
    public void executeOrders(@RequestBody List<OrderVO> orders,
            @RequestParam(name = "accountId", required = true) long accountId) throws RemoteApiException {
        orderService.executeOrders(orders, accountId);
    }

}
