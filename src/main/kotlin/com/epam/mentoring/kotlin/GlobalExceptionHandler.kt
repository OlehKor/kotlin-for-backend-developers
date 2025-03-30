package com.epam.mentoring.kotlin

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

/**
 * Global exception handler for the application.
 * Provides consistent error responses across all controllers.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    /**
     * Handles BreedNotFoundException and returns a 404 Not Found response.
     */
    @ExceptionHandler(BreedNotFoundException::class)
    fun handleBreedNotFoundException(
        ex: BreedNotFoundException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("Breed not found: {}", ex.message)
        return createErrorResponse(
            status = HttpStatus.NOT_FOUND,
            error = "Not Found",
            message = ex.message ?: "Breed not found",
            path = exchange.request.path.toString()
        )
    }
    
    /**
     * Handles WebClientResponseException and returns an appropriate error response.
     */
    @ExceptionHandler(WebClientResponseException::class)
    fun handleWebClientResponseException(
        ex: WebClientResponseException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("API client error: {}", ex.message)
        return createErrorResponse(
            status = HttpStatus.valueOf(ex.statusCode.value()),
            error = ex.statusText,
            message = "Error communicating with external API: ${ex.message}",
            path = exchange.request.path.toString()
        )
    }
    
    /**
     * Handles ResponseStatusException and returns the appropriate status code.
     */
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("Response status exception: {}", ex.message)
        return createErrorResponse(
            status = HttpStatus.valueOf(ex.statusCode.value()),
            error = ex.reason ?: "Error",
            message = ex.message ?: "An error occurred",
            path = exchange.request.path.toString()
        )
    }
    
    /**
     * Fallback handler for any unhandled exceptions.
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        exchange: ServerWebExchange
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception", ex)
        return createErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            error = "Internal Server Error",
            message = "An unexpected error occurred",
            path = exchange.request.path.toString()
        )
    }
    
    /**
     * Helper method to create consistent error responses.
     */
    private fun createErrorResponse(
        status: HttpStatus,
        error: String,
        message: String,
        path: String
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = System.currentTimeMillis(),
            status = status.value(),
            error = error,
            message = message,
            path = path
        )
        return ResponseEntity(errorResponse, status)
    }
}

/**
 * Standardized error response format.
 */
data class ErrorResponse(
    val timestamp: Long,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
) 