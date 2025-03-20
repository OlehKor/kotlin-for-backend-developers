package com.epam.mentoring.kotlin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.net.URL

@Service
class DogBreedService(
    private val repository: DogBreedRepository,
    private val apiClient: DogBreedApiClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Cacheable("breeds")
    suspend fun getAllBreeds(): Flow<DogBreed> = flow {
        try {
            logger.info("Fetching all breeds from database")
            repository.findAll().collect { breed ->
                logger.debug("Emitting breed: {}", breed)
                emit(breed)
            }
            logger.info("Successfully fetched all breeds")
        } catch (e: Exception) {
            logger.error("Error fetching breeds", e)
            throw e
        }
    }

    @Cacheable("subBreeds")
    suspend fun getAllSubBreeds(): List<DogBreed> {
        try {
            logger.info("Fetching all sub-breeds")
            val subBreeds = repository.findAllBySubBreedIsNotNull()
            logger.info("Found {} sub-breeds", subBreeds.size)
            return subBreeds
        } catch (e: Exception) {
            logger.error("Error fetching sub-breeds", e)
            throw e
        }
    }

    @Cacheable("breedsWithoutSubBreeds")
    suspend fun getBreedsWithoutSubBreeds(): List<DogBreed> {
        try {
            logger.info("Fetching breeds without sub-breeds")
            val breeds = repository.findAllBySubBreedIsNull()
            logger.info("Found {} breeds without sub-breeds", breeds.size)
            return breeds
        } catch (e: Exception) {
            logger.error("Error fetching breeds without sub-breeds", e)
            throw e
        }
    }

    @Cacheable("breedSubBreeds")
    suspend fun getBreedSubBreeds(breed: String): List<DogBreed> {
        try {
            logger.info("Fetching sub-breeds for breed: {}", breed)
            val breedExists = repository.findByBreedAndSubBreedIsNull(breed)
                ?: repository.findByBreedAndSubBreedIsNotNull(breed).firstOrNull()
                ?: throw BreedNotFoundException(breed)
            
            val subBreeds = repository.findByBreedAndSubBreedIsNotNull(breed)
            logger.info("Found {} sub-breeds for breed {}", subBreeds.size, breed)
            return subBreeds
        } catch (e: Exception) {
            logger.error("Error fetching sub-breeds for breed: {}", breed, e)
            throw e
        }
    }

    @Cacheable("breedImages")
    suspend fun getBreedImage(breed: String): ByteArray {
        try {
            logger.info("Fetching image for breed: {}", breed)
            val dogBreed = repository.findByBreedAndSubBreedIsNull(breed)
                ?: repository.findByBreedAndSubBreedIsNotNull(breed).firstOrNull()
                ?: throw BreedNotFoundException(breed)
            
            if (dogBreed.image != null) {
                logger.info("Found cached image for breed: {}", breed)
                return dogBreed.image!!
            }

            logger.info("No cached image found, fetching from API for breed: {}", breed)
            val imageResponse = apiClient.getBreedImage(breed)
            val imageBytes = URL(imageResponse.message).readBytes()
            dogBreed.image = imageBytes
            repository.save(dogBreed)
            logger.info("Successfully saved image for breed: {}", breed)
            return imageBytes
        } catch (e: WebClientResponseException) {
            logger.error("API error fetching image for breed: {}", breed, e)
            throw BreedNotFoundException(breed)
        } catch (e: Exception) {
            logger.error("Error fetching image for breed: {}", breed, e)
            throw e
        }
    }
}

class BreedNotFoundException(breed: String) : RuntimeException("Breed not found: $breed") 