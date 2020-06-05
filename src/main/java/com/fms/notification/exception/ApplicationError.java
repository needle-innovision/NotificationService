package com.fms.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApplicationError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApplicationError(Exception e) {
        super(e.getCause().getCause().getMessage());
    }

    public ApplicationError(String message) {
        super(message);
    }

}
