package com.github.mysterix5.vover.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class VoverErrorDTO {
    private String message;
    private List<String> subMessages = new ArrayList<>();

    public VoverErrorDTO(@NotNull MultipleSubErrorException e){
        message = e.getMessage();
        subMessages = e.getSubMessages();
    }

    public VoverErrorDTO(@NotNull Exception e){
        message = e.getMessage();
    }

    public VoverErrorDTO(String message, String ...subMessages){
        this.message = message;
        Collections.addAll(this.subMessages, subMessages);
    }
}
