package com.github.mysterix5.vover.addWords;

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
    public ResponseEntity<Void> addWord(@RequestParam("word") String word,
                                        @RequestParam("tag") String tag,
                                        @RequestParam("audio") MultipartFile audio,
                                        Principal principal
    ) throws IOException {
        var audioBytes = audio.getBytes();
        wordService.addWordToDb(word.toLowerCase(), principal.getName(), tag.toLowerCase(), audioBytes);

        return ResponseEntity.ok().build();
    }
}
