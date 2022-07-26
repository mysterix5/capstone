package com.github.mysterix5.vover.addWords;

import com.github.mysterix5.vover.model.VoverErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addword")
public class AddWordController {
    private final AddWordService wordService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addWord(@RequestParam("word") String word,
                                        @RequestParam("tag") String tag,
                                        @RequestParam("accessibility") String accessibility,
                                        @RequestParam("audio") MultipartFile audio,
                                        Principal principal
    ) {
        try {
            var audioBytes = audio.getBytes();
            wordService.addWordToDb(word.toLowerCase(), principal.getName(), tag.toLowerCase(), accessibility, audioBytes);
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("Something went wrong while saving your recording"));
        }

        return ResponseEntity.ok().build();
    }
}
