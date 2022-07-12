package com.github.mysterix5.capstone.addWords;

import com.github.mysterix5.capstone.model.WordDbEntity;
import com.github.mysterix5.capstone.model.WordInput;
import com.github.mysterix5.capstone.textHandling.WordsMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddWordService {
    private final WordsMongoRepository wordRepository;

    public void addWordToDb(WordInput wordInput) {
        wordRepository.save(new WordDbEntity(wordInput));
    }
}
