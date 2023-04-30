package com.epam.mentoring.kotlin;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dog Breed APIs", description = "Breed APIs for demo purpose")
@RestController()
@RequestMapping("v1/breeds")
public class DogBreedController {

    private final DogBreedService dogBreedService;

    public DogBreedController(final DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    @GetMapping()
    public List<DogBreedResponse> getAllDogBreeds() {
        return dogBreedService.getBreeds()
                .stream()
                .map(dogBreed -> new DogBreedResponse(dogBreed.getBreed(), convertSubBreeds(dogBreed.getSubBreed())))
                .toList();
    }

    private List<String> convertSubBreeds(final String subBreed) {
        if (StringUtils.isNotBlank(subBreed)) {
            return Arrays.asList(subBreed.split(","));
        }
        return null;
    }
}
