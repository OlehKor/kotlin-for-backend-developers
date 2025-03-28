package com.epam.mentoring.kotlin

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder

class GlobalExceptionHandlerTest {

    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var mockExchange: ServerWebExchange
    
    @BeforeEach
    fun setup() {
        exceptionHandler = GlobalExceptionHandler()
        val request = MockServerHttpRequest.get("/api/test").build()
        mockExchange = MockServerWebExchange.from(request)
    }
    
    @Test
    fun `handleBreedNotFoundException returns correct error response`() {
        // Given
        val breedNotFoundException = BreedNotFoundException("husky")
        
        // When
        val responseEntity = exceptionHandler.handleBreedNotFoundException(breedNotFoundException, mockExchange)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        val errorResponse = responseEntity.body!!
        assertEquals(404, errorResponse.status)
        assertEquals("Not Found", errorResponse.error)
        assertEquals("Breed not found: husky", errorResponse.message)
        assertEquals("/api/test", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }
    
    @Test
    fun `handleWebClientResponseException returns correct error response`() {
        // Given
        val webClientException = mockk<WebClientResponseException>()
        every { webClientException.statusCode } returns HttpStatus.BAD_GATEWAY
        every { webClientException.statusText } returns "Bad Gateway"
        every { webClientException.message } returns "External API error"
        
        // When
        val responseEntity = exceptionHandler.handleWebClientResponseException(webClientException, mockExchange)
        
        // Then
        assertEquals(HttpStatus.BAD_GATEWAY, responseEntity.statusCode)
        val errorResponse = responseEntity.body!!
        assertEquals(502, errorResponse.status)
        assertEquals("Bad Gateway", errorResponse.error)
        assertTrue(errorResponse.message.contains("External API error"))
        assertEquals("/api/test", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }
    
    @Test
    fun `handleResponseStatusException returns correct error response`() {
        // Given
        val statusException = mockk<ResponseStatusException>()
        every { statusException.statusCode } returns HttpStatus.UNAUTHORIZED
        every { statusException.reason } returns "Unauthorized"
        every { statusException.message } returns "Access denied"
        
        // When
        val responseEntity = exceptionHandler.handleResponseStatusException(statusException, mockExchange)
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
        val errorResponse = responseEntity.body!!
        assertEquals(401, errorResponse.status)
        assertEquals("Unauthorized", errorResponse.error)
        assertEquals("Access denied", errorResponse.message)
        assertEquals("/api/test", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }
    
    @Test
    fun `handleGenericException returns correct error response`() {
        // Given
        val exception = RuntimeException("Something went wrong")
        
        // When
        val responseEntity = exceptionHandler.handleGenericException(exception, mockExchange)
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.statusCode)
        val errorResponse = responseEntity.body!!
        assertEquals(500, errorResponse.status)
        assertEquals("Internal Server Error", errorResponse.error)
        assertEquals("An unexpected error occurred", errorResponse.message)
        assertEquals("/api/test", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }
} 