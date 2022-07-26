package com.github.mysterix5.vover.main;

import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.text.TextSubmitDTO;
import com.github.mysterix5.vover.model.other.VoverErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/main")
public class MainController {

    private final MainService mainService;

    @PostMapping
    public ResponseEntity<Object> onSubmittedText(@RequestBody TextSubmitDTO textSubmitDTO, Principal principal) {
        log.info("Text submitted by user '{}': {}", principal.getName(), textSubmitDTO.getText());
        try {
            return ResponseEntity.ok().body(mainService.onSubmittedText(textSubmitDTO.getText(), principal.getName()));
        } catch (MultipleSubErrorException e) {
            return ResponseEntity.internalServerError().body(new VoverErrorDTO(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("Unknown error while handling your request :("));
        }
    }

    @PostMapping("/audio")
    public ResponseEntity<Object> loadListFromCloudAndMerge(HttpServletResponse httpResponse, @RequestBody List<String> ids) {
        AudioInputStream mergedAudio;
        try {
            mergedAudio = mainService.getMergedAudio(ids);
            httpResponse.setContentType("audio/mp3");
            httpResponse.getOutputStream().write(mergedAudio.readAllBytes());
            return ResponseEntity.ok().build();
        } catch (MultipleSubErrorException e) {
            return ResponseEntity.badRequest().body(new VoverErrorDTO(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoverErrorDTO("Unknown error while handling your request :("));
        }
    }


}
