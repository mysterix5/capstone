package com.github.mysterix5.vover.cloud_storage;

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

    public List<InputStream> loadMultipleMp3FromCloud(List<String> cloudFilePaths) {
        return cloudFilePaths.parallelStream()
                .map(path -> {
                    try {
                        return cloudRepository.find(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(ByteArrayInputStream::new)
                .collect(Collectors.toList());
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
