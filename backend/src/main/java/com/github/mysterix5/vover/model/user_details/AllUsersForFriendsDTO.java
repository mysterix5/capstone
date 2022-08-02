package com.github.mysterix5.vover.model.user_details;

import lombok.Data;

import java.util.List;

@Data
public class AllUsersForFriendsDTO {
    private List<VoverFriendDTO> users;
    private List<String> friendRequests;
    private List<String> friendRequestsReceived;
}
