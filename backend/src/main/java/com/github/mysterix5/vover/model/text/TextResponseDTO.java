package com.github.mysterix5.vover.model.text;

import com.github.mysterix5.vover.model.word.WordDbResponseDTO;
import com.github.mysterix5.vover.model.word.WordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextResponseDTO {
    private List<WordResponseDTO> textWords;
    private Map<String, List<WordDbResponseDTO>> wordMap;
}
