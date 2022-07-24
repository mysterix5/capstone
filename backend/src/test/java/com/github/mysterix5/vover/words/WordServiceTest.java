package com.github.mysterix5.vover.words;

import com.github.mysterix5.vover.cloudStorage.CloudRepository;
import com.github.mysterix5.vover.model.other.Accessibility;
import com.github.mysterix5.vover.model.word.RecordManagementDTO;
import com.github.mysterix5.vover.model.word.RecordPage;
import com.github.mysterix5.vover.model.word.RecordPageSubmitDTO;
import com.github.mysterix5.vover.model.word.WordDbEntity;
import com.github.mysterix5.vover.main.WordsMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.notNull;

class WordServiceTest {


    // TODO only single and valid words allowed
    @Test
    void addWordToDb() throws IOException {
        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        CloudRepository mockedCloudRepo = Mockito.mock(CloudRepository.class);
        WordService wordService = new WordService(mockedWordRepo, mockedCloudRepo);

        UUID myUuid = new UUID(10, 10);

        String word = "word";
        String creator = "creator";
        String tag = "tag";
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, word + "-" + creator + "-" + tag + "-" + myUuid + ".mp3");

        Mockito.when(mockedWordRepo.save(notNull())).thenReturn(wordDbEntity);
        try (MockedStatic<UUID> mb = Mockito.mockStatic(UUID.class)) {
            mb.when(UUID::randomUUID).thenReturn(myUuid);


            wordService.addWordToDb(word, creator, tag, "PUBLIC", null);

            try {
                Mockito.verify(mockedCloudRepo).save(wordDbEntity.getCloudFileName(), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    void getRecordPage() {
        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        CloudRepository mockedCloudRepo = Mockito.mock(CloudRepository.class);
        WordService wordService = new WordService(mockedWordRepo, mockedCloudRepo);

        int page = 0;
        int size = 2;
        String searchTerm = "";
        RecordPageSubmitDTO recordPageSubmitDTO = new RecordPageSubmitDTO(page, size, searchTerm);
        Pageable paging = PageRequest.of(page, size);

        String username = "1";

        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        WordDbEntity wordDbEntity2 = WordDbEntity.builder().id("id2").word("eins").creator("creator2").tag("tag2").cloudFileName("eins.mp3").accessibility(Accessibility.PUBLIC).build();

        Page<WordDbEntity> resultPage = new PageImpl<>(List.of(wordDbEntity1, wordDbEntity2), paging, 2);

        Mockito.when(mockedWordRepo.findAllByCreator(username, paging)).thenReturn(resultPage);

        RecordPage recordPage = wordService.getRecordPage(username, page, size, searchTerm);

        RecordPage expected = RecordPage.builder()
                .page(page)
                .size(size)
                .noPages(1)
                .searchTerm(searchTerm)
                .records(List.of(new RecordManagementDTO(wordDbEntity1), new RecordManagementDTO(wordDbEntity2))).build();

        assertThat(recordPage).isEqualTo(expected);
    }
}