package com.github.mysterix5.vover.model.other;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class VoverErrorDTO {
    private String message;
    private List<String> subMessages = new ArrayList<>();

    public VoverErrorDTO(MultipleSubErrorException e){
        message = e.getMessage();
        subMessages = e.getSubMessages();
    }

    public VoverErrorDTO(Exception e){
        message = e.getMessage();
    }

    public VoverErrorDTO(String message, String ...subMessages){
        this.message = message;
        Collections.addAll(this.subMessages, subMessages);
    }
}
