package com.github.mysterix5.vover.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioResponseDTO {
    private byte[] data;
    private String contentType;
    private int contentLength;
}
