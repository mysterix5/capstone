package com.github.mysterix5.vover.cloud_storage;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {
    private final CloudRepository cloudRepository;

    public byte[] loadMultipleMp3FromCloudAndMerge(List<String> cloudFilePaths) {
        List<InputStream> inputStreams = cloudFilePaths.parallelStream()
                .map(path -> {
                    try {
                        return cloudRepository.find(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(ByteArrayInputStream::new)
                .collect(Collectors.toList());

        return mergeWithJaffree(inputStreams);
    }

    public byte[] mergeWithJaffree(List<InputStream> inputStreams) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            for (InputStream inputStream : inputStreams) {
                FFmpeg.atPath()
                        .addInput(PipeInput.pumpFrom(inputStream))
                        .addOutput(
                                PipeOutput.pumpTo(byteArrayOutputStream)
                                        .setFormat("mp3")
                        )
                        .execute();
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String filePath, byte[] byteArray) throws IOException {
        cloudRepository.save(filePath, byteArray);
    }

    public byte[] find(String filePath) throws IOException {
        return cloudRepository.find(filePath);
    }

    public void delete(String cloudFileName) throws IOException {
        cloudRepository.delete(cloudFileName);
    }

    public void move(String oldCloudFileName, String newCloudFileName) throws IOException {
        cloudRepository.move(oldCloudFileName, newCloudFileName);
    }
}
