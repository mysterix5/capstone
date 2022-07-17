package com.github.mysterix5.vover.cloudstorage;

import com.github.sardine.Sardine;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CloudRepositoryTest {

    @Test
    void find() throws IOException {
        Sardine mockedSardine = Mockito.mock(Sardine.class);
                String url = "https://cloud.com/";
        CloudRepository cloudRepository = new CloudRepository(mockedSardine, url);

        var obFile = new File("src/test/resources/com.github.mysterix5.vover.cloudstorage/ob.wav");
        var obStream = new FileInputStream(obFile);

        Mockito.when(mockedSardine.get(url + "ob.wav")).thenReturn(obStream);
        byte[] actual = cloudRepository.find("ob.wav");

        obStream = new FileInputStream(obFile);
        var expected = obStream.readAllBytes();

        Mockito.verify(mockedSardine).get(url + "ob.wav");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void save() {
    }
}