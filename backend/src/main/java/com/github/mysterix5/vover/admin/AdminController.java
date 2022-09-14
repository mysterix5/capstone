package com.github.mysterix5.vover.admin;

import com.github.mysterix5.vover.model.admin.RecordingsTransferDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void ensureUserDetailsForAllUsers(){
        log.info("ADMIN: ensure user details for all users");
        adminService.ensureUserDetailsForAllUsers();
    }

    /**
     * A strange bug occurred where one users audio recordings couldn't be mixed with recordings from other people.
     * The audio must be somehow corrupted or recorded with a total different codec setting.
     * This way I save the audio on a debug account for later debugging but remove them from the public db.
     * @param recordingsTransferDTO object with buggyUser and bugfixUser
     */
    @PostMapping("/transferrecordings")
    public void transferAllUserRecordingsToBugfixUser(@RequestBody RecordingsTransferDTO recordingsTransferDTO) {
        log.info("ADMIN BUGFIX: transfer all recordings from user '{}' to user '{}'", recordingsTransferDTO.getBuggyUser(), recordingsTransferDTO.getBugfixUser());
        adminService.transferAllUserRecordingsToBugfixUser(recordingsTransferDTO.getBuggyUser(), recordingsTransferDTO.getBugfixUser());
    }
}
