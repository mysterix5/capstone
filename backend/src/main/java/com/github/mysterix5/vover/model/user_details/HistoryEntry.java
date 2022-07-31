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
}
