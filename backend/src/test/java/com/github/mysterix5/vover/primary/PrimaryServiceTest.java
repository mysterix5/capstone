package com.github.mysterix5.vover.primary;

import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.record.Accessibility;
import com.github.mysterix5.vover.model.record.Availability;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import com.github.mysterix5.vover.model.record.RecordDbResponseDTO;
import com.github.mysterix5.vover.model.record.RecordResponseDTO;
import com.github.mysterix5.vover.records.RecordMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO tests on primary valid checks etc. should be added here regularly as functionality grows
class PrimaryServiceTest {

    @Test
    void testOnSubmittedTextSimpleValid() {
        String testString = "bester test";

        RecordMongoRepository mockedWordRepo = Mockito.mock(RecordMongoRepository.class);

        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("bester").creator("creator1").tag("tag1").cloudFileName("bester.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordDbEntity recordDbEntity2 = RecordDbEntity.builder().id("id2").word("test").creator("creator2").tag("tag2").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        when(mockedWordRepo.findByWordIn(new HashSet<>(List.of("bester", "test"))))
                .thenReturn(List.of(
                        recordDbEntity1,
                        recordDbEntity2
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        PrimaryService primaryService = new PrimaryService(mockedWordRepo, mockedCloudService);

        PrimaryResponseDTO response = primaryService.onSubmittedText(testString, "user");

        PrimaryResponseDTO expected = new PrimaryResponseDTO(
                List.of(new RecordResponseDTO("bester", Availability.PUBLIC), new RecordResponseDTO("test", Availability.PUBLIC)),
                Map.of("bester", List.of(new RecordDbResponseDTO(recordDbEntity1)),
                        "test", List.of(new RecordDbResponseDTO(recordDbEntity2))),
                List.of("id1","id2"));

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void testOnSubmittedTextWithInvalidAndAbsent() {
        String testString = "beste/r test ever% wirklich";

        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordMongoRepository mockedWordRepo = Mockito.mock(RecordMongoRepository.class);
        when(mockedWordRepo.findByWordIn(new HashSet<>(List.of("test", "wirklich"))))
                .thenReturn(List.of(
                        recordDbEntity1
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        PrimaryService primaryService = new PrimaryService(mockedWordRepo, mockedCloudService);

        var response = primaryService.onSubmittedText(testString, "user");

        List<String> defaultIds = new ArrayList<>();
        defaultIds.add(null);
        defaultIds.add("id1");
        defaultIds.add(null);
        defaultIds.add(null);

        var expected = new PrimaryResponseDTO(
                List.of(
                        new RecordResponseDTO("beste/r", Availability.INVALID),
                        new RecordResponseDTO("test", Availability.PUBLIC),
                        new RecordResponseDTO("ever%", Availability.INVALID),
                        new RecordResponseDTO("wirklich", Availability.NOT_AVAILABLE)
                ),
                Map.of(
                        "test", List.of(new RecordDbResponseDTO(recordDbEntity1))
                ),
                defaultIds);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void loadWavFromCloudAndMerge() throws IOException {
        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordDbEntity recordDbEntity2 = RecordDbEntity.builder().id("id2").word("eins").creator("creator2").tag("tag2").cloudFileName("eins.mp3").accessibility(Accessibility.PUBLIC).build();

        RecordMongoRepository mockedWordRepo = Mockito.mock(RecordMongoRepository.class);
        when(mockedWordRepo.findAllById(List.of("id1", "id2"))).thenReturn(List.of(recordDbEntity1, recordDbEntity2));

        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        PrimaryService primaryService = new PrimaryService(mockedWordRepo, mockedCloudService);

        primaryService.getMergedAudio(List.of("id1", "id2"));

        verify(mockedCloudService).loadMultipleAudioFromCloudAndMerge(List.of("test.mp3", "eins.mp3"));
    }
}