package com.epam.mentoring.kotlin

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val status: Int,
        val message: String
    )

    @ExceptionHandler(BreedNotFoundException::class)
    fun handleBreedNotFoundException(ex: BreedNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message ?: "Breed not found")
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(WebClientResponseException::class)
    fun handleWebClientResponseException(ex: WebClientResponseException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(ex.statusCode.value(), ex.message ?: "External API error")
        return ResponseEntity(error, ex.statusCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
} 