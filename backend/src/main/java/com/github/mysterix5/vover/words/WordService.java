package com.github.mysterix5.vover.words;

import com.github.mysterix5.vover.cloudStorage.CloudService;
import com.github.mysterix5.vover.model.other.Accessibility;
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

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService {
    private final WordsMongoRepository wordRepository;
    private final CloudService cloudService;

    public void addWordToDb(String word, String creator, String tag, String accessibility, byte[] audio) throws IOException {
        String cloudFileName = createCloudFileName(word, creator, tag);
        WordDbEntity wordDbEntity = new WordDbEntity(word, creator, tag, accessibility, cloudFileName);
        cloudService.save(cloudFileName, audio);
        wordRepository.save(wordDbEntity);
    }

    private String createCloudFileName(String word, String creator, String tag) {
        String cloudId = UUID.randomUUID().toString();
        StringBuilder cloudFileName = new StringBuilder();
        cloudFileName
                .append(word).append("-")
                .append(creator).append("-")
                .append(tag).append("-")
                .append(cloudId)
                .append(".mp3");
        return cloudFileName.toString();
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
                .accessibilityChoices(Accessibility.values())
                .build();
    }

    public AudioInputStream getAudio(String id, String username) {
        WordDbEntity word = wordRepository.findById(id).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The audio file you requested is not yours. Don't try to hack me! :(");
        }
        try {
            return cloudService.find(word.getCloudFileName());
        } catch (IOException e) {
            throw new RuntimeException("your audio could not be found in the cloud");
        }
    }

    public void deleteRecord(String id, String username) {
        WordDbEntity word = wordRepository.findById(id).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The audio file you requested is not yours. Don't try to hack me! :(");
        }
        try {
            cloudService.delete(word.getCloudFileName());
        } catch (IOException e) {
            throw new RuntimeException("Deleting your audio file failed, did nothing");
        }
        wordRepository.delete(word);
    }

    public void changeRecordMetadata(RecordManagementDTO recordManagementDTO, String username) {
        WordDbEntity word = wordRepository.findById(recordManagementDTO.getId()).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The audio file you requested is not yours. Don't try to hack me! :(");
        }
        if (word.getWord().equals(recordManagementDTO.getWord())
                && word.getTag().equals(recordManagementDTO.getTag())
                && word.getAccessibility().equals(recordManagementDTO.getAccessibility())
        ) {
            throw new RuntimeException("Nothing was changed");
        }
        word.setWord(recordManagementDTO.getWord());
        word.setTag(recordManagementDTO.getTag());
        word.setAccessibility(recordManagementDTO.getAccessibility());

        String oldCloudName = word.getCloudFileName();
        String newCloudName = createCloudFileName(word.getWord(), word.getCreator(), word.getTag());

        try {
            if(!oldCloudName.equals(newCloudName)){
                cloudService.move(oldCloudName, newCloudName);
            }
            wordRepository.save(word);
        }catch (Exception e){
            throw new RuntimeException("Something went wrong changing your record metadata");
        }
    }
}
