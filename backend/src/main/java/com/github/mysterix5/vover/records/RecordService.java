package com.github.mysterix5.vover.records;

import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.record.Accessibility;
import com.github.mysterix5.vover.model.record.RecordManagementDTO;
import com.github.mysterix5.vover.model.record.RecordPage;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {
    private final RecordMongoRepository wordRepository;
    private final CloudService cloudService;

    public void addWordToDb(String word, String creator, String tag, String accessibility, byte[] audio) throws IOException {
        if(!StringOperations.isWord(word)){
            throw new MultipleSubErrorException("The metadata you send with your record was not acceptable",
                    List.of("This is not a valid word", "only letters, no white spaces, numbers or special characters"));
        }
        if(!StringOperations.isWord(tag)){
            throw new MultipleSubErrorException("The metadata you send with your record was not acceptable",
                    List.of("This is not a valid tag", "only letters, no white spaces, numbers or special characters"));
        }
        String cloudFileName = createCloudFileName(word, creator, tag, accessibility);
        RecordDbEntity recordDbEntity = new RecordDbEntity(word, creator, tag, accessibility, cloudFileName);
        cloudService.save(cloudFileName, audio);
        wordRepository.save(recordDbEntity);
    }

    private String createCloudFileName(String word, String creator, String tag, String accessibility) {
        String cloudId = UUID.randomUUID().toString();
        StringBuilder cloudFileName = new StringBuilder();
        cloudFileName
                .append(word).append("-")
                .append(creator).append("-")
                .append(tag).append("-")
                .append(accessibility).append("-")
                .append(cloudId)
                .append(".mp3");
        return cloudFileName.toString();
    }

    public RecordPage getRecordPage(String username, int page, int size, String searchTerm) {
        Pageable paging = PageRequest.of(page, size);
        Page<RecordDbEntity> resultPage = wordRepository.findAllByCreator(username, paging);

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
        RecordDbEntity word = wordRepository.findById(id).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The audio file you requested is not yours. Don't try to hack me! :(");
        }
        try {
            return cloudService.find(word.getCloudFileName());
        } catch (IOException e) {
            throw new RuntimeException("Your audio could not be found in the cloud");
        }
    }

    public void deleteRecord(String id, String username) {
        RecordDbEntity word = wordRepository.findById(id).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The audio file you requested is not yours. Don't try to hack me! :(");
        }
        try {
            cloudService.delete(word.getCloudFileName());
            wordRepository.delete(word);
        } catch (IOException e) {
            throw new RuntimeException("Deleting your audio file failed, did nothing");
        }
        wordRepository.delete(word);
    }

    public void changeRecordMetadata(RecordManagementDTO recordManagementDTO, String username) {
        RecordDbEntity word = wordRepository.findById(recordManagementDTO.getId()).orElseThrow();
        if (!word.getCreator().equals(username)) {
            throw new RuntimeException("The record you requested to change is not yours. Don't try to hack me! :(");
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
        word.setCloudFileName(createCloudFileName(word.getWord(), word.getCreator(), word.getTag(), word.getAccessibility().toString()));

        try {
            cloudService.move(oldCloudName, word.getCloudFileName());
            wordRepository.save(word);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong changing your record metadata");
        }
    }
}
