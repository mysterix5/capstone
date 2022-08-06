package com.github.mysterix5.vover.model.user_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeResponseDTO {
    private List<String> friends;
    private List<String> scope;

}
