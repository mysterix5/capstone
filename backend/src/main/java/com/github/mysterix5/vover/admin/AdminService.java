package com.github.mysterix5.vover.admin;

import com.github.mysterix5.vover.audio_processing.AudioProcessingService;
import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.history.HistoryMongoRepository;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import com.github.mysterix5.vover.model.security.VoverUserEntity;
import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import com.github.mysterix5.vover.records.RecordService;
import com.github.mysterix5.vover.security.UserService;
import com.github.mysterix5.vover.user_details.VoverUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final VoverUserDetailsService userDetailsService;
    private final UserService userService;
    private final RecordService recordService;
    private final AudioProcessingService audioProcessingService;
    private final HistoryMongoRepository historyRepository;

    private final CloudService cloudService;

    public void ensureUserDetailsForAllUsers() {
        List<VoverUserEntity> users = userService.findAll();
        for (VoverUserEntity user : users) {
            userDetailsService.ensureUserDetails(user.getUsername());
        }
    }

    public void transferAllUserRecordingsToBugfixUser(String buggyUser, String bugfixUser) {
        userService.findByUsername(bugfixUser).orElseThrow();
        List<RecordDbEntity> recordings = recordService.findAllByUsername(buggyUser);
        for (RecordDbEntity rec : recordings) {
            recordService.changeRecordCreatorAndSetPrivate(rec, bugfixUser);
        }
    }

    public void changeCodecToStandard() {
        var records = recordService.findAll();

        for (var r : records) {
            try {
                log.info("record: {}", r.getCloudFileName());
                var recordInputStream = new ByteArrayInputStream(cloudService.find(r.getCloudFileName()));

                ByteArrayOutputStream byteArrayOutputStream = audioProcessingService.processAudio(recordInputStream);

                cloudService.save(r.getCloudFileName(), byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    public void historyCleanup() {
        List<VoverUserEntity> users = userService.findAll();
        for (VoverUserEntity user : users) {
            VoverUserDetails userDetails = userDetailsService.getUserDetails(user.getUsername());

            List<HistoryEntry> userHistory = historyRepository.findAllByIdIn(userDetails.getHistory(), null);
            Iterator<HistoryEntry> iteratorOuter = userHistory.iterator();
            while (iteratorOuter.hasNext()) {
                HistoryEntry historyEntryOuter = iteratorOuter.next();
                LocalDateTime outerDateTime = historyEntryOuter.getRequestTime();
                Iterator<HistoryEntry> iteratorInner = userHistory.iterator();
                while (iteratorInner.hasNext()) {
                    HistoryEntry historyEntryInner = iteratorInner.next();
                    LocalDateTime innerDateTime = historyEntryInner.getRequestTime();
                    if (historyEntryInner != historyEntryOuter && historyEntryInner.sameRequest(historyEntryOuter)) {
                        if (innerDateTime.isAfter(outerDateTime)) {
                            historyRepository.delete(historyEntryOuter);
                            iteratorOuter.remove();
                            break;
                        } else {
                            historyRepository.delete(historyEntryInner);
                            iteratorInner.remove();
                        }
                    }
                }
            }
        }
    }
}
