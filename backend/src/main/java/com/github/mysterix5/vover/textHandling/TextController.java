package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.model.TextResponseDTO;
import com.github.mysterix5.vover.model.TextSubmitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/main")
public class TextController {

    private final TextService textService;

    @PostMapping
    public ResponseEntity<TextResponseDTO> onSubmittedText(@RequestBody TextSubmitDTO textSubmitDTO, Principal principal){
        log.info("Text submitted by user '{}': {}", principal.getName(), textSubmitDTO.getText());
        return ResponseEntity.ok().body(textService.onSubmittedText(textSubmitDTO.getText(), principal.getName()));
    }

    @PostMapping("/audio")
    public void loadListFromCloudAndMerge(HttpServletResponse httpResponse, @RequestBody List<String> ids) throws IOException {

        AudioInputStream mergedAudio = textService.getMergedAudio(ids);

        httpResponse.setContentType("audio/mp3");
        httpResponse.getOutputStream().write(mergedAudio.readAllBytes());
    }


}
