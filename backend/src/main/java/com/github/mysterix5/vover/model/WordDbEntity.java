package com.github.mysterix5.vover.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "words")
public class WordDbEntity {
    private String word;
    private List<WordTag> tags;
    private String url;
    private String creator;

    public WordDbEntity(WordInput wordInput){
        word = wordInput.getWord().toLowerCase();
        tags = wordInput.getTags();
    }
}
