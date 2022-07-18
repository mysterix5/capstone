package com.github.mysterix5.vover.cloudstorage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CloudServiceTest {

    @Test
    void loadMultipleAudioFromCloudAndMerge() throws IOException {
        CloudRepository cloudRepository = Mockito.mock(CloudRepository.class);
        CloudService cloudService = new CloudService(cloudRepository);

        var obFile = new File("src/test/resources/com.github.mysterix5.vover.cloudstorage/ob.mp3");
        var obStream = new FileInputStream(obFile);
        var dasFile = new File("src/test/resources/com.github.mysterix5.vover.cloudstorage/das.mp3");
        var dasStream = new FileInputStream(dasFile);

        Mockito.when(cloudRepository.find("ob.mp3")).thenReturn(obStream.readAllBytes());
        Mockito.when(cloudRepository.find("das.mp3")).thenReturn(dasStream.readAllBytes());

        AudioInputStream mergedAudio = cloudService.loadMultipleAudioFromCloudAndMerge(List.of("ob.mp3", "das.mp3"));

        var obDasFile = new File("src/test/resources/com.github.mysterix5.vover.cloudstorage/obDas.mp3");
        var obDasStream = new FileInputStream(obDasFile);
        var obDasBytes = obDasStream.readAllBytes();

        assertThat(mergedAudio.readAllBytes()).isEqualTo(obDasBytes);
    }
}