package com.github.mysterix5.vover.cloudStorage;

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

        var obFile = new File("src/test/resources/cloudStorage/ob.mp3");
        var dasFile = new File("src/test/resources/cloudStorage/das.mp3");
        var obDasFile = new File("src/test/resources/cloudStorage/obDas.mp3");

        try (var obStream = new FileInputStream(obFile);
             var dasStream = new FileInputStream(dasFile);
             var obDasStream = new FileInputStream(obDasFile)
        ) {
            Mockito.when(cloudRepository.find("ob.mp3")).thenReturn(obStream.readAllBytes());
            Mockito.when(cloudRepository.find("das.mp3")).thenReturn(dasStream.readAllBytes());

            var obDasBytes = obDasStream.readAllBytes();

            AudioInputStream mergedAudio = cloudService.loadMultipleAudioFromCloudAndMerge(List.of("ob.mp3", "das.mp3"));

            assertThat(mergedAudio.readAllBytes()).isEqualTo(obDasBytes);
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    @Test
    void save() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/cloudStorage/ob.mp3");

        try (var obStream = new FileInputStream(obFile)) {
            byte[] bytes = obStream.readAllBytes();
            cloudService.save("ob.mp3", bytes);

            Mockito.verify(cloudRepository).save("ob.mp3", bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void find() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/cloudStorage/ob.mp3");

        try (var obStream = new FileInputStream(obFile)) {
            byte[] bytes = obStream.readAllBytes();
            Mockito.when(cloudRepository.find("ob.mp3")).thenReturn(bytes);

            AudioInputStream actual = cloudService.find("ob.mp3");

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
            cloudService.delete("ob.mp3");

            Mockito.verify(cloudRepository).delete("ob.mp3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void move() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        try{
            cloudService.move("ob.mp3", "das.mp3");

            Mockito.verify(cloudRepository).move("ob.mp3", "das.mp3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}