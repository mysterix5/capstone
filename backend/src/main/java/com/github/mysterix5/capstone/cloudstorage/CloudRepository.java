package com.github.mysterix5.capstone.cloudstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Repository
@Slf4j
public class CloudRepository {

    private final RestTemplate restTemplate;

    private final String username;
    private final String password;
    private final String baseUrl;

    public CloudRepository(RestTemplate restTemplate,
                           @Value("${app.webdav.username}") String username,
                           @Value("${app.webdav.password}") String password,
                           @Value("${app.webdav.baseurl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    public byte[] find(String filePath) throws RuntimeException {
        String url = baseUrl + filePath;

        ResponseEntity<byte[]> result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), byte[].class);

        return result.getBody();
    }

    private HttpHeaders createHeaders() {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }
}
