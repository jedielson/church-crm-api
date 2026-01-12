package org.churchcrm.churchcrmapi.crosscutting.web.internal;

import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.crosscutting.security.UnauthorizedException;
import org.churchcrm.churchcrmapi.crosscutting.web.ConflictException;
import org.churchcrm.churchcrmapi.crosscutting.web.ForbiddenException;
import org.churchcrm.churchcrmapi.crosscutting.web.NotFoundException;
import org.churchcrm.churchcrmapi.crosscutting.web.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that returns RFC 9457 Problem Details format for all exceptions.
 * 
 * <p>This handler provides consistent error responses across the application using
 * base exception types:</p>
 * <ul>
 *   <li>{@link ValidationException} - HTTP 400 Bad Request</li>
 *   <li>{@link NotFoundException} - HTTP 404 Not Found</li>
 *   <li>{@link ConflictException} - HTTP 409 Conflict</li>
 *   <li>{@link ForbiddenException} - HTTP 403 Forbidden</li>
 *   <li>{@link UnauthorizedException} - HTTP 401 Unauthorized</li>
 * </ul>
 * 
 * <p>Security considerations:</p>
 * <ul>
 *   <li>Returns generic 404 for unauthorized access (security through obscurity)</li>
 *   <li>Logs detailed errors server-side but returns sanitized messages to clients</li>
 *   <li>Avoids leaking sensitive information in error responses</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ========================================================================
    // Base Exception Handlers
    // ========================================================================

    /**
     * Handles ValidationException (HTTP 400).
     * Returns detailed field-level validation errors when available.
     */
    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex, WebRequest request) {
        log.debug("Validation error: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        // Add field-level errors if present
        if (ex.hasFieldErrors()) {
            problemDetail.setProperty("fieldErrors", ex.getFieldErrors());
        }
        
        return problemDetail;
    }

    /**
     * Handles NotFoundException (HTTP 404).
     * Returns generic "not found" message for security.
     */
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        
        String detail = ex.getResourceType() != null 
            ? String.format("%s not found", ex.getResourceType())
            : "Resource not found";
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            detail
        );
        problemDetail.setTitle("Not Found");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    /**
     * Handles ConflictException (HTTP 409).
     * Returns conflict details to help client resolve the issue.
     */
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException ex, WebRequest request) {
        log.debug("Conflict: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.getMessage()
        );
        problemDetail.setTitle("Conflict");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        // Add conflict details if present
        if (ex.getConflictType() != null) {
            problemDetail.setProperty("conflictType", ex.getConflictType());
        }
        if (ex.getConflictValue() != null) {
            problemDetail.setProperty("conflictValue", ex.getConflictValue());
        }
        
        return problemDetail;
    }

    /**
     * Handles ForbiddenException (HTTP 403).
     * Returns explicit access denied message.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbiddenException(ForbiddenException ex, WebRequest request) {
        log.warn("Forbidden access attempt: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            ex.getMessage()
        );
        problemDetail.setTitle("Forbidden");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        // Add permission details if present
        if (ex.getRequiredPermission() != null) {
            problemDetail.setProperty("requiredPermission", ex.getRequiredPermission());
        }
        if (ex.getResource() != null) {
            problemDetail.setProperty("resource", ex.getResource());
        }
        
        return problemDetail;
    }

    /**
     * Handles UnauthorizedException (HTTP 401).
     * Returns generic authentication failure message.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Authentication failed"
        );
        problemDetail.setTitle("Unauthorized");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    // ========================================================================
    // Spring Framework Exception Handlers
    // ========================================================================

    /**
     * Handles Spring's MethodArgumentNotValidException (HTTP 400).
     * Converts Spring validation errors to RFC 9457 format with field details.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        log.debug("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        
        // Extract field errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for one or more fields"
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("fieldErrors", fieldErrors);
        
        return ResponseEntity.badRequest().body(problemDetail);
    }

    /**
     * Handles Spring Security's AccessDeniedException (HTTP 403).
     * Returns generic access denied message.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Spring Security access denied: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            "Access denied"
        );
        problemDetail.setTitle("Forbidden");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }

    // ========================================================================
    // Fallback Exception Handler
    // ========================================================================

    /**
     * Handles all other unexpected exceptions (HTTP 500).
     * Returns generic error message to avoid information leakage.
     * Logs full stack trace for debugging.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return problemDetail;
    }
}
