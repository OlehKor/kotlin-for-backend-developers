package com.epam.mentoring.kotlin

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {
    @Bean
    @Primary
    fun dogBreedService(): DogBreedService = mockk(relaxed = true)
} 