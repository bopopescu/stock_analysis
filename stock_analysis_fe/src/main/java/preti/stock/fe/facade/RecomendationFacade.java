package preti.stock.fe.facade;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.coremodel.Trade;

@Service
public class RecomendationFacade extends AbstractApiFacade {
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected String getApiPath() {
        return "recomendation";
    }

    public List<Trade> generateRecomendations(long accountId, Date recomendationsDate) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/generate?accountId={accountId}&date={date}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("date", formatDate(recomendationsDate));

        ResponseEntity<Trade[]> response = restTemplate.getForEntity(resourceUrl.toString(), Trade[].class, parameters);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }

        return Arrays.asList(response.getBody());
    }

}
