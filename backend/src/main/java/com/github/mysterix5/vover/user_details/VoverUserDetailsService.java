package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.history.HistoryService;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoverUserDetailsService {
    private final VoverUserDetailsMongoRepository userDetailsRepository;
    private final HistoryService historyService;

    public void addRequestToHistory(String username, List<RecordDbEntity> recordList) {
        String text = String.join(" ",
                recordList.stream()
                        .map(RecordDbEntity::getWord)
                        .toList()
                );
        List<String> ids = recordList.stream()
                .map(RecordDbEntity::getId)
                .toList();
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setText(text);
        historyEntry.setChoices(ids);
        historyEntry.setRequestTime(LocalDateTime.now());

        historyEntry = historyService.save(historyEntry);

        VoverUserDetails voverUserDetails = getUserDetails(username);
        voverUserDetails.getHistory().add(historyEntry.getId());
        userDetailsRepository.save(voverUserDetails);
        log.info("text '{}' is added to history of user '{}'", text, username);
    }

    public List<HistoryEntry> getHistory(String username) {
        List<String> historyIds = getUserDetails(username).getHistory();
        return historyService.getAllByIds(historyIds);
    }

    private VoverUserDetails getUserDetails(String username) {
        return userDetailsRepository.findById(username).orElse(new VoverUserDetails(username));
    }

}
