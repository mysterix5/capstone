package com.github.mysterix5.vover.words;

import com.github.mysterix5.vover.cloudStorage.CloudRepository;
import com.github.mysterix5.vover.cloudStorage.CloudService;
import com.github.mysterix5.vover.model.other.Accessibility;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.word.RecordManagementDTO;
import com.github.mysterix5.vover.model.word.RecordPage;
import com.github.mysterix5.vover.model.word.WordDbEntity;
import com.github.mysterix5.vover.main.WordsMongoRepository;
import com.github.mysterix5.vover.usermanagement.UserMongoRepository;
import com.github.mysterix5.vover.usermanagement.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.passay.PasswordValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.notNull;

class WordServiceTest {

    WordsMongoRepository mockedWordRepo;
    CloudService mockedCloudService;
    WordService wordService;

    @BeforeEach
    void setupUserService() {
        mockedWordRepo = Mockito.mock(WordsMongoRepository.class);
        mockedCloudService = Mockito.mock(CloudService.class);
        wordService = new WordService(mockedWordRepo, mockedCloudService);
    }

    // TODO only single and valid words allowed
    @Test
    void addWordToDb() throws IOException {

        UUID myUuid = new UUID(10, 10);

        String word = "word";
        String creator = "creator";
        String tag = "tag";
        Accessibility accessibility = Accessibility.PUBLIC;
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, word + "-" + creator + "-" + tag + "-" + accessibility + "-" + myUuid + ".mp3");

