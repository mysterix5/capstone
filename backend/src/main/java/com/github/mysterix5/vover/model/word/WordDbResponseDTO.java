package com.github.mysterix5.vover.model.word;

import lombok.Data;

@Data
public class WordDbResponseDTO {
    private String id;
    private String word;
    private String creator;
    private String tag;

    public WordDbResponseDTO (WordDbEntity wordDb) {
        id = wordDb.getId();
        word = wordDb.getWord();
        creator = wordDb.getCreator();
        tag = wordDb.getTag();
    }
}
