package com.github.mysterix5.vover.model.primary;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PrimarySubmitDTO {
    private String text;
    private List<String> scope;
}
