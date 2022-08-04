package com.github.mysterix5.vover.model.user_details;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoverFriendDTO {
    private String username;

    public VoverFriendDTO(VoverUserDetails userDetails) {
        username = userDetails.getUsername();
    }
}
