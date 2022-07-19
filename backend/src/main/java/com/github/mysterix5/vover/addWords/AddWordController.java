package com.github.mysterix5.vover.addWords;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addword")
public class AddWordController {
    private final AddWordService wordService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addWord(@RequestParam("word") String word,
                                  @RequestParam("creator") String creator,
                                  @RequestParam("tag") String tag,
                                  @RequestParam("audio") MultipartFile audio
    ) throws IOException {
        var audioBytes = audio.getBytes();
        wordService.addWordToDb(word.toLowerCase(), creator.toLowerCase(), tag.toLowerCase(), audioBytes);

        return ResponseEntity.ok().build();
    }
}
