package com.github.mysterix5.vover.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "records")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDbEntity {
    @Id
    private String id;
    private String word;
    private String creator;
    private String tag;
    private Accessibility accessibility = Accessibility.PUBLIC;
    private String cloudFileName;

    public RecordDbEntity(String word, String creator, String tag, String cloudFileName){
        this.word = word.toLowerCase();
        this.creator = creator;
        this.tag = tag.toLowerCase();
        this.cloudFileName = cloudFileName;
    }

    public RecordDbEntity(String word, String creator, String tag, String accessibility, String cloudFileName){
        this.word = word.toLowerCase();
        this.creator = creator;
        this.tag = tag.toLowerCase();
        this.cloudFileName = cloudFileName;

        this.accessibility = Accessibility.valueOf(accessibility);
    }

}
