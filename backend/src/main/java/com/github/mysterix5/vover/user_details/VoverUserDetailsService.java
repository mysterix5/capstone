package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.history.HistoryService;
import com.github.mysterix5.vover.model.other.MultipleSubErrorException;
import com.github.mysterix5.vover.model.record.RecordDbEntity;
import com.github.mysterix5.vover.model.user_details.AllUsersForFriendsDTO;
import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverFriendDTO;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoverUserDetailsService {
    private final VoverUserDetailsMongoRepository userDetailsRepository;
    private final HistoryService historyService;

    public void addRequestToHistory(String username, List<RecordDbEntity> recordList) {
        String text = String.join(" ",
                recordList.stream()
                        .map(RecordDbEntity::getWord)
                        .toList()
                );
        List<String> ids = recordList.stream()
                .map(RecordDbEntity::getId)
                .toList();
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setText(text);
        historyEntry.setChoices(ids);
        historyEntry.setRequestTime(LocalDateTime.now());

        historyEntry = historyService.save(historyEntry);

        VoverUserDetails voverUserDetails = getUserDetails(username);
        voverUserDetails.getHistory().add(historyEntry.getId());
        userDetailsRepository.save(voverUserDetails);
        log.info("text '{}' is added to history of user '{}'", text, username);
    }

    public List<HistoryEntry> getHistory(String username) {
        List<String> historyIds = getUserDetails(username).getHistory();
        return historyService.getAllByIds(historyIds);
    }

    private VoverUserDetails getUserDetails(String username) {
        return userDetailsRepository.findById(username).orElse(new VoverUserDetails(username));
    }

    public void sendFriendRequest(String username, String friendName) {
        VoverUserDetails userDetails = getUserDetails(username);
        VoverUserDetails friendDetails = getUserDetails(friendName);
        if(userDetails.getFriendRequests().contains(friendName)){
            throw new MultipleSubErrorException("You already requested this friendship");
        }
        userDetails.getFriendRequests().add(friendName);
        friendDetails.getReceivedFriendRequests().add(username);
        userDetailsRepository.save(userDetails);
        userDetailsRepository.save(friendDetails);
    }

    public AllUsersForFriendsDTO getAllUsersWithFriendInfo(String username) {
        List<VoverFriendDTO> users = userDetailsRepository.findAll().stream()
                .map(VoverFriendDTO::new)
                .filter(u -> !u.getUsername().equalsIgnoreCase(username))
                .toList();
        VoverUserDetails userDetails = getUserDetails(username);

        AllUsersForFriendsDTO allUsersForFriendsDTO = new AllUsersForFriendsDTO();
        allUsersForFriendsDTO.setUsers(users);
        allUsersForFriendsDTO.setFriendRequests(userDetails.getFriendRequests());
        allUsersForFriendsDTO.setFriendRequestsReceived(userDetails.getReceivedFriendRequests());

        return allUsersForFriendsDTO;
    }

    public void ensureUserDetails(String username) {
        if(!userDetailsRepository.existsById(username)){
            userDetailsRepository.save(new VoverUserDetails(username));
        }
    }
}
