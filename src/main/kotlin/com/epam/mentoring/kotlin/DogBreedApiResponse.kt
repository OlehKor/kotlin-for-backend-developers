package com.epam.mentoring.kotlin

data class DogBreedApiResponse(
    val message: Map<String, List<String>>,
    val status: String
)

data class DogBreedImageResponse(
    val message: String,
    val status: String
) 