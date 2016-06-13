package preti.stock.api.exception;

import preti.stock.api.ApiError;

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
