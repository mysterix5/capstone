package com.github.mysterix5.vover.primary;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.primary.PrimarySubmitDTO;
import com.github.mysterix5.vover.model.primary.WordAvailability;
import com.github.mysterix5.vover.model.record.*;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import com.github.mysterix5.vover.records.RecordMongoRepository;
import com.github.mysterix5.vover.static_tools.StringOperations;
import com.github.mysterix5.vover.user_details.VoverUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryService {
    private final RecordMongoRepository recordRepository;
    private final CloudService cloudService;
    private final VoverUserDetailsService voverUserDetailsService;

    public PrimaryResponseDTO onSubmittedText(PrimarySubmitDTO primarySubmitDTO, String username) {
        List<String> wordList = StringOperations.splitText(primarySubmitDTO.getText());

        return createResponses(wordList, primarySubmitDTO.getScope(), username);
    }

    private PrimaryResponseDTO createResponses(List<String> wordList, List<String> scope, String username) {
        // list with all words in lowercase
        wordList = wordList.stream().map(String::toLowerCase).toList();
        // set (no doubles) of legal words appearing in request
        Set<String> appearingWordsSet = wordList.stream().filter(StringOperations::isWord).collect(Collectors.toSet());
        if (appearingWordsSet.isEmpty()) {
            throw new MultipleSubErrorException("You didn't submit a single valid word...");
        }
        VoverUserDetails userDetails = voverUserDetailsService.getUserDetails(username);
        Collections.sort(scope);
        if(!scope.equals(userDetails.getScope())){
            userDetails.setScope(scope);
            voverUserDetailsService.save(userDetails);
        }
        // create a map with record choices to every word in appearingWordsSet
        // each choice has an RecordAvailability
        Map<String, List<RecordDbResponseDTO>> dbWordsMap = createDbWordsMap(appearingWordsSet, userDetails);

        // sort dbWordsMap by RecordAvailability
        for (List<RecordDbResponseDTO> recordDbResponseDTOList : dbWordsMap.values()) {
            recordDbResponseDTOList.sort(
                    Comparator.comparing(RecordDbResponseDTO::getAvailability).reversed()
            );
        }

        List<String> defaultIds = new ArrayList<>();
        List<WordResponseDTO> textWordsResponse = wordList.stream()
                .map(WordResponseDTO::new)
                .peek(w -> {
                    if (appearingWordsSet.contains(w.getWord())) {
                        if (dbWordsMap.containsKey(w.getWord())) {
                            // word is legal and records available
                            w.setAvailability(WordAvailability.AVAILABLE);
                            defaultIds.add(dbWordsMap.get(w.getWord()).get(0).getId());
                        } else {
                            // word is legal but no records available
                            w.setAvailability(WordAvailability.NOT_AVAILABLE);
                            defaultIds.add(null);
                        }
                    } else {
                        // word is illegal
                        w.setAvailability(WordAvailability.INVALID);
                        defaultIds.add(null);
                    }
                }).toList();

        return new PrimaryResponseDTO(textWordsResponse, dbWordsMap, defaultIds);
    }

    private Map<String, List<RecordDbResponseDTO>> createDbWordsMap(Set<String> textWords, VoverUserDetails userDetails) {
        List<RecordDbEntity> allDbEntriesForWords = recordRepository.findByWordIn(textWords);

        Map<String, List<RecordDbResponseDTO>> dbWordsMap = new HashMap<>();
        for (RecordDbEntity w : allDbEntriesForWords) {
            RecordAvailability recordAvailability;
            if (w.getCreator().equals(userDetails.getUsername())) {
                // record from requesting user
                recordAvailability = RecordAvailability.MYSELF;
            } else if (w.getAccessibility().equals(Accessibility.PRIVATE)) {
                // record is 'PRIVATE'
                continue;
            } else if (userDetails.getScope().contains(w.getCreator())) {
                // record from user in scope
                recordAvailability = RecordAvailability.SCOPE;
            } else if (userDetails.getFriends().contains(w.getCreator())) {
                // record from user in friends
                recordAvailability = RecordAvailability.FRIENDS;
            } else if (w.getAccessibility().equals(Accessibility.PUBLIC)) {
                // record accessibility is 'PUBLIC'
                recordAvailability = RecordAvailability.PUBLIC;
            } else {
                // record is from user not in friends and accessibility is 'FRIENDS'
                continue;
            }
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(new RecordDbResponseDTO(w, recordAvailability));
        }
        return dbWordsMap;
    }

    private boolean recordIsAllowedForUser(RecordDbEntity recordDbEntity, VoverUserDetails userDetails) {
        if (recordDbEntity.getAccessibility().equals(Accessibility.PUBLIC)) {
            return true;
        }
        if (recordDbEntity.getCreator().equals(userDetails.getUsername())) {
            return true;
        }
        if (userDetails.getFriends().contains(recordDbEntity.getCreator()) && recordDbEntity.getAccessibility().equals(Accessibility.FRIENDS)) {
            return true;
        }
        return false;
    }

    public byte[] getMergedAudio(List<String> ids, String username) {
        List<RecordDbEntity> recordDbEntities = (List<RecordDbEntity>) recordRepository.findAllById(ids);
        VoverUserDetails userDetails = voverUserDetailsService.getUserDetails(username);
        for (var r : recordDbEntities) {
            if (!recordIsAllowedForUser(r, userDetails)) {
                throw new MultipleSubErrorException("You are not allowed to get one of the records! Don't try to hack me! :(");
            }
        }
        List<String> filePaths = ids.stream()
                .map(id -> recordDbEntities.stream()
                        .filter(wordDb -> Objects.equals(wordDb.getId(), id))
                        .findFirst()
                        .orElseThrow()
                        .getCloudFileName())
                .toList();
        try {
            List<InputStream> audioInputStreams = cloudService.loadMultipleMp3FromCloud(filePaths);
            byte[] merged = mergeAudioWithJaffree(audioInputStreams);
            voverUserDetailsService.addRequestToHistory(username,
                    ids.stream()
                            .map(id -> recordDbEntities.stream()
                                    .filter(wordDb -> Objects.equals(wordDb.getId(), id))
                                    .findFirst()
                                    .orElseThrow()
                            ).toList()
            );
            return merged;
        } catch (Exception e) {
            throw new MultipleSubErrorException("An error occurred while creating your audio file");
        }
    }

    private byte[] mergeAudioWithJaffree(List<InputStream> inputStreams) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            for (InputStream inputStream : inputStreams) {
                FFmpeg.atPath()
                        .addInput(PipeInput.pumpFrom(inputStream))
                        .addOutput(
                                PipeOutput.pumpTo(byteArrayOutputStream)
                                        .setFormat("mp3")
                        )
                        .execute();
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
