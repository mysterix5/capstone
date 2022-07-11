package com.github.mysterix5.capstone.addWords;

import com.github.mysterix5.capstone.model.WordInput;
import com.github.mysterix5.capstone.textHandling.WordsMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addword")
public class AddWordController {
    private final AddWordService wordService;

    @PostMapping
    public void addWord(@RequestBody WordInput wordInput){
        wordService.addWordToDb(wordInput);
    }
}
