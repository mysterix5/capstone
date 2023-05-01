package com.github.mysterix5.vover.admin;

import com.github.mysterix5.vover.model.admin.RecordingsTransferDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * This will mainly be used for db migration / consistency
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/userdetails")
    public void ensureUserDetailsForAllUsers() {
        log.info("ADMIN: ensure user details for all users");
        adminService.ensureUserDetailsForAllUsers();
    }

    /**
     * A strange bug occurred where one users audio recordings couldnt be mixed with recordings from other people.
     * The audio must be somehow corrupted or recorded with a total different codec setting.
     * This way i save the audio on a debug account for later debugging but remove them from the public db.
     */
    @PostMapping("/transferrecordings")
    public void transferAllUserRecordingsToBugfixUser(@RequestBody RecordingsTransferDTO recordingsTransferDTO) {
        adminService.transferAllUserRecordingsToBugfixUser(recordingsTransferDTO.getBuggyUser(), recordingsTransferDTO.getBugfixUser());
    }

    /**
     * execute FFMpeg audio processing on all records
     */
    @PutMapping("/changecodec")
    public void changeCodecToStandard() {
        log.info("ADMIN: change codec of all records to standard");
        adminService.changeCodecToStandard();
    }

    @PutMapping("/history-cleanup")
    public void historyCleanup() {
        log.info("ADMIN: cleanup history");
        adminService.historyCleanup();
    }
}
