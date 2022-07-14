package com.github.mysterix5.capstone.textHandling;

import com.github.mysterix5.capstone.cloudstorage.CloudService;
import com.github.mysterix5.capstone.model.AudioResponseDTO;
import com.github.mysterix5.capstone.model.Availability;
import com.github.mysterix5.capstone.model.WordResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TextService {
    private final WordsMongoRepository wordsRepository;
    private final CloudService cloudService;
    public List<WordResponseDTO> onSubmittedText(String text) {
        List<String> wordList = splitText(text);

        return createResponses(wordList);
    }

    private List<String> splitText(String text){
        return Arrays.stream(text.split(" ")).toList();
    }

    private List<WordResponseDTO> createResponses(List<String> wordList){
        return wordList.parallelStream()
                .map(w->new WordResponseDTO(w.toLowerCase()))
                .peek(wordResponseDTO->{
                    if(wordValidCheck(wordResponseDTO)){
                        getDbInformation(wordResponseDTO);
                    }
                }).toList();
    }

    private void getDbInformation(WordResponseDTO wordResponse){
        if(wordsRepository.existsByWord(wordResponse.getWord())){
            wordResponse.setAvailability(Availability.PUBLIC);
        }else{
            wordResponse.setAvailability(Availability.ABSENT);
        }
    }


    // TODO grow with functionality
    private boolean wordValidCheck(WordResponseDTO responseWord){
        List<String> forbiddenChars = List.of("/", "%");
        for(String c: forbiddenChars){
            if(responseWord.getWord().contains(c)){
                responseWord.setAvailability(Availability.INVALID);
                return false;
            }
        }
        return true;
    }

    public AudioResponseDTO getMergedWav(List<WordResponseDTO> words) throws IOException {

        List<String> urls = words.parallelStream()
                .map((word)-> wordsRepository.findByWord(word.getWord()))
                .map(wordDb -> {
                    if(wordDb.isEmpty()){
                        throw new IllegalArgumentException();
                    }
                    return wordDb.get().getUrl();
                })
                .toList();

        return cloudService.loadMultipleAudioFromCloudAndMerge(urls);
    }
}
