package com.fms.notification.exception.handler;

import com.fms.notification.exception.ApplicationError;
import com.fms.notification.exception.EntityNotFoundException;
import com.fms.notification.exception.NotificationSubscriptionException;
import com.fms.notification.vo.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fms.notification.vo.Error;
import javax.ws.rs.BadRequestException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestControllerAdvice
//@ControllerAdvice
@RestController
@Slf4j
public class ControllerResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the Error object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new Error(BAD_REQUEST, error, ex));
    }


    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the Error object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return buildResponseEntity(new Error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the Error object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        Error error = new Error(BAD_REQUEST);
        error.setMessage(ex.getBindingResult().getFieldError().getDefaultMessage());
        error.addValidationErrors(ex.getBindingResult().getFieldErrors());
        error.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(error);
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the Error object
     */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
        Error error = new Error(BAD_REQUEST);
        error.setMessage(ex.getLocalizedMessage());
        error.addValidationErrors(ex.getConstraintViolations());
        return buildResponseEntity(error);
    }

   /* @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        Response<String> responseVO = new Response<>();
        responseVO.setStatus(HttpStatus.BAD_REQUEST);
        responseVO.setMessage("Invalid data provided.");

        return ResponseEntity.badRequest().body(responseVO);
    }
*/

    @ExceptionHandler(ApplicationError.class)
    public final ResponseEntity<Object> handleApplicationError(ApplicationError ex, WebRequest request) {
        log.error("Application Exception {}", ex);
        Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.", ex);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Object> handleSqlException(RuntimeException ex, WebRequest request) {
        log.error("SQLException {}", ex);
        Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong", ex);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.error("Handling BadRequestException: {}", ex);
        Error error = new Error(HttpStatus.BAD_REQUEST, "Invalid request", ex);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Handling EntityNotFoundException: {}", ex);
        Error error = new Error(HttpStatus.NOT_FOUND, "Resource not found", ex);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(NotificationSubscriptionException.class)
    public final ResponseEntity<Object> handleNotificationSubscriptionException(NotificationSubscriptionException ex, WebRequest request) {
        log.error("Handling NotificationSubscriptionException: {}", ex);
        return buildResponseEntity(new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
    }

    private ResponseEntity<Object> buildResponseEntity(Error error) {
        return new ResponseEntity<>(new ErrorResponse(error, error.getMessage()), error.getStatus());
    }


}
