package com.github.mysterix5.vover.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordManagementDTO {
    private String id;
    private String word;
    private String tag;
    private Accessibility accessibility;

    public RecordManagementDTO(RecordDbEntity wordDb){
        id = wordDb.getId();
        word = wordDb.getWord();
        tag = wordDb.getTag();
        accessibility = wordDb.getAccessibility();
    }
}
