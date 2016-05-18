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

import preti.stock.coremodel.Trade;

@Service
public class TradeFacade extends AbstractApiFacade {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected String getApiPath() {
        return "trade";
    }
    
    public List<Trade> getOpenTrades(long accountId, long stockId) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/open?accountId={accountId}&stockId={stockId}");
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("stockId", stockId);

        ResponseEntity<Trade[]> response = restTemplate.getForEntity(resourceUrl.toString(), Trade[].class, parameters);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }

        return Arrays.asList(response.getBody());
    }
}
