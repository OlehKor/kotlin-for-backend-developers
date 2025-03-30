package com.epam.mentoring.kotlin

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DogBreedRepository : CoroutineCrudRepository<DogBreed, Long> {
    suspend fun findAllByBreed(breed: String): List<DogBreed>
    suspend fun findByBreedAndSubBreedIsNull(breed: String): DogBreed?
    suspend fun findByBreedAndSubBreedIsNotNull(breed: String): List<DogBreed>
    suspend fun findAllBySubBreedIsNotNull(): List<DogBreed>
    suspend fun findAllBySubBreedIsNull(): List<DogBreed>
} 