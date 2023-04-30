package com.epam.mentoring.kotlin;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DogBreedRepository extends CrudRepository<DogBreed, Long> {
}
