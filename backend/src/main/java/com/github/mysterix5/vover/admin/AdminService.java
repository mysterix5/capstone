package com.github.mysterix5.vover.admin;

import com.github.mysterix5.vover.audio_processing.AudioProcessingService;
import com.github.mysterix5.vover.cloud_storage.CloudService;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import com.github.mysterix5.vover.model.security.VoverUserEntity;
import com.github.mysterix5.vover.records.RecordService;
import com.github.mysterix5.vover.security.UserService;
import com.github.mysterix5.vover.user_details.VoverUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final VoverUserDetailsService userDetailsService;
    private final UserService userService;
    private final RecordService recordService;
    private final AudioProcessingService audioProcessingService;

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
}
