package com.github.mysterix5.vover.words;

import com.github.mysterix5.vover.cloudStorage.CloudRepository;
import com.github.mysterix5.vover.model.word.RecordManagementDTO;
import com.github.mysterix5.vover.model.word.RecordPage;
import com.github.mysterix5.vover.model.word.WordDbEntity;
import com.github.mysterix5.vover.main.WordsMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService {
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


    public RecordPage getRecordPage(String username, int page, int size, String searchTerm) {
        Pageable paging = PageRequest.of(page, size);
        Page<WordDbEntity> resultPage = wordRepository.findAllByCreator(username, paging);

        return RecordPage.builder()
                .page(resultPage.getNumber())
                .noPages(resultPage.getTotalPages())
                .size(resultPage.getSize())
                .searchTerm(searchTerm)
                .records(resultPage.getContent().stream().map(RecordManagementDTO::new).toList())
                .build();
    }
}
