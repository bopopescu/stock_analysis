package preti.stock.fe.facade;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;

@Service
public class StockFacade extends AbstractApiFacade {
	private final String DATE_FORMAT = "yyyy-MM-dd";

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

	public StockHistory[] getStockHistory(long stockId, Date initialDate, Date finalDate) throws RemoteApiException {
		URL resourceUrl;
		try {
			resourceUrl = getResourceEndpoint("/history?stockId={stockId}&begin={begin}&end={end}");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("stockId", stockId);
		parameters.put("begin", dateFormat.format(initialDate));
		parameters.put("end", dateFormat.format(finalDate));

		ResponseEntity<StockHistory[]> response = restTemplate.getForEntity(resourceUrl.toString(), StockHistory[].class, parameters);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			return null;
		}
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RemoteApiException(response.getStatusCode());
		}
		return response.getBody();
	}

}
