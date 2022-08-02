package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{id}")
    public ResponseEntity<HistoryEntry> getHistoryEntryForPrimary(@PathVariable String id){
        return ResponseEntity.ok(historyService.getById(id));
    }
}
