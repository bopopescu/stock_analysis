package preti.stock.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Invalid operation")
public class InvalidOperationException extends Exception {

    public InvalidOperationException(String message) {
        super(message);
    }
    
}
