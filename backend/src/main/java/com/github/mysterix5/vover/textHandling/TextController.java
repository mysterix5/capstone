package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.model.WordResponseDTO;
import com.github.mysterix5.vover.model.TextSubmitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/main")
public class TextController {

    private final TextService textService;

    @PutMapping
    public ResponseEntity<List<WordResponseDTO>> onSubmittedText(@RequestBody TextSubmitDTO textSubmitDTO){
        log.info("Text in submit text: " + textSubmitDTO.getText());
        return ResponseEntity.ok().body(textService.onSubmittedText(textSubmitDTO.getText()));
    }

    @PostMapping("/audio")
    public void loadListFromCloudAndMerge(HttpServletResponse httpResponse, @RequestBody List<WordResponseDTO> words) throws IOException {

        AudioInputStream mergedAudio = textService.getMergedAudio(words);

        httpResponse.setContentType("audio/mp3");
        httpResponse.getOutputStream().write(mergedAudio.readAllBytes());
    }


}
