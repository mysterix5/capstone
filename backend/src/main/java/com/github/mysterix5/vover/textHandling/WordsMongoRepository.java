package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.model.WordDbEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface WordsMongoRepository extends MongoRepository<WordDbEntity, String> {
    List<WordDbEntity> findByWordIn(Set<String> words);
}
