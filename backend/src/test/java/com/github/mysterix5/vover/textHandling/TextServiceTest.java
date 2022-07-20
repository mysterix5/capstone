package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.cloudstorage.CloudService;
import com.github.mysterix5.vover.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO tests on text valid checks etc. should be added here regularly as functionality grows
class TextServiceTest {

    @Test
    void testOnSubmittedTextSimpleValid() {
        String testString = "bester test";

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);

        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("bester").creator("creator1").tag("tag1").cloudFileName("bester.mp3").build();
        WordDbEntity wordDbEntity2 = WordDbEntity.builder().id("id2").word("test").creator("creator2").tag("tag2").cloudFileName("test.mp3").build();
        when(mockedWordRepo.findByWordIn(new HashSet<>(List.of("bester", "test"))))
                .thenReturn(List.of(
                        wordDbEntity1,
                        wordDbEntity2
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        TextResponseDTO response = textService.onSubmittedText(testString);

        TextResponseDTO expected = new TextResponseDTO(
                List.of(new WordResponseDTO("bester", Availability.PUBLIC), new WordResponseDTO("test", Availability.PUBLIC)),
                Map.of("bester", List.of(new WordDbResponseDTO(wordDbEntity1)),
                        "test", List.of(new WordDbResponseDTO(wordDbEntity2))));

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void testOnSubmittedTextWithInvalidAndAbsent() {
        String testString = "beste/r test ever% wirklich";

        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").build();
        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        when(mockedWordRepo.findByWordIn(new HashSet<>(List.of("test", "wirklich"))))
                .thenReturn(List.of(
                        wordDbEntity1
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        var response = textService.onSubmittedText(testString);

        var expected = new TextResponseDTO(
                List.of(
                        new WordResponseDTO("beste/r", Availability.INVALID),
                        new WordResponseDTO("test", Availability.PUBLIC),
                        new WordResponseDTO("ever%", Availability.INVALID),
                        new WordResponseDTO("wirklich", Availability.ABSENT)
                ),
                Map.of(
                        "test", List.of(new WordDbResponseDTO(wordDbEntity1))
                ));

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void loadWavFromCloudAndMerge() throws IOException {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").build();
        WordDbEntity wordDbEntity2 = WordDbEntity.builder().id("id2").word("eins").creator("creator2").tag("tag2").cloudFileName("eins.mp3").build();

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        when(mockedWordRepo.findAllById(List.of("id1", "id2"))).thenReturn(List.of(wordDbEntity1, wordDbEntity2));

        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        textService.getMergedAudio(List.of("id1", "id2"));

        verify(mockedCloudService).loadMultipleAudioFromCloudAndMerge(List.of("test.mp3", "eins.mp3"));
    }
}