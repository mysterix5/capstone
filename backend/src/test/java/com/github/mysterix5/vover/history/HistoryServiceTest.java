package com.github.mysterix5.vover.history;

import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    HistoryMongoRepository mockedHistoryRepository;

    @Test
    void saveNewHistoryEntry() {

        var time = LocalDateTime.now();
        HistoryService historyService = new HistoryService(mockedHistoryRepository);
        try (MockedStatic<LocalDateTime> mb = Mockito.mockStatic(LocalDateTime.class)) {
            mb.when(LocalDateTime::now).thenReturn(time).thenReturn(time);
            HistoryEntry historyEntry = new HistoryEntry("testid", "ein kleiner test", List.of("id1", "id2", "id3"), time);

            String username = "user";
            VoverUserDetails testUserDetails = new VoverUserDetails(username);

            Mockito.when(mockedHistoryRepository.findAllByIdIn(List.of(), null)).thenReturn(List.of());

            historyService.save(testUserDetails, historyEntry);

            Mockito.verify(mockedHistoryRepository).save(historyEntry);
            assertThat(testUserDetails.getHistory()).isEqualTo(List.of("testid"));
        }
    }
    @Test
    void saveHistoryEntryAgain() {
        HistoryService historyService = new HistoryService(mockedHistoryRepository);
        var time = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> mb = Mockito.mockStatic(LocalDateTime.class)) {
            mb.when(LocalDateTime::now).thenReturn(time).thenReturn(time);
            HistoryEntry historyEntry = new HistoryEntry("newid", "ein kleiner test", List.of("id1", "id2", "id3"), time);

            String username = "user";
            VoverUserDetails testUserDetails = new VoverUserDetails(username);

            HistoryEntry oldHistoryEntry = new HistoryEntry("oldid", "ein kleiner test", List.of("id1", "id2", "id3"), time.minusHours(1));
            testUserDetails.setHistory(List.of("oldid"));

            Mockito.when(mockedHistoryRepository.findAllByIdIn(List.of("oldid"), null)).thenReturn(List.of(oldHistoryEntry));

            historyService.save(testUserDetails, historyEntry);

            Mockito.verify(mockedHistoryRepository).save(oldHistoryEntry);
            assertThat(oldHistoryEntry.getRequestTime()).isEqualTo(time);
            assertThat(testUserDetails.getHistory()).isEqualTo(List.of("oldid"));
        }
    }
}
