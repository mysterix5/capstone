package com.github.mysterix5.vover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordResponseDTO {
    private String word;
    private Availability availability;

    public WordResponseDTO(String word){
        this.word = word;
    }
}
