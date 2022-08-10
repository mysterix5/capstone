package com.github.mysterix5.vover.model.user_details;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VoverUserDetails {
    @Id
    private String username;
    private List<String> scope = new ArrayList<>();
    private List<String> friends = new ArrayList<>();
    private List<String> friendRequests = new ArrayList<>();
    private List<String> receivedFriendRequests = new ArrayList<>();
    private List<String> history = new ArrayList<>();

    public VoverUserDetails(String username) {
        this.username = username;
    }
}
