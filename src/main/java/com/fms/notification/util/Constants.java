package com.fms.notification.util;

import com.fms.notification.vo.RedirectResponse;
import com.fms.notification.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface Constants {
    static <T> ResponseEntity<Response> buildResponseEntity(T data, HttpStatus status) {
        return new ResponseEntity<>(new Response("", data), status);
    }

    static <T> ResponseEntity<Response> buildResponseEntity(T data, String message, HttpStatus status) {
        return new ResponseEntity<>(new Response(message, data), status);
    }

    static <T> ResponseEntity<Response> buildResponseEntity(T obj, String message, String redirectPage, HttpStatus status) {
        return new ResponseEntity<>(new RedirectResponse(message, obj, redirectPage), status);
    }
}
