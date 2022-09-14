package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
@Slf4j
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{id}")
    public ResponseEntity<HistoryEntry> getHistoryEntryForPrimary(@PathVariable String id, Principal principal){
        log.info("user '{}' requests history entry with id '{}'", principal.getName(), id);
        return ResponseEntity.ok(historyService.getById(id));
    }
}
