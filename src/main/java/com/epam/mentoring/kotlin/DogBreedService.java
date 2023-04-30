package com.epam.mentoring.kotlin;

import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DogBreedService {
    private final DogBreedRepository dogBreedRepository;

    public DogBreedService(final DogBreedRepository dogBreedRepository) {
        this.dogBreedRepository = dogBreedRepository;
    }

    public void save(final Map<String, List<String>> breeds) {
        List<DogBreed> dogBreeds = breeds.entrySet()
                .stream()
                .map(this::toDogBreed)
                .toList();
        dogBreedRepository.saveAll(dogBreeds);
    }

    @Cacheable("breeds")
    public List<DogBreed> getBreeds() {
        return (List<DogBreed>) dogBreedRepository.findAll();
    }

    private DogBreed toDogBreed(final Map.Entry<String, List<String>> entry) {
        final String subBreeds = String.join(",", entry.getValue());
        return new DogBreed(null, entry.getKey(), subBreeds, null);
    }
}
