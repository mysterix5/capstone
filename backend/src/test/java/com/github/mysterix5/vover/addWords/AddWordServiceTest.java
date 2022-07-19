package com.github.mysterix5.vover.addWords;

import com.github.mysterix5.vover.cloudstorage.CloudRepository;
import com.github.mysterix5.vover.model.WordDbEntity;
import com.github.mysterix5.vover.textHandling.WordsMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.notNull;


class AddWordServiceTest {


    // TODO only single and valid words allowed
    @Test
    void addWordToDb() {
        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        CloudRepository mockedCloudRepo = Mockito.mock(CloudRepository.class);
        AddWordService addWordService = new AddWordService(mockedWordRepo, mockedCloudRepo);

        String word = "word";
        String creator = "creator";
        String tag = "tag";
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, word + "-" + creator + "-" + tag + "-" + "testid" + ".mp3");

        Mockito.when(mockedWordRepo.save(notNull())).thenReturn(wordDbEntity);

        addWordService.addWordToDb(word, creator, tag, null);

        try {
            Mockito.verify(mockedCloudRepo).save(wordDbEntity.getCloudFileName(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}