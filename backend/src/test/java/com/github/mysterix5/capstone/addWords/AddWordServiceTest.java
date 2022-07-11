package com.github.mysterix5.capstone.addWords;

import com.github.mysterix5.capstone.model.WordDbEntity;
import com.github.mysterix5.capstone.model.WordInput;
import com.github.mysterix5.capstone.model.WordTag;
import com.github.mysterix5.capstone.textHandling.WordsMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;


class AddWordServiceTest {

    // TODO only single and valid words allowed
    @Test
    void addWordToDb() {

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        AddWordService addWordService = new AddWordService(mockedWordRepo);

        WordInput wordInput = WordInput.builder().word("testword").tags(List.of(WordTag.ACCENT)).build();
        addWordService.addWordToDb(wordInput);

        Mockito.verify(mockedWordRepo).save(new WordDbEntity(wordInput));
    }
}