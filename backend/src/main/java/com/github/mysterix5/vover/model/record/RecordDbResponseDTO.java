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

    public RecordDbResponseDTO(RecordDbEntity recordDb) {
        id = recordDb.getId();
        word = recordDb.getWord();
        creator = recordDb.getCreator();
        tag = recordDb.getTag();
    }
}
