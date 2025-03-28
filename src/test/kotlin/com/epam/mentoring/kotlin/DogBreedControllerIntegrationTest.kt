package com.epam.mentoring.kotlin

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestConfig::class)
class DogBreedControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var service: DogBreedService

    private val testBreeds = listOf(
        DogBreed(id = 1, breed = "husky"),
        DogBreed(id = 2, breed = "labrador")
    )

    private val testSubBreeds = listOf(
        DogBreed(id = 3, breed = "shepherd", subBreed = "german"),
        DogBreed(id = 4, breed = "shepherd", subBreed = "swiss")
    )

    @Test
    fun `getAllBreeds returns all breeds`() {
        coEvery { service.getAllBreeds() } returns flowOf(*testBreeds.toTypedArray())

        webTestClient.get()
            .uri("/api/breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreed::class.java)
            .hasSize(2)
            .contains(*testBreeds.toTypedArray())
    }

    @Test
    fun `getAllSubBreeds returns all sub-breeds`() {
        coEvery { service.getAllSubBreeds() } returns testSubBreeds

        webTestClient.get()
            .uri("/api/breeds/sub-breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreed::class.java)
            .hasSize(2)
            .contains(*testSubBreeds.toTypedArray())
    }

    @Test
    fun `getBreedsWithoutSubBreeds returns breeds without sub-breeds`() {
        coEvery { service.getBreedsWithoutSubBreeds() } returns testBreeds

        webTestClient.get()
            .uri("/api/breeds/no-sub-breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreed::class.java)
            .hasSize(2)
            .contains(*testBreeds.toTypedArray())
    }

    @Test
    fun `getBreedSubBreeds returns sub-breeds for a specific breed`() {
        coEvery { service.getBreedSubBreeds("shepherd") } returns testSubBreeds

        webTestClient.get()
            .uri("/api/breeds/shepherd/sub-breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreed::class.java)
            .hasSize(2)
            .contains(*testSubBreeds.toTypedArray())
    }

    @Test
    fun `getBreedImage returns image for a specific breed`() {
        val imageBytes = byteArrayOf(1, 2, 3)
        coEvery { service.getBreedImage("husky") } returns imageBytes

        webTestClient.get()
            .uri("/api/breeds/husky/image")
            .accept(MediaType.IMAGE_JPEG)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response ->
                assert(response.responseBody!!.contentEquals(imageBytes))
            }
    }

    @Test
    fun `getBreedImage returns 404 when breed not found`() {
        coEvery { service.getBreedImage("nonexistent") } throws BreedNotFoundException("nonexistent")

        webTestClient.get()
            .uri("/api/breeds/nonexistent/image")
            .accept(MediaType.IMAGE_JPEG)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not Found")
            .jsonPath("$.message").isEqualTo("Breed not found: nonexistent")
            .jsonPath("$.path").isEqualTo("/api/breeds/nonexistent/image")
            .jsonPath("$.timestamp").exists()
    }
    
    @Test
    fun `getBreedSubBreeds returns 404 when breed not found`() {
        coEvery { service.getBreedSubBreeds("nonexistent") } throws BreedNotFoundException("nonexistent")

        webTestClient.get()
            .uri("/api/breeds/nonexistent/sub-breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not Found")
            .jsonPath("$.message").isEqualTo("Breed not found: nonexistent")
            .jsonPath("$.path").isEqualTo("/api/breeds/nonexistent/sub-breeds")
            .jsonPath("$.timestamp").exists()
    }
    
    @Test
    fun `getBreedImage returns 500 when WebClientResponseException occurs`() {
        val mockException = mockk<WebClientResponseException>()
        coEvery { mockException.statusCode } returns org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
        coEvery { mockException.statusText } returns "API Error"
        coEvery { mockException.message } returns "Error fetching image"
        
        coEvery { service.getBreedImage("error") } throws mockException

        webTestClient.get()
            .uri("/api/breeds/error/image")
            .accept(MediaType.IMAGE_JPEG)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody()
            .jsonPath("$.status").isEqualTo(500)
            .jsonPath("$.error").isEqualTo("API Error")
            .jsonPath("$.message").value<String> { message -> 
                assert(message.contains("Error fetching image"))
                true
            }
            .jsonPath("$.path").isEqualTo("/api/breeds/error/image")
            .jsonPath("$.timestamp").exists()
    }
    
    @Test
    fun `getAllBreeds returns 500 when generic exception occurs`() {
        coEvery { service.getAllBreeds() } throws RuntimeException("Database error")

        webTestClient.get()
            .uri("/api/breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody()
            .jsonPath("$.status").isEqualTo(500)
            .jsonPath("$.error").isEqualTo("Internal Server Error")
            .jsonPath("$.message").isEqualTo("An unexpected error occurred")
            .jsonPath("$.path").isEqualTo("/api/breeds")
            .jsonPath("$.timestamp").exists()
    }
} 