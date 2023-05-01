package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryMongoRepository historyRepository;

    public void save(VoverUserDetails userDetails, HistoryEntry historyEntry) {

        List<HistoryEntry> userHistory = historyRepository.findAllByIdIn(userDetails.getHistory(), null);
        Optional<HistoryEntry> old = userHistory.stream()
                .filter(historyEntry1 -> historyEntry1.sameRequest(historyEntry))
                .findFirst();
        old.ifPresentOrElse(
                h -> {
                    h.setRequestTime(LocalDateTime.now());
                    historyRepository.save(h);
                },
                () -> historyRepository.save(historyEntry)
        );

        userDetails.getHistory().add(historyEntry.getId());
    }

    public HistoryEntry getById(String id) {
        return historyRepository.findById(id).orElseThrow();
    }

    public List<HistoryEntry> getAllByIds(List<String> historyIds) {
        return historyRepository.findAllByIdIn(historyIds, Sort.by(Sort.Direction.ASC, "requestTime"));
    }
}
