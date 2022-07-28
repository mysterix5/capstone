package com.github.mysterix5.vover.model.other;

import java.util.List;

public class MultipleSubErrorException extends RuntimeException {
    private List<String> subMessages;

    public  MultipleSubErrorException(String message){
        super(message);
    }

    public MultipleSubErrorException(String message, Throwable cause){
        super(message,cause);
    }

    public MultipleSubErrorException(String message, List<String> subMessages, Throwable cause){
        super(message,cause);
        this.subMessages = subMessages;
    }
    public MultipleSubErrorException(String message, List<String> subMessages){
        super(message);
        this.subMessages = subMessages;
    }
    public List<String> getSubMessages() {
        return this.subMessages;
    }
}