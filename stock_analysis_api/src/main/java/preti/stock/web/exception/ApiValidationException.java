package preti.stock.web.exception;

import preti.stock.web.ApiError;

@SuppressWarnings("serial")
public class ApiValidationException extends Exception {
    private ApiError error;

    public ApiValidationException(ApiError error) {
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }

}
