package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.model.WordDbEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordsMongoRepository extends MongoRepository<WordDbEntity, String> {
    boolean existsByWord(String word);
}
