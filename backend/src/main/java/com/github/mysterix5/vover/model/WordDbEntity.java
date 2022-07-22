package com.github.mysterix5.vover.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@Document(collection = "words")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordDbEntity {
    @Id
    private String id;
    private String word;
    private String creator;
    private String tag;
    private String cloudFileName;
    private boolean forAll = true;

    public WordDbEntity(String word, String creator, String tag, String cloudFileName){
        this.word = word.toLowerCase();
        this.creator = creator;
        this.tag = tag.toLowerCase();
        this.cloudFileName = cloudFileName;
    }

}
