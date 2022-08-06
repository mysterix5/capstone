package com.github.mysterix5.vover.model.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordDbResponseDTO {
    private String id;
    private String word;
    private String creator;
    private String tag;
    private RecordAvailability availability;

    public RecordDbResponseDTO(RecordDbEntity recordDb, RecordAvailability availability) {
        id = recordDb.getId();
        word = recordDb.getWord();
        creator = recordDb.getCreator();
        tag = recordDb.getTag();
        this.availability = availability;
    }
}
