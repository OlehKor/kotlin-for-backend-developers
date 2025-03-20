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
    }
} 