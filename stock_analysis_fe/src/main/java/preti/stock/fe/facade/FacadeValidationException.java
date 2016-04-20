package preti.stock.fe.facade;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class FacadeValidationException extends RuntimeException {

    private HttpStatus httpStatus;
    private String errorCode;

    public FacadeValidationException(HttpStatus httpStatus, String errorCode) {
        super();
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
