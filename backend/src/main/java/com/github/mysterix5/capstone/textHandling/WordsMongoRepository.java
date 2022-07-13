package com.github.mysterix5.capstone.textHandling;

import com.github.mysterix5.capstone.model.WordDbEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WordsMongoRepository extends MongoRepository<WordDbEntity, String> {
    boolean existsByWord(String word);

    Optional<WordDbEntity> findByWord(String word);
}
