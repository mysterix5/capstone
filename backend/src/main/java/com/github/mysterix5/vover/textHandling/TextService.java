package com.github.mysterix5.vover.textHandling;

import com.github.mysterix5.vover.cloudstorage.CloudService;
import com.github.mysterix5.vover.model.Availability;
import com.github.mysterix5.vover.model.WordDbEntity;
import com.github.mysterix5.vover.model.WordResponseDTO;
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

    public List<WordResponseDTO> onSubmittedText(String text) {
        List<String> wordList = splitText(text);

        return createResponses(wordList);
    }

    private List<String> splitText(String text) {
        return Arrays.stream(text.split(" ")).toList();
    }

    private List<WordResponseDTO> createResponses(List<String> wordList) {
        wordList = wordList.stream().map(String::toLowerCase).toList();
        Set<String> appearingWordsSet = wordList.stream().filter(this::wordValidCheck).collect(Collectors.toSet());
        Map<String, List<WordDbEntity>> dbWordsMap = createDbWordsMap(appearingWordsSet);

        return wordList.stream()
                .map(WordResponseDTO::new)
                .peek(w -> {
                        if(appearingWordsSet.contains(w.getWord())){
                            if(dbWordsMap.containsKey(w.getWord())){
                                w.setAvailability(Availability.PUBLIC);
                            }else{
                                w.setAvailability(Availability.ABSENT);
                            }
                        }else{
                            w.setAvailability(Availability.INVALID);
                        }
                }).toList();
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

    private Map<String, List<WordDbEntity>> createDbWordsMap(Set<String> textWords){
        List<WordDbEntity> allDbEntriesForWords = wordsRepository.findByWordIn(textWords);

        Map<String, List<WordDbEntity>> dbWordsMap = new HashMap<>();
        for (WordDbEntity w : allDbEntriesForWords) {
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(w);
        }
        return dbWordsMap;
    }

    public AudioInputStream getMergedAudio(List<WordResponseDTO> textWordList) throws IOException {
        Set<String> appearingWordsSet = textWordList.stream().map(WordResponseDTO::getWord).collect(Collectors.toSet());

        Map<String, List<WordDbEntity>> dbWordsMap = createDbWordsMap(appearingWordsSet);
        if(dbWordsMap.size()< appearingWordsSet.size()){
            throw new IllegalArgumentException("some of the words are not present in the db");
        }

        Random rand = new Random();
        List<String> urls = textWordList.stream()
                .map(wordResponseDTO -> {
                    var wordChoices = dbWordsMap.get(wordResponseDTO.getWord());
                    int randomIndex = rand.nextInt(wordChoices.size());
                    return wordChoices.get(randomIndex);
                }).map(wordDbEntity -> ((WordDbEntity) wordDbEntity).getCloudFileName())
                .toList();

        return cloudService.loadMultipleAudioFromCloudAndMerge(urls);
    }
}
