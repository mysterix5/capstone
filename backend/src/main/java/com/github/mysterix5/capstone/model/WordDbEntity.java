package com.github.mysterix5.capstone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "words")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordDbEntity {
    private String word;
    private List<WordTag> tags;
    private String url;
    private String creator;

    public WordDbEntity(WordInput wordInput){
        word = wordInput.getWord().toLowerCase();
        tags = wordInput.getTags();
        url = word + ".wav";
    }
}
