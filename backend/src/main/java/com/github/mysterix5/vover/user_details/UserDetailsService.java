package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsService {
    private final UserDetailsMongoRepository userDetailsRepository;

    public void addRequestToHistory(String username, List<String> wordList) {
        String text = String.join(" ", wordList);
        LocalDateTime now = LocalDateTime.now();
        var historyEntry = new HistoryEntry(text, now);
        addHistoryEntry(username, historyEntry);
        log.info("text '{}' is added to history of user '{}'", text, username);
    }

    private void addHistoryEntry(String username, HistoryEntry historyEntry){
        VoverUserDetails voverUserDetails = userDetailsRepository.findByUsername(username).orElseThrow();
        voverUserDetails.getHistory().add(historyEntry);
        userDetailsRepository.save(voverUserDetails);
    }
}
