package preti.stock.fe.facade;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.client.RemoteApiException;
import preti.stock.client.model.Stock;
import preti.stock.client.model.StockHistory;

@Service
public class StockFacade extends AbstractApiFacade {
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected String getApiPath() {
        return "stock";
    }

    public Stock getStock(long stockId) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("?stockId={stockId}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stockId", stockId);

        ResponseEntity<Stock> response = restTemplate.getForEntity(resourceUrl.toString(), Stock.class, parameters);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }
        return response.getBody();
    }

    public StockHistory[] getStockHistory(long stockId, Date initialDate, Date finalDate) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/history?stockId={stockId}&begin={begin}&end={end}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stockId", stockId);
        parameters.put("begin", formatDate(initialDate));
        parameters.put("end", formatDate(finalDate));

        ResponseEntity<StockHistory[]> response = restTemplate.getForEntity(resourceUrl.toString(),
                StockHistory[].class, parameters);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }
        return response.getBody();
    }

}
