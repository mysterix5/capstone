package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsServiceTest {

    @Test
    void addRequestToHistory() {
        UserDetailsMongoRepository mockedUserDetailsRepository = Mockito.mock(UserDetailsMongoRepository.class);
        UserDetailsService userDetailsService = new UserDetailsService(mockedUserDetailsRepository);

        String username = "user";
        List<String> words = List.of("ein", "kleiner", "test");

        VoverUserDetails testUserDetails = new VoverUserDetails(username);

        Mockito.when(mockedUserDetailsRepository.findById(username)).thenReturn(Optional.of(testUserDetails));

        userDetailsService.addRequestToHistory(username, words);

        var expected = new VoverUserDetails(username);
        expected.getHistory().add(new HistoryEntry("ein kleiner test", testUserDetails.getHistory().get(0).getRequestTime()));

        Mockito.verify(mockedUserDetailsRepository).save(expected);
    }

    @Test
    void getHistory() {
        UserDetailsMongoRepository mockedUserDetailsRepository = Mockito.mock(UserDetailsMongoRepository.class);
        UserDetailsService userDetailsService = new UserDetailsService(mockedUserDetailsRepository);

        String username = "user";
        List<String> words = List.of("ein", "kleiner", "test");
        List<HistoryEntry> history = List.of(new HistoryEntry(String.join(" ", words), LocalDateTime.now()));

        VoverUserDetails testUserDetails = new VoverUserDetails(username);
        testUserDetails.setHistory(history);

        Mockito.when(mockedUserDetailsRepository.findById(username)).thenReturn(Optional.of(testUserDetails));

        List<HistoryEntry> actual = userDetailsService.getHistory(username);

        assertThat(actual).isEqualTo(history);
    }
}