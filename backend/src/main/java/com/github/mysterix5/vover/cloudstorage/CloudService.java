package com.github.mysterix5.vover.cloudstorage;

import com.github.mysterix5.vover.model.AudioResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {
    private final CloudRepository cloudRepository;

    private AudioResponseDTO createAudioResponseDTO(AudioInputStream audioIn) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AudioSystem.write(audioIn, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
        byte[] arrayWithHeader = byteArrayOutputStream.toByteArray();

        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(arrayWithHeader);
        audioResponseDTO.setContentLength((int) (audioIn.getFrameLength()*audioIn.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO loadMultipleAudioFromCloudAndMerge(List<String> cloudFilePaths) throws IOException {

        final AtomicInteger frameLength = new AtomicInteger(0);
        final AtomicReference<AudioFileFormat> atomicFormat= new AtomicReference<>(null);

        List<AudioInputStream> audioStreamList = cloudFilePaths.parallelStream()
                .map(filePath -> {
                    try {
                        return cloudRepository.find(filePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(bytes -> {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    AudioFileFormat format;
                    try {
                        format = AudioSystem.getAudioFileFormat(byteArrayInputStream);
                        atomicFormat.set(format);
                    } catch (UnsupportedAudioFileException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    frameLength.addAndGet(format.getFrameLength());
                    return new AudioInputStream(byteArrayInputStream, format.getFormat(), format.getFrameLength());
                })
                .toList();

        SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(audioStreamList));

        AudioInputStream appended =
                new AudioInputStream(
                        sequenceInputStream,
                        atomicFormat.get().getFormat(),
                        frameLength.get());

        return createAudioResponseDTO(appended);
    }

}
