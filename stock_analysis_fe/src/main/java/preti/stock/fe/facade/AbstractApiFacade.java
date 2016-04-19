package preti.stock.fe.facade;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractApiFacade {
	protected final String DATE_FORMAT = "yyyy-MM-dd";

	protected URL getBaseEndpoint() throws MalformedURLException {
		return new URL("http", "stock_analysis_api", 8080, "");
	}

	protected URL getApiEndpoint() throws MalformedURLException {
		return new URL(getBaseEndpoint(), getApiPath());
	}

	protected URL getResourceEndpoint(String resourcePath) {
		try {
            return new URL(getApiEndpoint() + resourcePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
	}

	protected String formatDate(Date d) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(d);
	}

	protected abstract String getApiPath();

}
