package com.github.mysterix5.vover.model.word;

import com.github.mysterix5.vover.model.other.Availability;
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
    private Availability availability;

    public WordResponseDTO(String word){
        this.word = word;
    }
}
