package com.github.mysterix5.capstone.textHandling;

import com.github.mysterix5.capstone.model.AudioResponseDTO;
import com.github.mysterix5.capstone.model.WordResponseDTO;
import com.github.mysterix5.capstone.model.TextSubmitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.UnsupportedAudioFileException;
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

    @PutMapping("/getaudio")
    public void loadListFromCloudAndMerge(HttpServletResponse httpResponse, @RequestBody List<WordResponseDTO> words) throws UnsupportedAudioFileException, IOException {

        AudioResponseDTO audioResponseDTO = textService.loadWavFromCloudAndMerge(words);

        setWavHttpResponse(httpResponse, audioResponseDTO);
    }

    public void setWavHttpResponse(HttpServletResponse httpResponse, AudioResponseDTO audioResponseDTO) throws IOException {
        httpResponse.setContentLength(audioResponseDTO.getContentLength());
        httpResponse.setContentType(audioResponseDTO.getContentType());
        httpResponse.getOutputStream().write(audioResponseDTO.getData());
    }

}
