package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryMongoRepository historyRepository;
    public HistoryEntry save(HistoryEntry historyEntry){
        return historyRepository.save(historyEntry);
    }

    public List<HistoryEntry> getAllByIds(List<String> historyIds) {
        return historyRepository.findAllByIdIn(historyIds);
    }
}
