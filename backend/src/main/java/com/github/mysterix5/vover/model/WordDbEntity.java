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
    private String tag;
    private String cloudFileName;
    private String creator;

    public WordDbEntity(String word, String creator, String tag, String cloudFileName){
        this.word = word.toLowerCase();
        this.tag = tag.toLowerCase();
        this.creator = creator;
        this.cloudFileName = cloudFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordDbEntity that = (WordDbEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(word, that.word) && Objects.equals(tag, that.tag) && Objects.equals(cloudFileName, that.cloudFileName) && Objects.equals(creator, that.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, tag, cloudFileName, creator);
    }

}
