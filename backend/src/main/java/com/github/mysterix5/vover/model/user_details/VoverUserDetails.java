package com.github.mysterix5.vover.model.user_details;

import lombok.Data;

import java.util.List;

@Data
public class VoverUserDetails {
    private String username;
    private List<String> friends;
    private List<HistoryEntry> history;
}
