package com.github.mysterix5.vover.words;

import com.github.mysterix5.vover.model.other.VoverErrorDTO;
import com.github.mysterix5.vover.model.word.RecordManagementDTO;
import com.github.mysterix5.vover.model.word.RecordPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/word")
public class WordController {
    private final WordService wordService;

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

    @GetMapping("/audio/{id}")
    public ResponseEntity<Object> getSingleAudio(@PathVariable String id, HttpServletResponse httpResponse, Principal principal){
        try {
            AudioInputStream audio = wordService.getAudio(id, principal.getName());
            httpResponse.setContentType("audio/mp3");
            httpResponse.getOutputStream().write(audio.readAllBytes());
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new VoverErrorDTO(e));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> getSingleAudio(@PathVariable String id, Principal principal){
        try {
            wordService.deleteRecord(id, principal.getName());
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new VoverErrorDTO(e));
        }
    }

    @PutMapping
    public ResponseEntity<Object> changeRecordMetadata(@RequestBody RecordManagementDTO recordManagementDTO, Principal principal){
        try {
            wordService.changeRecordMetadata(recordManagementDTO, principal.getName());
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new VoverErrorDTO(e));
        }
    }

    @GetMapping("/{page}/{size}")
    public ResponseEntity<Object> getRecordPage(@PathVariable int page, @PathVariable int size, @RequestParam String searchTerm, Principal principal){
        try{
            RecordPage RecordPage = wordService.getRecordPage(principal.getName(), page, size, searchTerm);
            return ResponseEntity.ok().body(RecordPage);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("Something went wrong fetching your records"));
        }
    }

}