package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/userdetails")
public class VoverUserDetailsController {
    private final VoverUserDetailsService voverUserDetailsService;

    @GetMapping("/history")
    public ResponseEntity<List<HistoryEntry>> getHistory(Principal principal) {
        return ResponseEntity.ok(voverUserDetailsService.getHistory(principal.getName()));
    }
}
