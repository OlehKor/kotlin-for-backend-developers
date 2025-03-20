package com.epam.mentoring.kotlin

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class Config {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun webClient(): WebClient = WebClient.builder()
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .filter(logRequest())
        .filter(logResponse())
        .build()

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            clientRequest.headers().forEach { name, values ->
                values.forEach { value ->
                    logger.debug("{}={}", name, value)
                }
            }
            Mono.just(clientRequest)
        }
    }

    private fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            logger.info("Response status: {}", clientResponse.statusCode())
            Mono.just(clientResponse)
        }
    }

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(Info()
            .title("Dog Breed API")
            .description("API for retrieving information about dog breeds")
            .version("1.0")
        )
} 