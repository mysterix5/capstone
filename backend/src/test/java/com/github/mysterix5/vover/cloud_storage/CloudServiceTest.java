package com.github.mysterix5.vover.cloud_storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CloudServiceTest {

    @Test
    void loadMultipleAudioFromCloudAndMerge() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/cloud_storage/eins.mp3");
        var dasFile = new File("src/test/resources/cloud_storage/zwei.mp3");
        var obDasFile = new File("src/test/resources/cloud_storage/einszwei.mp3");

        try (var obStream = new FileInputStream(obFile);
             var dasStream = new FileInputStream(dasFile);
             var obDasStream = new FileInputStream(obDasFile)
        ) {
            Mockito.when(cloudRepository.find("eins.mp3")).thenReturn(obStream.readAllBytes());
            Mockito.when(cloudRepository.find("zwei.mp3")).thenReturn(dasStream.readAllBytes());

            var obDasBytes = obDasStream.readAllBytes();

            byte[] mergedAudio = cloudService.loadMultipleMp3FromCloudAndMerge(List.of("eins.mp3", "zwei.mp3"));

            assertThat(mergedAudio).isEqualTo(obDasBytes);
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    @Test
    void save() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/cloud_storage/eins.mp3");

        try (var obStream = new FileInputStream(obFile)) {
            byte[] bytes = obStream.readAllBytes();
            cloudService.save("eins.mp3", bytes);

            Mockito.verify(cloudRepository).save("eins.mp3", bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void find() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/cloud_storage/eins.mp3");

        try (var obStream = new FileInputStream(obFile)) {
            byte[] bytes = obStream.readAllBytes();
            Mockito.when(cloudRepository.find("eins.mp3")).thenReturn(bytes);

            AudioInputStream actual = cloudService.find("eins.mp3");

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            AudioFileFormat baseFormat = AudioSystem.getAudioFileFormat(byteArrayInputStream);
            AudioInputStream expected = new AudioInputStream(byteArrayInputStream, baseFormat.getFormat(), baseFormat.getFrameLength());

            assertThat(actual.getFormat().getEncoding()).isEqualTo(expected.getFormat().getEncoding());
            assertThat(actual.getFormat().getSampleRate()).isEqualTo(expected.getFormat().getSampleRate());
            assertThat(actual.getFrameLength()).isEqualTo(expected.getFrameLength());
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void delete() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        try{
            cloudService.delete("eins.mp3");

            Mockito.verify(cloudRepository).delete("eins.mp3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void move() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        try{
            cloudService.move("eins.mp3", "zwei.mp3");

            Mockito.verify(cloudRepository).move("eins.mp3", "zwei.mp3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}