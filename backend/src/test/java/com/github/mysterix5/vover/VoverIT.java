package com.github.mysterix5.vover;

import com.github.mysterix5.vover.model.primary.PrimaryResponseDTO;
import com.github.mysterix5.vover.model.primary.PrimarySubmitDTO;
import com.github.mysterix5.vover.model.primary.WordAvailability;
import com.github.mysterix5.vover.model.record.Accessibility;
import com.github.mysterix5.vover.model.record.RecordManagementDTO;
import com.github.mysterix5.vover.model.record.RecordPage;
import com.github.mysterix5.vover.model.security.LoginResponse;
import com.github.mysterix5.vover.model.security.UserAuthenticationDTO;
import com.github.mysterix5.vover.model.security.UserRegisterDTO;
import com.github.mysterix5.vover.model.user_details.HistoryEntry;
import com.github.sardine.Sardine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoverIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Sardine sardine;

    @Test
    public void integrationTest() throws IOException {
        // fail and succeed with registering
        ResponseEntity<Void> userCreationResponse = restTemplate.postForEntity("/api/auth/register", new UserRegisterDTO("", "asdfASDF3%", "asdfASDF3%"), Void.class);
        assertThat(userCreationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        userCreationResponse = restTemplate.postForEntity("/api/auth/register", new UserRegisterDTO("user1", "asdf", "asdf"), Void.class);
        assertThat(userCreationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        userCreationResponse = restTemplate.postForEntity("/api/auth/register", new UserRegisterDTO("user1", "asdfASDF3%", "asdfASDF3%"), Void.class);
        assertThat(userCreationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        userCreationResponse = restTemplate.postForEntity("/api/auth/register", new UserRegisterDTO("user2", "asdfASDF3!", "asdfASDF3!"), Void.class);
        assertThat(userCreationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // login user1
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", new UserAuthenticationDTO("user1", "asdfASDF3%"), LoginResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token1 = loginResponse.getBody().getToken();

        // login user2
        loginResponse = restTemplate.postForEntity("/api/auth/login", new UserAuthenticationDTO("user2", "asdfASDF3!"), LoginResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token2 = loginResponse.getBody().getToken();

        // simulate record public
        File publicFile = new File("src/test/resources/cloud_storage/public.mp3");
        Resource resource = new FileSystemResource(publicFile);
        log.info("resource: {}", resource);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("audio", resource);
        multiValueMap.add("word", "public");
        multiValueMap.add("tag", "normal");
        multiValueMap.add("accessibility", "PUBLIC");

        HttpHeaders headers = createHeaders(token1);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<Void> recordAddResponse = restTemplate.postForEntity("/api/record", requestEntity, Void.class);
        log.info("record response: {}", recordAddResponse);
        assertThat(recordAddResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // simulate record friends
        File friendsFile = new File("src/test/resources/cloud_storage/friends.mp3");
        resource = new FileSystemResource(friendsFile);
        log.info("resource: {}", resource);

        multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("audio", resource);
        multiValueMap.add("word", "friends");
        multiValueMap.add("tag", "normal");
        multiValueMap.add("accessibility", "FRIENDS");

        headers = createHeaders(token1);

        requestEntity = new HttpEntity<>(multiValueMap, headers);

        recordAddResponse = restTemplate.postForEntity("/api/record", requestEntity, Void.class);
        log.info("record response: {}", recordAddResponse);
        assertThat(recordAddResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // get records for user and then get audio with the id
        ResponseEntity<RecordPage> recordPageResponse = restTemplate.exchange("/api/record/0/6?searchTerm=", HttpMethod.GET, new HttpEntity<>(headers), RecordPage.class);
        assertThat(recordPageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Mockito.when(sardine.get(Mockito.any(String.class)))
                .thenReturn(new FileInputStream(publicFile))
                .thenReturn(new FileInputStream(publicFile))
                .thenReturn(new FileInputStream(friendsFile))
                .thenReturn(new FileInputStream(publicFile))
                .thenReturn(new FileInputStream(friendsFile));

        ResponseEntity<byte[]> audioSingleGetResponse = restTemplate.exchange("/api/record/audio/" + recordPageResponse.getBody().getRecords().get(0).getId(), HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        log.info("record response: {}", audioSingleGetResponse);
        assertThat(audioSingleGetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // submit text to backend from both users, user2 should only access the public word
        PrimarySubmitDTO primarySubmitDTO = PrimarySubmitDTO.builder().text("public friends").scope(new ArrayList<>()).build();
        ResponseEntity<PrimaryResponseDTO> primarySubmitResponse = restTemplate.postForEntity("/api/primary/textsubmit", new HttpEntity<>(primarySubmitDTO, createHeaders(token1)), PrimaryResponseDTO.class);
        assertThat(primarySubmitResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(primarySubmitResponse.getBody().getTextWords().stream().filter(wordResponseDTO -> wordResponseDTO.getAvailability().equals(WordAvailability.AVAILABLE)).count()).isEqualTo(2);

        primarySubmitDTO = PrimarySubmitDTO.builder().text("public friends").scope(new ArrayList<>()).build();
        primarySubmitResponse = restTemplate.postForEntity("/api/primary/textsubmit", new HttpEntity<>(primarySubmitDTO, createHeaders(token2)), PrimaryResponseDTO.class);
        assertThat(primarySubmitResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(primarySubmitResponse.getBody().getTextWords().stream().filter(wordResponseDTO -> wordResponseDTO.getAvailability().equals(WordAvailability.AVAILABLE)).count()).isEqualTo(1);

        // request the merged audio and check history
        ResponseEntity<Void> mergedAudioResponse = restTemplate.postForEntity("/api/primary/getaudio", new HttpEntity<>(recordPageResponse.getBody().getRecords().stream().map(RecordManagementDTO::getId).toList(), createHeaders(token1)), Void.class);
        assertThat(mergedAudioResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<HistoryEntry[]> historyResponse = restTemplate.exchange("/api/userdetails/history", HttpMethod.GET, new HttpEntity<>(createHeaders(token1)), HistoryEntry[].class);
        assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("history: {}", historyResponse.getBody());
        assertThat(historyResponse.getBody()[0].getText()).isEqualTo("public friends");
        assertThat(historyResponse.getBody()[0].getChoices()).containsExactlyInAnyOrderElementsOf(recordPageResponse.getBody().getRecords().stream().map(RecordManagementDTO::getId).toList());

        // send friend request to user2
        ResponseEntity<Void> friendRequestResponse = restTemplate.postForEntity("/api/userdetails/friendrequest", new HttpEntity<>("user2", createHeaders(token1)), Void.class);
        assertThat(friendRequestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        friendRequestResponse = restTemplate.exchange("/api/userdetails/acceptfriend", HttpMethod.PUT, new HttpEntity<>("user1", createHeaders(token2)), Void.class);
        assertThat(friendRequestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // user2 can now the word 'friends' from user1 because they are friends
        primarySubmitDTO = PrimarySubmitDTO.builder().text("public friends").scope(new ArrayList<>()).build();
        primarySubmitResponse = restTemplate.postForEntity("/api/primary/textsubmit", new HttpEntity<>(primarySubmitDTO, createHeaders(token2)), PrimaryResponseDTO.class);
        assertThat(primarySubmitResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(primarySubmitResponse.getBody().getTextWords().stream().filter(wordResponseDTO -> wordResponseDTO.getAvailability().equals(WordAvailability.AVAILABLE)).count())
                .isEqualTo(2);

        // user1 changes the accessibility of 'friends' recording to PRIVATE and then user2 has again no access to it
        RecordManagementDTO recordManagementDTO = new RecordManagementDTO(recordPageResponse.getBody().getRecords().stream().filter(r -> r.getWord().equals("friends")).map(RecordManagementDTO::getId).findFirst().orElseThrow(), "friends", "normal", Accessibility.PRIVATE);
        var recordChangeResponse = restTemplate.exchange("/api/record", HttpMethod.PUT, new HttpEntity<>(recordManagementDTO, createHeaders(token1)), Void.class);
        assertThat(recordChangeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        primarySubmitResponse = restTemplate.postForEntity("/api/primary/textsubmit", new HttpEntity<>(primarySubmitDTO, createHeaders(token2)), PrimaryResponseDTO.class);
        assertThat(primarySubmitResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(primarySubmitResponse.getBody().getTextWords().stream().filter(wordResponseDTO -> wordResponseDTO.getAvailability().equals(WordAvailability.AVAILABLE)).count())
                .isEqualTo(1);

    }

    private HttpHeaders createHeaders(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        return headers;
    }
}
