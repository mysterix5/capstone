package com.github.mysterix5.vover.model;

import lombok.Data;

@Data
public class AudioResponseDTO {
    private byte[] data;
    private String contentType;
    private int contentLength;
}
