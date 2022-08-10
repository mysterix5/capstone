package com.github.mysterix5.vover.model.primary;

import com.github.mysterix5.vover.model.record.RecordDbResponseDTO;
import com.github.mysterix5.vover.model.record.WordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrimaryResponseDTO {
    private List<WordResponseDTO> textWords;
    private Map<String, List<RecordDbResponseDTO>> wordRecordMap;
    private List<String> defaultIds;
}
