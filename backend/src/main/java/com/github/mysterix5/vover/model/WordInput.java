package com.github.mysterix5.vover.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordInput {
    private String word;
    private String creator;
    private String tag;
}
