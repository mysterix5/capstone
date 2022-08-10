package com.github.mysterix5.vover.primary;

import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimarySubmitDTO;
import com.github.mysterix5.vover.model.primary.WordAvailability;
import com.github.mysterix5.vover.model.record.*;
import com.github.mysterix5.vover.model.record.RecordAvailability;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import com.github.mysterix5.vover.records.RecordMongoRepository;
import com.github.mysterix5.vover.user_details.VoverUserDetailsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PrimaryServiceTest {

    @Test
    void testOnSubmittedTextSimpleValid() {
        String testString = "bester test";

        RecordMongoRepository mockedRecordRepo = Mockito.mock(RecordMongoRepository.class);

        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("bester").creator("creator1").tag("tag1").cloudFileName("bester.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordDbEntity recordDbEntity2 = RecordDbEntity.builder().id("id2").word("test").creator("creator2").tag("tag2").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        when(mockedRecordRepo.findByWordIn(new HashSet<>(List.of("bester", "test"))))
                .thenReturn(List.of(
                        recordDbEntity1,
                        recordDbEntity2
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        VoverUserDetailsService mockedVoverUserDetailsService = Mockito.mock(VoverUserDetailsService.class);
        PrimaryService primaryService = new PrimaryService(mockedRecordRepo, mockedCloudService, mockedVoverUserDetailsService);

        VoverUserDetails userDetails = new VoverUserDetails();
        userDetails.setUsername("user");
        Mockito.when(mockedVoverUserDetailsService.getUserDetails("user")).thenReturn(userDetails);

        PrimaryResponseDTO response = primaryService.onSubmittedText(PrimarySubmitDTO.builder().text(testString).scope(new ArrayList<>()).build(), "user");

        PrimaryResponseDTO expected = new PrimaryResponseDTO(
                List.of(new WordResponseDTO("bester", WordAvailability.AVAILABLE), new WordResponseDTO("test", WordAvailability.AVAILABLE)),
                Map.of("bester", List.of(new RecordDbResponseDTO(recordDbEntity1, RecordAvailability.PUBLIC)),
                        "test", List.of(new RecordDbResponseDTO(recordDbEntity2, RecordAvailability.PUBLIC))),
                List.of("id1","id2"));

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void testOnSubmittedTextWithInvalidAndAbsent() {
        String testString = "beste/r test ever% wirklich";

        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordMongoRepository mockedRecordRepo = Mockito.mock(RecordMongoRepository.class);
        when(mockedRecordRepo.findByWordIn(new HashSet<>(List.of("test", "wirklich"))))
                .thenReturn(List.of(
                        recordDbEntity1
                ));
        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        VoverUserDetailsService mockedVoverUserDetailsService = Mockito.mock(VoverUserDetailsService.class);
        PrimaryService primaryService = new PrimaryService(mockedRecordRepo, mockedCloudService, mockedVoverUserDetailsService);

        VoverUserDetails userDetails = new VoverUserDetails();
        userDetails.setUsername("user");
        Mockito.when(mockedVoverUserDetailsService.getUserDetails("user")).thenReturn(userDetails);

        var response = primaryService.onSubmittedText(PrimarySubmitDTO.builder().text(testString).scope(new ArrayList<>()).build(), "user");

        List<String> defaultIds = new ArrayList<>();
        defaultIds.add(null);
        defaultIds.add("id1");
        defaultIds.add(null);
        defaultIds.add(null);

        var expected = new PrimaryResponseDTO(
                List.of(
                        new WordResponseDTO("beste/r", WordAvailability.INVALID),
                        new WordResponseDTO("test", WordAvailability.AVAILABLE),
                        new WordResponseDTO("ever%", WordAvailability.INVALID),
                        new WordResponseDTO("wirklich", WordAvailability.NOT_AVAILABLE)
                ),
                Map.of(
                        "test", List.of(new RecordDbResponseDTO(recordDbEntity1, RecordAvailability.PUBLIC))
                ),
                defaultIds);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void getMergedAudioFailBecauseOneRecordNotAllowedForUser() {
        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("test").creator("creator1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordDbEntity recordDbEntity2 = RecordDbEntity.builder().id("id2").word("eins").creator("creator2").tag("tag2").cloudFileName("eins.mp3").accessibility(Accessibility.PRIVATE).build();

        RecordMongoRepository mockedRecordRepo = Mockito.mock(RecordMongoRepository.class);
        when(mockedRecordRepo.findAllById(List.of("id1", "id2"))).thenReturn(List.of(recordDbEntity1, recordDbEntity2));

        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        VoverUserDetailsService mockedVoverUserDetailsService = Mockito.mock(VoverUserDetailsService.class);
        VoverUserDetails userDetails = new VoverUserDetails();
        userDetails.setUsername("creator1");
        Mockito.when(mockedVoverUserDetailsService.getUserDetails("creator1")).thenReturn(userDetails);
        PrimaryService primaryService = new PrimaryService(mockedRecordRepo, mockedCloudService, mockedVoverUserDetailsService);

        Assertions.assertThatExceptionOfType(MultipleSubErrorException.class)
                .isThrownBy(() -> primaryService.getMergedAudio(List.of("id1", "id2"), "creator1"))
                .withMessage("You are not allowed to get one of the records! Don't try to hack me! :(");
    }
    @Test
    void getMergedAudio() {
        RecordDbEntity recordDbEntity1 = RecordDbEntity.builder().id("id1").word("eins").creator("creator1").tag("tag1").cloudFileName("eins.mp3").accessibility(Accessibility.PUBLIC).build();
        RecordDbEntity recordDbEntity2 = RecordDbEntity.builder().id("id2").word("zwei").creator("creator2").tag("tag2").cloudFileName("zwei.mp3").accessibility(Accessibility.PUBLIC).build();

        RecordMongoRepository mockedRecordRepo = Mockito.mock(RecordMongoRepository.class);
        when(mockedRecordRepo.findAllById(List.of("id1", "id2"))).thenReturn(List.of(recordDbEntity1, recordDbEntity2));

        CloudService mockedCloudService = Mockito.mock(CloudService.class);
        VoverUserDetailsService mockedVoverUserDetailsService = Mockito.mock(VoverUserDetailsService.class);
        PrimaryService primaryService = new PrimaryService(mockedRecordRepo, mockedCloudService, mockedVoverUserDetailsService);

        File einsFile = new File("src/test/resources/cloud_storage/eins.mp3");
        File zweiFile = new File("src/test/resources/cloud_storage/zwei.mp3");
        File einsZweiFile = new File("src/test/resources/cloud_storage/einsZwei.mp3");

        try (FileInputStream einsStream = new FileInputStream(einsFile);
             FileInputStream zweiStream = new FileInputStream(zweiFile);
             FileInputStream einsZweiStream = new FileInputStream(einsZweiFile)
        ) {
            ByteArrayInputStream einsByteStream = new ByteArrayInputStream(einsStream.readAllBytes());
            ByteArrayInputStream zweiByteStream = new ByteArrayInputStream(zweiStream.readAllBytes());

            when(mockedCloudService.loadMultipleMp3FromCloud(List.of("eins.mp3", "zwei.mp3"))).thenReturn(List.of(einsByteStream, zweiByteStream));

            byte[] mergedAudio = primaryService.getMergedAudio(List.of("id1", "id2"), "creator1");

            Mockito.verify(mockedVoverUserDetailsService).addRequestToHistory("creator1", List.of(recordDbEntity1, recordDbEntity2));
            assertThat(mergedAudio.length).isEqualTo(einsZweiStream.readAllBytes().length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}