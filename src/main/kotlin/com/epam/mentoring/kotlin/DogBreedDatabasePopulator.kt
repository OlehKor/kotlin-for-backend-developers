package com.epam.mentoring.kotlin

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DogBreedDatabasePopulator(
    private val repository: DogBreedRepository,
    private val apiClient: DogBreedApiClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun populateDatabase() = runBlocking {
        try {
            logger.info("Checking if database needs to be populated...")
            val count = repository.count()
            logger.info("Current breed count in database: {}", count)

            if (count == 0L) {
                logger.info("Database is empty, fetching breeds from API...")
                val response = apiClient.getAllBreeds()
                logger.info("Received {} breeds from API", response.message.size)

                response.message.forEach { (breed, subBreeds) ->
                    if (subBreeds.isEmpty()) {
                        logger.debug("Saving breed without sub-breeds: {}", breed)
                        repository.save(DogBreed(breed = breed))
                    } else {
                        logger.debug("Saving breed {} with {} sub-breeds", breed, subBreeds.size)
                        subBreeds.forEach { subBreed ->
                            repository.save(DogBreed(breed = breed, subBreed = subBreed))
                        }
                    }
                }
                logger.info("Database population completed successfully")
            } else {
                logger.info("Database already populated, skipping initialization")
            }
        } catch (e: Exception) {
            logger.error("Error during database population", e)
            throw e
        }
    }
} 