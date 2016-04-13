package preti.stock.fe.facade;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractApiFacade {

	protected URL getBaseEndpoint() throws MalformedURLException {
		return new URL("http", "stock_analysis_api", 8080, "");
	}

	protected URL getApiEndpoint() throws MalformedURLException {
		return new URL(getBaseEndpoint(), getApiPath());
	}

	protected URL getResourceEndpoint(String resourcePath) throws MalformedURLException {
		return new URL(getApiEndpoint() + resourcePath);
	}

	protected abstract String getApiPath();

}
