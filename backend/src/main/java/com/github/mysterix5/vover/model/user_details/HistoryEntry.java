package com.github.mysterix5.vover.model.user_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "history")
public class HistoryEntry {
    @Id
    private String id;
    private String text;
    private List<String> choices;
    private LocalDateTime requestTime;

    public boolean sameRequest(HistoryEntry historyEntry) {
        if (this == historyEntry) return true;
        if (this.choices.size() != historyEntry.choices.size()) return false;
        for (int i = 0; i < this.choices.size(); i++) {
            if (!this.choices.get(i).equals(historyEntry.choices.get(i))) {
                return false;
            }
        }
        return text.equals(historyEntry.text);
    }
}
