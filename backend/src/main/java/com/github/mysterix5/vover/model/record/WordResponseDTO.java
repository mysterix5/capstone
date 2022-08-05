package com.github.mysterix5.vover.model.record;

import com.github.mysterix5.vover.model.primary.WordAvailability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordResponseDTO {
    private String word;
    private WordAvailability availability;

    public WordResponseDTO(String word){
        this.word = word;
    }
}
