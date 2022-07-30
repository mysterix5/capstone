package com.github.mysterix5.vover.cloud_storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CloudServiceTest {

    @Test
    void loadMultipleMp3FromCloud() {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        File einsFile = new File("src/test/resources/cloud_storage/eins.mp3");
        File zweiFile = new File("src/test/resources/cloud_storage/zwei.mp3");

        try (FileInputStream einsStream = new FileInputStream(einsFile);
             FileInputStream zweiStream = new FileInputStream(zweiFile)
        ) {
            byte[] einsBytes = einsStream.readAllBytes();
            byte[] zweiBytes = zweiStream.readAllBytes();

            Mockito.when(cloudRepository.find("eins.mp3")).thenReturn(einsBytes);
            Mockito.when(cloudRepository.find("zwei.mp3")).thenReturn(zweiBytes);

            List<InputStream> audioInputStreams = cloudService.loadMultipleMp3FromCloud(List.of("eins.mp3", "zwei.mp3"));

            assertThat(audioInputStreams.get(0).readAllBytes()).containsExactly(einsBytes);
            assertThat(audioInputStreams.get(1).readAllBytes()).containsExactly(zweiBytes);
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

            byte[] actual = cloudService.find("eins.mp3");

            assertThat(actual).isEqualTo(bytes);
        } catch (IOException e) {
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