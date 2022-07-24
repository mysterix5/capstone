package com.github.mysterix5.vover.model.word;

import lombok.Data;

@Data
public class RecordPageSubmitDTO {
    private int page;
    private int size;
    private String searchTerm;
}
