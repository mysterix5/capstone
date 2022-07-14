package com.github.mysterix5.capstone.textHandling;

import com.github.mysterix5.capstone.cloudstorage.CloudService;
import com.github.mysterix5.capstone.model.Availability;
import com.github.mysterix5.capstone.model.WordDbEntity;
import com.github.mysterix5.capstone.model.WordInput;
import com.github.mysterix5.capstone.model.WordResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
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

    @Test
    void loadWavFromCloudAndMerge() throws UnsupportedAudioFileException, IOException {
        List<WordResponseDTO> wordResponseDTOList = List.of(
                WordResponseDTO.builder().word("test").availability(Availability.PUBLIC).build(),
                WordResponseDTO.builder().word("eins").availability(Availability.PUBLIC).build()
        );

        WordsMongoRepository mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        when(mockedWordRepo.findByWord("test")).thenReturn(Optional.of(new WordDbEntity(WordInput.builder().word("test").build())));
        when(mockedWordRepo.findByWord("eins")).thenReturn(Optional.of(new WordDbEntity(WordInput.builder().word("eins").build())));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        TextService textService = new TextService(mockedWordRepo, mockedCloudService);

        textService.loadWavFromCloudAndMerge(wordResponseDTOList);

        verify(mockedCloudService).loadListFromCloudAndMerge(List.of("test.wav", "eins.wav"));
    }
}