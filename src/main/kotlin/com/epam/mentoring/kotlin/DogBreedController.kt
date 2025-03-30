package com.epam.mentoring.kotlin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/breeds")
class DogBreedController(private val service: DogBreedService) {

    @Operation(summary = "Get all dog breeds")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved breeds"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping
    suspend fun getAllBreeds(): Flow<DogBreed> = service.getAllBreeds()

    @Operation(summary = "Get all sub-breeds")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved sub-breeds"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/sub-breeds")
    suspend fun getAllSubBreeds(): List<DogBreed> = service.getAllSubBreeds()

    @Operation(summary = "Get breeds without sub-breeds")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved breeds without sub-breeds"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/no-sub-breeds")
    suspend fun getBreedsWithoutSubBreeds(): List<DogBreed> = service.getBreedsWithoutSubBreeds()

    @Operation(summary = "Get sub-breeds for a specific breed")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved sub-breeds"),
        ApiResponse(responseCode = "404", description = "Breed not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/{breed}/sub-breeds")
    suspend fun getBreedSubBreeds(@PathVariable breed: String): List<DogBreed> = 
        service.getBreedSubBreeds(breed)

    @Operation(summary = "Get image for a specific breed")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved breed image"),
        ApiResponse(responseCode = "404", description = "Breed not found"),
        ApiResponse(responseCode = "406", description = "Not Acceptable - Client doesn't accept JPEG images"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/{breed}/image", produces = [MediaType.IMAGE_JPEG_VALUE])
    suspend fun getBreedImage(@PathVariable breed: String): ResponseEntity<ByteArray> {
        val imageBytes = service.getBreedImage(breed)
        return ResponseEntity.ok().body(imageBytes)
    }
} 