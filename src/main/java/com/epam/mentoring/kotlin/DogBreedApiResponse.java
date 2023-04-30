package com.epam.mentoring.kotlin;

import java.util.List;
import java.util.Map;

public record DogBreedApiResponse(Map<String, List<String>> message) {
}

