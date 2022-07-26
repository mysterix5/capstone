package com.github.mysterix5.vover.main;

import com.github.mysterix5.vover.model.word.WordDbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface WordsMongoRepository extends MongoRepository<WordDbEntity, String> {
    List<WordDbEntity> findByWordIn(Set<String> words);


    Page<WordDbEntity> findAllByCreator(String creator, Pageable paging);
}
