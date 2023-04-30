package com.epam.mentoring.kotlin;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DogBreedDatabasePopulator {
    private final DogBreedApiClient dogBreedApiClient;
    private final DogBreedService dogBreedService;

    public DogBreedDatabasePopulator(final DogBreedApiClient dogBreedApiClient, final DogBreedService dogBreedService) {
        this.dogBreedApiClient = dogBreedApiClient;
        this.dogBreedService = dogBreedService;
    }

    @PostConstruct
    public void initializeDatabase() throws Exception {
        if (dogBreedService.getBreeds().isEmpty()) {
            Map<String, List<String>> breeds = dogBreedApiClient.getBreeds();
            dogBreedService.save(breeds);
        }
    }

}
