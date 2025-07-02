package com.ibs.vi.exception;

import com.ibs.vi.view.RouteErrorResponseView;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<RouteErrorResponseView> handleRouteNotFound(RouteNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new RouteErrorResponseView(ex.getMessage(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }
}
