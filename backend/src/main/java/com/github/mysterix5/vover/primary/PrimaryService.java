package com.github.mysterix5.vover.primary;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
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

    public PrimaryResponseDTO onSubmittedText(String text, String username) {
        List<String> wordList = StringOperations.splitText(text);

        return createResponses(wordList, username);
    }

    private PrimaryResponseDTO createResponses(List<String> wordList, String username) {
        wordList = wordList.stream().map(String::toLowerCase).toList();
        Set<String> appearingWordsSet = wordList.stream().filter(StringOperations::isWord).collect(Collectors.toSet());
        if(appearingWordsSet.isEmpty()){
            throw new MultipleSubErrorException("You didn't submit a single valid word...");
        }
        VoverUserDetails userDetails = voverUserDetailsService.getUserDetails(username);
        Map<String, List<RecordDbResponseDTO>> dbWordsMap = createDbWordsMap(appearingWordsSet, userDetails);
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

    private Map<String, List<RecordDbResponseDTO>> createDbWordsMap(Set<String> textWords, VoverUserDetails userDetails) {
        List<RecordDbEntity> allDbEntriesForWords = recordRepository.findByWordIn(textWords);

        allDbEntriesForWords = allDbEntriesForWords.stream().filter(wordDb -> recordIsAllowedForUser(wordDb, userDetails)).toList();

        Map<String, List<RecordDbResponseDTO>> dbWordsMap = new HashMap<>();
        for (RecordDbEntity w : allDbEntriesForWords) {
            if (!dbWordsMap.containsKey(w.getWord())) {
                dbWordsMap.put(w.getWord(), new ArrayList<>());
            }
            dbWordsMap.get(w.getWord()).add(new RecordDbResponseDTO(w));
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
        if(userDetails.getFriends().contains(recordDbEntity.getCreator()) && recordDbEntity.getAccessibility().equals(Accessibility.FRIENDS)){
            return true;
        }
        // TODO if user->friends().contains(recordDbEntity.getCreator() && recordDbEntity.getAccessibility().equals(Accessibility.Friends)) return true;
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
