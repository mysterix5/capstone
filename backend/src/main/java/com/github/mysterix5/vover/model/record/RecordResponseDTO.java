package com.github.mysterix5.vover.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordResponseDTO {
    private String word;
    private Availability availability;

    public RecordResponseDTO(String word){
        this.word = word;
    }
}
