package com.github.mysterix5.vover.addWords;

import com.github.mysterix5.vover.cloudstorage.CloudRepository;
import com.github.mysterix5.vover.model.WordDbEntity;
import com.github.mysterix5.vover.textHandling.WordsMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddWordService {
    private final WordsMongoRepository wordRepository;
    private final CloudRepository cloudRepository;

    public void addWordToDb(String word, String creator, String tag, String accessibility, byte[] audio) throws IOException {
        String cloudId = UUID.randomUUID().toString();
        StringBuilder cloudFileName = new StringBuilder();
        cloudFileName.append(word).append("-").append(creator).append("-").append(tag).append("-").append(cloudId).append(".mp3");
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, accessibility, cloudFileName.toString());
        cloudRepository.save(cloudFileName.toString(), audio);
        wordRepository.save(wordDbEntity);
    }
}
