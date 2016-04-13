package preti.stock.fe.facade;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.coremodel.Stock;

@Service
public class StockFacade extends AbstractApiFacade {

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	protected String getApiPath() {
		return "stock";
	}

	public Stock getStock(long stockId) throws RemoteApiException {
		URL resourceUrl;
		try {
			resourceUrl = getResourceEndpoint("?stockId={stockId}");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

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

}
