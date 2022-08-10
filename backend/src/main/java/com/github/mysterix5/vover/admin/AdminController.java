package com.github.mysterix5.vover.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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
}
