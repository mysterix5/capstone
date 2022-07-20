package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.cloudstorage.CloudService;
import com.github.mysterix5.vover.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextService {
    private final WordsMongoRepository wordsRepository;
    private final CloudService cloudService;

    public TextResponseDTO onSubmittedText(String text) {
        List<String> wordList = splitText(text);

        return createResponses(wordList);
    }

    private List<String> splitText(String text) {
        return Arrays.stream(text.split(" ")).toList();
    }

    private TextResponseDTO createResponses(List<String> wordList) {
        wordList = wordList.stream().map(String::toLowerCase).toList();
        Set<String> appearingWordsSet = wordList.stream().filter(this::wordValidCheck).collect(Collectors.toSet());
        Map<String, List<WordDbResponseDTO>> dbWordsMap = createDbWordsMap(appearingWordsSet);

        List<WordResponseDTO> textWordsResponse = wordList.stream()
                .map(WordResponseDTO::new)
                .peek(w -> {
                    if (appearingWordsSet.contains(w.getWord())) {
                        if (dbWordsMap.containsKey(w.getWord())) {
                            w.setAvailability(Availability.PUBLIC);
                        } else {
                            w.setAvailability(Availability.ABSENT);
                        }
                    } else {
                        w.setAvailability(Availability.INVALID);
                    }
                }).toList();

        return new TextResponseDTO(textWordsResponse, dbWordsMap);
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

    private Map<String, List<WordDbResponseDTO>> createDbWordsMap(Set<String> textWords) {
        List<WordDbEntity> allDbEntriesForWords = wordsRepository.findByWordIn(textWords);

        Map<String, List<WordDbResponseDTO>> dbWordsMap = new HashMap<>();
        for (WordDbEntity w : allDbEntriesForWords) {
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(new WordDbResponseDTO(w));
        }
        return dbWordsMap;
    }

    public AudioInputStream getMergedAudio(List<String> ids) {
        List<WordDbEntity> wordDbEntities = (List<WordDbEntity>) wordsRepository.findAllById(ids);
        List<String> filePaths = ids.stream()
                .map(id->wordDbEntities.stream()
                        .filter(wordDb -> Objects.equals(wordDb.getId(), id))
                        .findFirst()
                        .orElseThrow().getCloudFileName()).toList();
        try {
            return cloudService.loadMultipleAudioFromCloudAndMerge(filePaths);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
