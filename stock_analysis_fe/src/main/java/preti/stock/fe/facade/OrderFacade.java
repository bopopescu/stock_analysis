package preti.stock.fe.facade;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.api.ApiHeader;
import preti.stock.coremodel.Order;
import preti.stock.coremodel.Trade;

@Service
public class OrderFacade extends AbstractApiFacade {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected String getApiPath() {
        return "order";
    }

    // FIXME: mostrar a mensagem de erro, caso não haja saldo suficiente
    public List<Order> createOrders(List<Order> orders) {
        URL resourceUrl = getResourceEndpoint("/create");

        ResponseEntity<Order[]> response = restTemplate.postForEntity(resourceUrl.toString(),
                orders.toArray(new Order[] {}), Order[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new FacadeValidationException(response.getStatusCode(),
                    response.getHeaders().getFirst(ApiHeader.ERROR_VALIDATION_CODE.headerName));
        }

        return Arrays.asList(response.getBody());
    }

    public List<Order> getAllOpenOrders(long accountId) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/getOpen?accountId={accountId}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(resourceUrl.toString(), Order[].class, parameters);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }

        return Arrays.asList(response.getBody());
    }

    public List<Trade> executeOrders(List<Order> orders, long accountId) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/execute?accountId={accountId}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        
        ResponseEntity<Trade[]> response = restTemplate.postForEntity(resourceUrl.toString(), orders.toArray(new Order[]{}), Trade[].class, parameters);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }

        return Arrays.asList(response.getBody());
    }

}
