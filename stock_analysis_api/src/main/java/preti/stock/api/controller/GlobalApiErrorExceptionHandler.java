package preti.stock.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import preti.stock.api.exception.ApiValidationException;
import preti.stock.client.ApiHeader;

@ControllerAdvice
public class GlobalApiErrorExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalApiErrorExceptionHandler.class);

    @ExceptionHandler(ApiValidationException.class)
    public void handleUncaughtException(ApiValidationException ex, HttpServletResponse response,
            HttpServletRequest request) {
        logger.info(String.format("ApiValidationException at %s: %s ", request.getRequestURI(), ex.getError().code));
        response.setHeader("Content-Type", "application/json");
        response.setHeader(ApiHeader.ERROR_VALIDATION_CODE.headerName, ex.getError().code);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
