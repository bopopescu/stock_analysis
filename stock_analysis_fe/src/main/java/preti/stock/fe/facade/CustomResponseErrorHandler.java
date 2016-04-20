package preti.stock.fe.facade;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class CustomResponseErrorHandler implements ResponseErrorHandler {
    private Logger logger = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        logger.warn(String.format("Response error %s %s", response.getStatusCode(), response.getStatusText()));

    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return isError(response.getStatusCode());
    }

    public boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
    }

}
