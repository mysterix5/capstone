package com.github.mysterix5.vover.user_details;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserDetailsController {
    private final UserDetailsService userDetailsService;


}
