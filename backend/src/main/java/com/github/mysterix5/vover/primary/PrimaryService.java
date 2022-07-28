package com.github.mysterix5.vover.primary;

import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.record.*;
import com.github.mysterix5.vover.records.RecordMongoRepository;
import com.github.mysterix5.vover.records.StringOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryService {
    private final RecordMongoRepository wordsRepository;
    private final CloudService cloudService;

    public PrimaryResponseDTO onSubmittedText(String text, String username) {
        List<String> wordList = StringOperations.splitText(text);

        return createResponses(wordList, username);
    }

    private PrimaryResponseDTO createResponses(List<String> wordList, String username) {
        wordList = wordList.stream().map(String::toLowerCase).toList();
        Set<String> appearingWordsSet = wordList.stream().filter(StringOperations::isWord).collect(Collectors.toSet());
        Map<String, List<RecordDbResponseDTO>> dbWordsMap = createDbWordsMap(appearingWordsSet, username);
        List<String> defaultIds = new ArrayList<>();

        List<RecordResponseDTO> textWordsResponse = wordList.stream()
                .map(RecordResponseDTO::new)
                .peek(w -> {
                    if (appearingWordsSet.contains(w.getWord())) {
                        if (dbWordsMap.containsKey(w.getWord())) {
                            w.setAvailability(Availability.PUBLIC);
                            defaultIds.add(dbWordsMap.get(w.getWord()).get(0).getId());
                        } else {
                            w.setAvailability(Availability.NOT_AVAILABLE);
                            defaultIds.add(null);
                        }
                    } else {
                        w.setAvailability(Availability.INVALID);
                        defaultIds.add(null);
                    }
                }).toList();

        return new PrimaryResponseDTO(textWordsResponse, dbWordsMap, defaultIds);
    }

    private Map<String, List<RecordDbResponseDTO>> createDbWordsMap(Set<String> textWords, String username) {
        List<RecordDbEntity> allDbEntriesForWords = wordsRepository.findByWordIn(textWords);

        allDbEntriesForWords = allDbEntriesForWords.stream().filter(wordDb -> recordIsAllowedForUser(wordDb, username)).toList();

        Map<String, List<RecordDbResponseDTO>> dbWordsMap = new HashMap<>();
        for (RecordDbEntity w : allDbEntriesForWords) {
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(new RecordDbResponseDTO(w));
        }
        return dbWordsMap;
    }

    private boolean recordIsAllowedForUser(RecordDbEntity recordDbEntity, String username) {
        if(recordDbEntity.getAccessibility().equals(Accessibility.PUBLIC)){
            return true;
        }
        if(recordDbEntity.getCreator().equals(username)){
            return true;
        }
        // TODO if user->friends().contains(recordDbEntity.getCreator() && recordDbEntity.getAccessibility().equals(Accessibility.Friends)) return true;
        return false;
    }

    public AudioInputStream getMergedAudio(List<String> ids, String username) {
        List<RecordDbEntity> recordDbEntities = (List<RecordDbEntity>) wordsRepository.findAllById(ids);
        recordDbEntities.stream().map(r->{
            if(!recordIsAllowedForUser(r, username)){
                throw new MultipleSubErrorException("You are not allowed to get one of the records! Don't try to hack me! :(");
            }
            return null;
        });
        List<String> filePaths = ids.stream()
                .map(id -> recordDbEntities.stream()
                        .filter(wordDb -> Objects.equals(wordDb.getId(), id))
                        .findFirst()
                        .orElseThrow().getCloudFileName()).toList();
        try {
            return cloudService.loadMultipleAudioFromCloudAndMerge(filePaths);
        } catch (Exception e) {
            throw new MultipleSubErrorException("An error occurred while creating your audio file");
        }
    }
}
