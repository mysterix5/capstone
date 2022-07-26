package com.github.mysterix5.vover.addWords;

import com.github.mysterix5.vover.cloudstorage.CloudRepository;
import com.github.mysterix5.vover.model.WordDbEntity;
import com.github.mysterix5.vover.textHandling.WordsMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.notNull;

class AddWordServiceTest {


    // TODO only single and valid words allowed
    @Test
    void addWordToDb() throws IOException {
        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        CloudRepository mockedCloudRepo = Mockito.mock(CloudRepository.class);
        AddWordService addWordService = new AddWordService(mockedWordRepo, mockedCloudRepo);

        UUID myUuid = new UUID(10,10);

        String word = "word";
        String creator = "creator";
        String tag = "tag";
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, word + "-" + creator + "-" + tag + "-" + myUuid + ".mp3");

        Mockito.when(mockedWordRepo.save(notNull())).thenReturn(wordDbEntity);
        try (MockedStatic<UUID> mb = Mockito.mockStatic(UUID.class)) {
            mb.when(UUID::randomUUID).thenReturn(myUuid);


            addWordService.addWordToDb(word, creator, tag, "PUBLIC", null);

            try {
                Mockito.verify(mockedCloudRepo).save(wordDbEntity.getCloudFileName(), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}