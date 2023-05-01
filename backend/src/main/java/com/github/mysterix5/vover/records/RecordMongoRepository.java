package com.github.mysterix5.vover.records;

import com.github.mysterix5.vover.model.record.RecordDbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RecordMongoRepository extends MongoRepository<RecordDbEntity, String> {
    List<RecordDbEntity> findByWordIn(Set<String> words);
    List<RecordDbEntity> findAllByCreator(String creator);
    Page<RecordDbEntity> findByCreatorAndWordLike(String creator, String word, Pageable paging);
}
