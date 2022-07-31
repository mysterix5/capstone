package com.github.mysterix5.vover.model.user_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntry {
    private String text;
    private LocalDateTime requestTime;
}
