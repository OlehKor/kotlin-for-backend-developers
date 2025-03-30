package com.epam.mentoring.kotlin

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class DogBreedApiClient(
    private val webClient: WebClient
) {
    companion object {
        private const val BASE_URL = "https://dog.ceo/api"
    }

    suspend fun getAllBreeds(): DogBreedApiResponse {
        return webClient.get()
            .uri("$BASE_URL/breeds/list/all")
            .retrieve()
            .awaitBody()
    }

    suspend fun getBreedImage(breed: String): DogBreedImageResponse {
        return webClient.get()
            .uri("$BASE_URL/breed/$breed/images/random")
            .retrieve()
            .awaitBody()
    }
} 