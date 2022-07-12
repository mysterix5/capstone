package com.github.mysterix5.capstone.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WordInput {
    private String word;
    private List<WordTag> tags;
}
