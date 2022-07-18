package com.github.mysterix5.vover.cloudstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {
    private final CloudRepository cloudRepository;

    public AudioInputStream loadMultipleAudioFromCloudAndMerge(List<String> cloudFilePaths) throws IOException {
        List<AudioInputStream> audioInputStreams = cloudFilePaths.parallelStream().map((path)->{
            try {
                return cloudRepository.find(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).map(bytes -> {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            AudioFileFormat baseFormat;
            try {
                baseFormat = AudioSystem.getAudioFileFormat(byteArrayInputStream);
            } catch (UnsupportedAudioFileException | IOException e) {
                throw new RuntimeException(e);
            }
            return new AudioInputStream(byteArrayInputStream, baseFormat.getFormat(), baseFormat.getFrameLength());
        }).toList();

        return mergeAudioStreams(audioInputStreams);
    }

    private AudioInputStream mergeAudioStreams(List<AudioInputStream> audioInputStreams) throws IOException {
        byte[] data = new byte[512];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for (AudioInputStream audioInputStream : audioInputStreams) {
            int nBytesRead = 0;
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(data, 0, data.length);
                if (nBytesRead != -1) {
                    byteArrayOutputStream.write(data, 0, nBytesRead);
                }
            }
            audioInputStream.close();
        }
        var format = audioInputStreams.get(0).getFormat();
        return new AudioInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                format,
                byteArrayOutputStream.size());
    }

}
