package com.epam.mentoring.kotlin;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DogBreedResponse(String breed, List<String> subBreeds) {
}