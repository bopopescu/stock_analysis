package preti.stock.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import preti.stock.web.exception.ApiValidationException;

@ControllerAdvice
public class GlobalApiErrorExceptionHandler {

    @ExceptionHandler(ApiValidationException.class)
    public void handleUncaughtException(ApiValidationException ex, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setHeader("ErrorCode", ex.getError().code);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
