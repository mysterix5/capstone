package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.other.VoverErrorDTO;
import com.github.mysterix5.vover.model.user_details.AllUsersForFriendsDTO;
import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/userdetails")
public class VoverUserDetailsController {
    private final VoverUserDetailsService voverUserDetailsService;

    @GetMapping("/history")
    public ResponseEntity<List<HistoryEntry>> getHistory(Principal principal) {
        return ResponseEntity.ok(voverUserDetailsService.getHistory(principal.getName()));
    }

    @PostMapping("/friendrequest")
    public ResponseEntity<Object> sendFriendRequest(@RequestBody String friendName, Principal principal) {
        try {
            log.info("user '{}' sent friend request to user '{}'", principal.getName(), friendName);
            voverUserDetailsService.sendFriendRequest(principal.getName(), friendName);
            return ResponseEntity.ok().build();
        }catch(MultipleSubErrorException e){
            return ResponseEntity.badRequest().body(new VoverErrorDTO(e));
        }catch(Exception e) {
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("something went wrong sending your friend request, sorry"));
        }
    }

    @GetMapping("/friend")
    public ResponseEntity<Object> getAllUsersWithFriendInfo(Principal principal){
        try {
            AllUsersForFriendsDTO users = voverUserDetailsService.getAllUsersWithFriendInfo(principal.getName());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoverErrorDTO("Something went wrong collecting all users"));
        }
    }
}
