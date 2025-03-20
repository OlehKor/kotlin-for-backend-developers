package com.epam.mentoring.kotlin

import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.net.URL

class DogBreedServiceTest {
    private val repository: DogBreedRepository = mockk()
    private val apiClient: DogBreedApiClient = mockk()
    private lateinit var service: DogBreedService

    @BeforeEach
    fun setup() {
        service = DogBreedService(repository, apiClient)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllBreeds returns all breeds from repository`() = runTest {
        val breeds = listOf(
            DogBreed(id = 1, breed = "husky"),
            DogBreed(id = 2, breed = "labrador")
        )
        coEvery { repository.findAll() } returns flowOf(*breeds.toTypedArray())

        val result = service.getAllBreeds().toList()

        assertEquals(breeds, result)
        coVerify { repository.findAll() }
    }

    @Test
    fun `getAllSubBreeds returns breeds with sub-breeds`() = runTest {
        val subBreeds = listOf(
            DogBreed(id = 1, breed = "shepherd", subBreed = "german"),
            DogBreed(id = 2, breed = "shepherd", subBreed = "swiss")
        )
        coEvery { repository.findAllBySubBreedIsNotNull() } returns subBreeds

        val result = service.getAllSubBreeds()

        assertEquals(subBreeds, result)
        coVerify { repository.findAllBySubBreedIsNotNull() }
    }

    @Test
    fun `getBreedsWithoutSubBreeds returns breeds without sub-breeds`() = runTest {
        val breeds = listOf(
            DogBreed(id = 1, breed = "husky"),
            DogBreed(id = 2, breed = "pug")
        )
        coEvery { repository.findAllBySubBreedIsNull() } returns breeds

        val result = service.getBreedsWithoutSubBreeds()

        assertEquals(breeds, result)
        coVerify { repository.findAllBySubBreedIsNull() }
    }

    @Test
    fun `getBreedImage returns cached image if available`() = runTest {
        val breed = "husky"
        val imageBytes = byteArrayOf(1, 2, 3)
        val dogBreed = DogBreed(id = 1, breed = breed, image = imageBytes)
        
        coEvery { repository.findByBreedAndSubBreedIsNull(breed) } returns dogBreed
        coEvery { repository.findByBreedAndSubBreedIsNotNull(breed) } returns emptyList()

        val result = service.getBreedImage(breed)

        assertEquals(imageBytes, result)
        coVerify { 
            repository.findByBreedAndSubBreedIsNull(breed)
            repository.findByBreedAndSubBreedIsNotNull(breed) wasNot Called
        }
    }

    @Test
    fun `getBreedImage fetches and saves new image if not cached`() = runTest {
        val breed = "husky"
        val imageBytes = byteArrayOf(1, 2, 3)
        val dogBreed = DogBreed(id = 1, breed = breed)
        val imageResponse = DogBreedImageResponse(message = "http://example.com/image.jpg", status = "success")
        
        coEvery { repository.findByBreedAndSubBreedIsNull(breed) } returns dogBreed
        coEvery { repository.findByBreedAndSubBreedIsNotNull(breed) } returns emptyList()
        coEvery { apiClient.getBreedImage(breed) } returns imageResponse
        coEvery { repository.save(any()) } returns dogBreed.copy(image = imageBytes)

        mockkStatic(URL::class)
        every { any<URL>().readBytes() } returns imageBytes

        val result = service.getBreedImage(breed)

        assertEquals(imageBytes, result)
        coVerify { 
            repository.findByBreedAndSubBreedIsNull(breed)
            repository.findByBreedAndSubBreedIsNotNull(breed) wasNot Called
            apiClient.getBreedImage(breed)
            repository.save(any())
        }
        verify { any<URL>().readBytes() }

        unmockkStatic(URL::class)
    }

    @Test
    fun `getBreedImage throws BreedNotFoundException when breed not found`() = runTest {
        val breed = "nonexistent"
        coEvery { repository.findByBreedAndSubBreedIsNull(breed) } returns null
        coEvery { repository.findByBreedAndSubBreedIsNotNull(breed) } returns emptyList()

        assertThrows<BreedNotFoundException> {
            service.getBreedImage(breed)
        }

        coVerify { 
            repository.findByBreedAndSubBreedIsNull(breed)
            repository.findByBreedAndSubBreedIsNotNull(breed)
        }
    }

    @Test
    fun `getBreedImage throws BreedNotFoundException when API call fails`() = runTest {
        val breed = "husky"
        val dogBreed = DogBreed(id = 1, breed = breed)
        
        coEvery { repository.findByBreedAndSubBreedIsNull(breed) } returns dogBreed
        coEvery { repository.findByBreedAndSubBreedIsNotNull(breed) } returns emptyList()
        coEvery { apiClient.getBreedImage(breed) } throws mockk<WebClientResponseException>()

        assertThrows<BreedNotFoundException> {
            service.getBreedImage(breed)
        }

        coVerify { 
            repository.findByBreedAndSubBreedIsNull(breed)
            repository.findByBreedAndSubBreedIsNotNull(breed) wasNot Called
            apiClient.getBreedImage(breed)
        }
    }
} 