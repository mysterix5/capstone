package com.github.mysterix5.vover.model.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordPageSubmitDTO {
    private int page;
    private int size;
    private String searchTerm;
}
