package com.github.mysterix5.vover.primary;

import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.record.*;
import com.github.mysterix5.vover.records.RecordMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryService {
    private final RecordMongoRepository wordsRepository;
    private final CloudService cloudService;

    public PrimaryResponseDTO onSubmittedText(String text, String username) {
        List<String> wordList = splitText(text);

        return createResponses(wordList, username);
    }

    private List<String> splitText(String text) {
        return Arrays.stream(text.split(" ")).toList();
    }

    private PrimaryResponseDTO createResponses(List<String> wordList, String username) {
        wordList = wordList.stream().map(String::toLowerCase).toList();
        Set<String> appearingWordsSet = wordList.stream().filter(this::wordValidCheck).collect(Collectors.toSet());
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


    // TODO grow with functionality
    private boolean wordValidCheck(String responseWord) {
        List<String> forbiddenChars = List.of("/", "%");
        for (String c : forbiddenChars) {
            if (responseWord.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean allowedWordForUser(String username, RecordDbEntity recordDbEntity) {
        if (recordDbEntity.getAccessibility() == Accessibility.PUBLIC
                || Objects.equals(recordDbEntity.getCreator(), username)
        ) {
            return true;
        }
        // if from friend return true
        return false;
    }

    private Map<String, List<RecordDbResponseDTO>> createDbWordsMap(Set<String> textWords, String username) {
        List<RecordDbEntity> allDbEntriesForWords = wordsRepository.findByWordIn(textWords);

        allDbEntriesForWords = allDbEntriesForWords.stream().filter(wordDb -> allowedWordForUser(username, wordDb)).toList();

        Map<String, List<RecordDbResponseDTO>> dbWordsMap = new HashMap<>();
        for (RecordDbEntity w : allDbEntriesForWords) {
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(new RecordDbResponseDTO(w));
        }
        return dbWordsMap;
    }

    public byte[] getMergedAudio(List<String> ids) {
        List<RecordDbEntity> wordDbEntities = (List<RecordDbEntity>) wordsRepository.findAllById(ids);
        List<String> filePaths = ids.stream()
                .map(id -> wordDbEntities.stream()
                        .filter(wordDb -> Objects.equals(wordDb.getId(), id))
                        .findFirst()
                        .orElseThrow().getCloudFileName()).toList();
        try {
            return cloudService.loadMultipleMp3FromCloudAndMerge(filePaths);
        } catch (Exception e) {
            throw new MultipleSubErrorException("An error occurred while creating your audio file");
        }
    }
}
