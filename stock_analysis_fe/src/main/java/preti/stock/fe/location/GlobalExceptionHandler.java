package preti.stock.fe.location;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import preti.stock.client.ApiHeader;
import preti.stock.fe.facade.FacadeValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FacadeValidationException.class)
    public void handleFacadeValdidationException(FacadeValidationException ex, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setHeader(ApiHeader.ERROR_VALIDATION_CODE.headerName, ex.getErrorCode());
        response.setStatus(ex.getHttpStatus().value());
    }

}
