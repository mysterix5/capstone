package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VoverUserDetailsServiceTest {

    @Test
    void addRequestToHistory() {
        VoverUserDetailsMongoRepository mockedUserDetailsRepository = Mockito.mock(VoverUserDetailsMongoRepository.class);
        VoverUserDetailsService voverUserDetailsService = new VoverUserDetailsService(mockedUserDetailsRepository);

        String username = "user";
        List<String> words = List.of("ein", "kleiner", "test");

        VoverUserDetails testUserDetails = new VoverUserDetails(username);

        Mockito.when(mockedUserDetailsRepository.findById(username)).thenReturn(Optional.of(testUserDetails));

        voverUserDetailsService.addRequestToHistory(username, words);

        var expected = new VoverUserDetails(username);
        expected.getHistory().add(new HistoryEntry("ein kleiner test", testUserDetails.getHistory().get(0).getRequestTime()));

        Mockito.verify(mockedUserDetailsRepository).save(expected);
    }

    @Test
    void getHistory() {
        VoverUserDetailsMongoRepository mockedUserDetailsRepository = Mockito.mock(VoverUserDetailsMongoRepository.class);
        VoverUserDetailsService voverUserDetailsService = new VoverUserDetailsService(mockedUserDetailsRepository);

        String username = "user";
        List<String> words = List.of("ein", "kleiner", "test");
        List<HistoryEntry> history = List.of(new HistoryEntry(String.join(" ", words), LocalDateTime.now()));

        VoverUserDetails testUserDetails = new VoverUserDetails(username);
        testUserDetails.setHistory(history);

        Mockito.when(mockedUserDetailsRepository.findById(username)).thenReturn(Optional.of(testUserDetails));

        List<HistoryEntry> actual = voverUserDetailsService.getHistory(username);

        assertThat(actual).isEqualTo(history);
    }
}