package com.github.mysterix5.vover;

import com.github.mysterix5.vover.model.security.LoginResponse;
import com.github.mysterix5.vover.model.security.UserAuthenticationDTO;
import com.github.mysterix5.vover.model.security.UserRegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoverIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void integrationTest() {
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

        // simulate record
        File publicFile = new File("src/test/resources/cloud_storage/public.mp3");
        Resource resource = new FileSystemResource(publicFile);
        log.info("resource: {}", resource);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("audio", resource);
        multiValueMap.add("word", "public");
        multiValueMap.add("tag", "normal");
        multiValueMap.add("accessibility", "PUBLIC");

        HttpHeaders headers = createHeaders(token1);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiValueMap, headers);

        ResponseEntity<Void> recordResponse = restTemplate.postForEntity("/api/word", requestEntity, Void.class);
        log.info("record response: {}", recordResponse);
        assertThat(recordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpHeaders createHeaders(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        return headers;
    }
}
