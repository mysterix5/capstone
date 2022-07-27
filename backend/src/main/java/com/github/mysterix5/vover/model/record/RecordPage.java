package com.github.mysterix5.vover.model.record;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecordPage {
    private int page;
    private int noPages;
    private int size;
    private String searchTerm;
    private List<RecordManagementDTO> records;
    private Accessibility[] accessibilityChoices;
}
