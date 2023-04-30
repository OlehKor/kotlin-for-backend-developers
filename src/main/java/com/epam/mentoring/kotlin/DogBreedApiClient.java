package com.epam.mentoring.kotlin;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DogBreedApiClient {
    private static final String DOG_BREED_API_URL = "https://dog.ceo/api/breeds/list/all";
    private final RestTemplate restTemplate;

    public DogBreedApiClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, List<String>> getBreeds() throws Exception {
        ResponseEntity<DogBreedApiResponse> response = restTemplate.getForEntity(DOG_BREED_API_URL, DogBreedApiResponse.class);
        if(200 != response.getStatusCode().value()) {
            throw new Exception();
        }
        return response.getBody().message();
    }
}
