package preti.stock.fe.facade;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class RemoteApiException extends Exception {
	HttpStatus status;

	public RemoteApiException(HttpStatus status) {
		super();
		this.status = status;
	}

}
