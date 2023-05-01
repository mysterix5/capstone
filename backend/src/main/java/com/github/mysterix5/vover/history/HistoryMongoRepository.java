package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryMongoRepository extends MongoRepository<HistoryEntry, String> {
    List<HistoryEntry> findAllByIdIn(List<String> historyIds, Sort sort);
}
