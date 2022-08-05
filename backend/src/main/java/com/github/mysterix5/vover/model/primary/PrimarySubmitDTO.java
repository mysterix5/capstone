package com.github.mysterix5.vover.model.primary;

import lombok.Data;

import java.util.List;

@Data
public class PrimarySubmitDTO {
    private String text;
    private List<String> scope;
}
