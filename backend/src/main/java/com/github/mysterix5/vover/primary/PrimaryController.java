package com.github.mysterix5.vover.primary;

import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimarySubmitDTO;
import com.github.mysterix5.vover.model.other.VoverErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/primary")
public class PrimaryController {

    private final PrimaryService primaryService;

    @PostMapping("/textsubmit")
    public ResponseEntity<Object> onSubmittedText(@RequestBody PrimarySubmitDTO primarySubmitDTO, Principal principal) {
        log.info("Text submitted by user '{}': {}", principal.getName(), primarySubmitDTO.getText());
        try {
            return ResponseEntity.ok().body(primaryService.onSubmittedText(primarySubmitDTO, principal.getName()));
        } catch (MultipleSubErrorException e) {
            return ResponseEntity.badRequest().body(new VoverErrorDTO(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("Unknown error while handling your request :("));
        }
    }

    @PostMapping("/getaudio")
    public ResponseEntity<Object> loadListFromCloudAndMerge(HttpServletResponse httpResponse, @RequestBody List<String> ids, Principal principal) {
        log.info("user '{}' requests an audio with '{}' words. ids: {}", principal.getName(), ids.size(), ids);
        try {
            byte[] mergedAudio = primaryService.getMergedAudio(ids, principal.getName());
            httpResponse.setContentType("audio/mp3");
            httpResponse.getOutputStream().write(mergedAudio);
            return ResponseEntity.ok().build();
        } catch (MultipleSubErrorException e) {
            return ResponseEntity.badRequest().body(new VoverErrorDTO(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoverErrorDTO("Unknown error while handling your request :("));
        }
    }
}