        Mockito.when(mockedWordRepo.save(notNull())).thenReturn(wordDbEntity);
        try (MockedStatic<UUID> mb = Mockito.mockStatic(UUID.class)) {
            mb.when(UUID::randomUUID).thenReturn(myUuid);

            wordService.addWordToDb(word, creator, tag, accessibility.toString(), null);

            try {
                Mockito.verify(mockedCloudService).save(wordDbEntity.getCloudFileName(), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    void getRecordPage() {
        int page = 0;
        int size = 2;
        String searchTerm = "";
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
                .records(List.of(new RecordManagementDTO(wordDbEntity1), new RecordManagementDTO(wordDbEntity2)))
                .accessibilityChoices(Accessibility.values()).build();

        assertThat(recordPage).isEqualTo(expected);
    }

    @Test
    void getAudioSuccess() {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        var obFile = new File("src/test/resources/cloudStorage/ob.mp3");
        try (var obStream = new FileInputStream(obFile)) {
            byte[] bytes = obStream.readAllBytes();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            AudioFileFormat baseFormat = AudioSystem.getAudioFileFormat(byteArrayInputStream);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, baseFormat.getFormat(), baseFormat.getFrameLength());

            Mockito.when(mockedCloudService.find(wordDbEntity1.getCloudFileName())).thenReturn(audioInputStream);

            AudioInputStream actual = wordService.getAudio(wordDbEntity1.getId(), username);

            assertThat(actual).isEqualTo(audioInputStream);
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAudioFailBecausePrincipalDoesNotOwnThisRecord() {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user2";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.getAudio(wordDbEntity1.getId(), username))
                .withMessage("The audio file you requested is not yours. Don't try to hack me! :(");
    }

    @Test
    void getAudioFailBecauseCloudThrowsError() throws IOException {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        Mockito.when(mockedCloudService.find(wordDbEntity1.getCloudFileName())).thenThrow(new IOException());

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.getAudio(wordDbEntity1.getId(), username))
                .withMessage("Your audio could not be found in the cloud");
    }

    @Test
    void deleteRecordSuccess() throws IOException {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        wordService.deleteRecord(wordDbEntity1.getId(), username);
        Mockito.verify(mockedCloudService).delete(wordDbEntity1.getCloudFileName());
    }

    @Test
    void deleteRecordFailBecausePrincipalDoesNotOwnThisRecord() {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user2";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.deleteRecord(wordDbEntity1.getId(), username))
                .withMessage("The audio file you requested is not yours. Don't try to hack me! :(");
    }

    @Test
    void deleteRecordFailBecauseCloudThrowsError() throws IOException {
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(wordDbEntity1.getId())).thenReturn(Optional.of(wordDbEntity1));

        Mockito.doThrow(new IOException()).when(mockedCloudService).delete(wordDbEntity1.getCloudFileName());

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.deleteRecord(wordDbEntity1.getId(), username))
                .withMessage("Deleting your audio file failed, did nothing");
    }

    @Test
    void changeRecordMetadata() {
        String oldCloudFileName = "test.mp3";
        RecordManagementDTO recordManagementDTO = new RecordManagementDTO("id1", "test1", "tag1", Accessibility.PUBLIC);
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName(oldCloudFileName).accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(recordManagementDTO.getId())).thenReturn(Optional.of(wordDbEntity1));

        try (MockedStatic<UUID> mb = Mockito.mockStatic(UUID.class)) {
            UUID myUuid = new UUID(10, 10);
            mb.when(UUID::randomUUID).thenReturn(myUuid);
            String newCloudFileName = recordManagementDTO.getWord() + "-" +
                    username + "-" +
                    recordManagementDTO.getTag() + "-" +
                    recordManagementDTO.getAccessibility().toString() + "-" +
                    myUuid +
                    ".mp3";

            wordService.changeRecordMetadata(recordManagementDTO, username);

            Mockito.verify(mockedCloudService).move(oldCloudFileName, newCloudFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void changeRecordMetadataFailBecausePrincipalDoesNotOwnThisRecord() {
        RecordManagementDTO recordManagementDTO = new RecordManagementDTO("id1", "test1", "tag1", Accessibility.PUBLIC);
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user2";

        Mockito.when(mockedWordRepo.findById(recordManagementDTO.getId())).thenReturn(Optional.of(wordDbEntity1));

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.changeRecordMetadata(recordManagementDTO, username))
                .withMessage("The record you requested to change is not yours. Don't try to hack me! :(");
    }

    @Test
    void changeRecordMetadataFailBecauseNoChanges() {
        RecordManagementDTO recordManagementDTO = new RecordManagementDTO("id1", "test", "tag1", Accessibility.PUBLIC);
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(recordManagementDTO.getId())).thenReturn(Optional.of(wordDbEntity1));

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> wordService.changeRecordMetadata(recordManagementDTO, username))
                .withMessage("Nothing was changed");
    }

    @Test
    void changeRecordMetadataFailBecauseCloudThrowsIOError() {
        RecordManagementDTO recordManagementDTO = new RecordManagementDTO("id1", "test1", "tag1", Accessibility.PUBLIC);
        WordDbEntity wordDbEntity1 = WordDbEntity.builder().id("id1").word("test").creator("user1").tag("tag1").cloudFileName("test.mp3").accessibility(Accessibility.PUBLIC).build();
        String username = "user1";

        Mockito.when(mockedWordRepo.findById(recordManagementDTO.getId())).thenReturn(Optional.of(wordDbEntity1));

        try (MockedStatic<UUID> mb = Mockito.mockStatic(UUID.class)) {
            UUID myUuid = new UUID(10, 10);
            mb.when(UUID::randomUUID).thenReturn(myUuid);
            String newCloudFileName = recordManagementDTO.getWord() + "-" +
                    username + "-" +
                    recordManagementDTO.getTag() + "-" +
                    recordManagementDTO.getAccessibility().toString() + "-" +
                    myUuid +
                    ".mp3";

            Mockito.doThrow(new IOException()).when(mockedCloudService).move(wordDbEntity1.getCloudFileName(), newCloudFileName);

            Assertions.assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> wordService.changeRecordMetadata(recordManagementDTO, username))
                    .withMessage("Something went wrong changing your record metadata");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
