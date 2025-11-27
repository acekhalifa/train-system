package com.esl.academy.api.core.response;

import com.esl.academy.api.core.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice("com.esl.academy.2025.api")
public class ResponseBodyAdviceImpl implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseEntity
                || body instanceof Resource
                || body instanceof String
                || body instanceof SseEmitter
                || body instanceof byte[]
                || MediaType.APPLICATION_PDF.equals(selectedContentType)) {
            return body;
        }

        return new ApiSuccess(body);
    }

    @ExceptionHandler({
            BadRequestException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            MaxUploadSizeExceededException.class,
            ConversionFailedException.class,
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentTypeMismatchException.class
    })

    public ResponseEntity<ApiError> handleBadRequestException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonBadRequest(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();

        String errorMessage = "Invalid request format";

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormat) {

            if (invalidFormat.getTargetType() == LocalDate.class) {
                String fieldName = invalidFormat.getPath().isEmpty() ? "date field"
                        : invalidFormat.getPath().get(0).getFieldName();
                String invalidValue = invalidFormat.getValue() != null ? invalidFormat.getValue().toString() : "null";

                errorMessage = String.format(
                        "Invalid date format for field '%s'. Value '%s' must be in format yyyy-MM-dd (e.g., 2024-12-31)",
                        fieldName, invalidValue
                );
            } else if (invalidFormat.getTargetType() == OffsetDateTime.class) {
                String fieldName = invalidFormat.getPath().isEmpty() ? "datetime field"
                        : invalidFormat.getPath().get(0).getFieldName();
                String invalidValue = invalidFormat.getValue() != null ? invalidFormat.getValue().toString() : "null";

                errorMessage = String.format(
                        "Invalid datetime format for field '%s'. Value '%s' must be in ISO-8601 format (e.g., 2024-12-31T14:30:00Z)",
                        fieldName, invalidValue
                );
            } else {
                errorMessage = String.format(
                        "Invalid value type for field '%s'",
                        invalidFormat.getPath().isEmpty() ? "unknown" : invalidFormat.getPath().get(0).getFieldName()
                );
            }
        } else if (e.getMessage() != null && e.getMessage().contains("Cannot deserialize")) {
            errorMessage = e.getMessage().split("\\:")[1].trim();
        }

        log.error("Invalid request format error at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        return commonBadRequest(errorMessage, uri, errorTraceId);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiError> handleHttpMessageConversionException(
            HttpMessageConversionException e,
            HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();

        String errorMessage = "Invalid JSON format in request body";
        if (e.getMessage() != null && e.getMessage().contains("Cannot deserialize value")) {
            errorMessage = e.getMessage().split("\\:")[1].trim();
        }

        log.error("JSON parsing error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        return commonBadRequest("JSON parse error: " + errorMessage, uri, errorTraceId);
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    public ResponseEntity<ApiError> handleServletRequestBindingException(
            ServletRequestBindingException e,
            HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();

        log.error("Request binding error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        return commonBadRequest(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler({
            NoSuchElementException.class,
            NotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonNotFound(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler({
            org.hibernate.exception.DataException.class,
            org.hibernate.exception.SQLGrammarException.class,
            org.hibernate.exception.ConstraintViolationException.class,
            org.springframework.dao.DataIntegrityViolationException.class,
            jakarta.persistence.PersistenceException.class,
            java.sql.SQLException.class
    })
    public ResponseEntity<ApiError> handleDatabaseException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("A database error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        final var message = extractDBExceptionUserFriendlyMessage(e);
        return commonBadRequest(message, uri, errorTraceId);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();

        log.error(e.getMessage(), e);
        String message = String.format(
                "Content-Type '%s' is not supported. Please use 'multipart/form-data' for this endpoint.",
                e.getContentType()
        );

        return commonUnsupported(message, uri, errorTraceId);
    }


    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class,
    })
    public ResponseEntity<ApiError> handleUnAuthorizedException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonUnAuthorized(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationException.class,
    })
    public ResponseEntity<ApiError> handleForbiddenException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonForbidden(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler({
            ConflictException.class,
    })
    public ResponseEntity<ApiError> handleConflictException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonConflict(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler({
            InternalServerException.class
    })
    public ResponseEntity<ApiError> handleInternalServerException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return commonInternal(e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidationException(HandlerMethodValidationException e, HttpServletRequest request) {
        final var message = e.getParameterValidationResults()
                .stream()
                .map(result -> {
                    final var parameterName = result.getMethodParameter().getParameter().getName();
                    final var errorMessage = result.getResolvableErrors()
                            .stream().map(MessageSourceResolvable::getDefaultMessage)
                            .collect(Collectors.joining(","));
                    return parameterName.concat(" ").concat(errorMessage);
                })
                .collect(Collectors.joining(", "));

        final var apiError = new ApiError(request.getRequestURI(),
                message, null, BAD_REQUEST.value(), now());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {


        String fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField().concat(" ").concat(Objects.toString(error.getDefaultMessage(), "")))
                .collect(Collectors.joining(", "));


        String globalErrors = e.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> Objects.toString(error.getDefaultMessage(), "Validation failed"))
                .collect(Collectors.joining(", "));


        String message = Stream.of(fieldErrors, globalErrors)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(", "));

        // Fallback if somehow everything is empty
        if (message.isBlank()) {
            message = "Validation failed";
        }

        final var apiError = new ApiError(request.getRequestURI(),
                message, null, BAD_REQUEST.value(), now());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public SseEmitter sseTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        log.error("An async request timeout exception error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);
        return null;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestPartException(
            MissingServletRequestPartException e,
            HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();
        final String partName = e.getRequestPartName();

        log.error("Missing request part error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        return commonBadRequest("Required request part '" + partName + "' is missing", uri, errorTraceId);
    }

    @ExceptionHandler({
            JsonProcessingException.class,
            JsonMappingException.class,
            InvalidFormatException.class,
            UnrecognizedPropertyException.class
    })
    public ResponseEntity<ApiError> handleJacksonException(Exception e, HttpServletRequest request) {
        final var errorTraceId = UUID.randomUUID().toString();
        final var uri = request.getRequestURI();

        log.error("A JSON processing error has occurred at {} with id {}", uri, errorTraceId);
        log.error(e.getMessage(), e);

        return commonBadRequest("Invalid JSON format: " + e.getMessage(), uri, errorTraceId);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
        final var requestURI = request.getRequestURI();
        final var errorTraceId = UUID.randomUUID().toString();

        final var apiError = new ApiError(requestURI,
                "An internal server error has occurred",
                errorTraceId,
                INTERNAL_SERVER_ERROR.value(), now());

        log.error("An internal server error has occurred at {} with id {}", requestURI, errorTraceId);
        log.error(ExceptionUtils.getStackTrace(e));

        return new ResponseEntity<>(apiError, INTERNAL_SERVER_ERROR);
    }

    private String extractDBExceptionUserFriendlyMessage(Exception e) {
        // Default message
        String message = "Invalid data format or constraint violation";

        Throwable cause = e;
        // Try to find the root cause with a meaningful message
        while (cause != null) {
            String causeMessage = cause.getMessage();

            if (causeMessage != null) {
                // Handle invalid JSON format
                if (causeMessage.contains("invalid input syntax for type json")) {
                    return "Invalid JSON format in request";
                }

                // Handle constraint violations
                if (causeMessage.contains("violates") && causeMessage.contains("constraint")) {
                    if (causeMessage.contains("unique")) {
                        return "A record with the same unique identifier already exists";
                    }
                    if (causeMessage.contains("foreign key")) {
                        if (causeMessage.contains("is still referenced from table")) {
                            String referencingTable = extractDetail(causeMessage, "is still referenced from table \"(.*?)\"");

                            if (referencingTable == null) {
                                referencingTable = extractDetail(causeMessage, "on table \"(.*?)\"");
                            }

                            // Convert table names to user-friendly business terms
                            String businessEntity = convertTableNameToBusinessEntity(referencingTable);

                            return String.format("Cannot delete because %s for this record exists", businessEntity);
                        }
                        return "Referenced record does not exist";
                    }
                    return "Data constraint violation occurred";
                }

                // Handle out-of-range errors
                if (causeMessage.contains("out of range")) {
                    return "One or more values are out of acceptable range";
                }
            }

            cause = cause.getCause();
        }

        return message;
    }

    /**
     * Converts database table names to user-friendly business entity names.
     *
     * @param tableName the database table name
     * @return user-friendly business entity name
     */
    private String convertTableNameToBusinessEntity(String tableName) {
        if (tableName == null) {
            return "a related record";
        }

        String converted = tableName.replace("_", " ");
        String capitalized = converted.substring(0, 1).toUpperCase() + converted.substring(1);

        String article = capitalized.toLowerCase().matches("^[aeiou].*") ? "an" : "a";

        return article + " " + capitalized;
    }

    /**
     * Extracts specific details from a message using a regex pattern.
     *
     * @param message the exception message
     * @param regex   the regex pattern to extract details
     * @return the extracted detail or null if not found
     */
    private String extractDetail(String message, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private ResponseEntity<ApiError> commonBadRequest(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, BAD_REQUEST.value(), now());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    private ResponseEntity<ApiError> commonNotFound(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, NOT_FOUND.value(), now());
        return new ResponseEntity<>(apiError, NOT_FOUND);
    }

    private ResponseEntity<ApiError> commonUnAuthorized(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, UNAUTHORIZED.value(), now());
        return new ResponseEntity<>(apiError, UNAUTHORIZED);
    }

    private ResponseEntity<ApiError> commonUnsupported(String message, String  requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, UNSUPPORTED_MEDIA_TYPE.value(), now());
        return new ResponseEntity<>(apiError, UNSUPPORTED_MEDIA_TYPE);
    }

    private ResponseEntity<ApiError> commonForbidden(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, FORBIDDEN.value(), now());
        return new ResponseEntity<>(apiError, FORBIDDEN);
    }

    private ResponseEntity<ApiError> commonConflict(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, CONFLICT.value(), now());
        return new ResponseEntity<>(apiError, CONFLICT);
    }

    private ResponseEntity<ApiError> commonInternal(String message, String requestURI, String errorTraceId) {
        final var apiError = new ApiError(requestURI, message, errorTraceId, INTERNAL_SERVER_ERROR.value(), now());
        return new ResponseEntity<>(apiError, INTERNAL_SERVER_ERROR);
    }
}
