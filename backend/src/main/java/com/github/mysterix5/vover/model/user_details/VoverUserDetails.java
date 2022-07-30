package com.github.mysterix5.vover.model.user_details;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VoverUserDetails {
    private String username;
    private List<String> friends = new ArrayList<>();
    private List<HistoryEntry> history = new ArrayList<>();

    public VoverUserDetails(String username) {
        this.username = username;
    }
}
