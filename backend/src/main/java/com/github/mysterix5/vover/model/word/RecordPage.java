package com.github.mysterix5.vover.model.word;

import com.github.mysterix5.vover.model.other.Accessibility;
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
