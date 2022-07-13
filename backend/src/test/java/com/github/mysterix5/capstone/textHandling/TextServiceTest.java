package com.github.mysterix5.capstone.textHandling;

import com.github.mysterix5.capstone.cloudstorage.CloudService;
import com.github.mysterix5.capstone.model.Availability;
import com.github.mysterix5.capstone.model.WordResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// TODO tests on text valid checks etc should be added here regularly as functionality grows
class TextServiceTest {

    @Test
    void testOnSubmittedTextSimpleValid() {
        String testString = "bester test";

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        when(mockedWordRepo.existsByWord("bester")).thenReturn(true);
        when(mockedWordRepo.existsByWord("test")).thenReturn(true);
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        var response = textService.onSubmittedText(testString);

        var expected = List.of(new WordResponseDTO("bester", Availability.PUBLIC), new WordResponseDTO("test", Availability.PUBLIC));

        assertThat(response).containsExactlyElementsOf(expected);
    }

    @Test
    void testOnSubmittedTextWithInvalidAndAbsent() {
        String testString = "beste/r test ever% wirklich";

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        when(mockedWordRepo.existsByWord("test")).thenReturn(true);
        when(mockedWordRepo.existsByWord("wirklich")).thenReturn(false);
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        var response = textService.onSubmittedText(testString);

        var expected = List.of(new WordResponseDTO("beste/r", Availability.INVALID), new WordResponseDTO("test", Availability.PUBLIC), new WordResponseDTO("ever%", Availability.INVALID), new WordResponseDTO("wirklich", Availability.ABSENT));

        assertThat(response).containsExactlyElementsOf(expected);
    }

}