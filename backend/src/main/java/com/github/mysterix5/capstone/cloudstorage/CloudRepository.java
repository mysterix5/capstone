package com.github.mysterix5.capstone.cloudstorage;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.io.InputStream;

@Repository
@Slf4j
public class CloudRepository {
    private final String username;
    private final String password;
    private final String baseUrl;

    public CloudRepository(@Value("${app.webdav.username}") String username,
                           @Value("${app.webdav.password}") String password,
                           @Value("${app.webdav.baseurl}") String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    public byte[] find(String filePath) throws IOException {
        String url = baseUrl + filePath;
        Sardine sardine = SardineFactory.begin(username, password);
        InputStream is = sardine.get(url);

        return is.readAllBytes();
    }

    public void save(String filePath, byte[] byteArray) throws IOException {
        String url = baseUrl + filePath;
        Sardine sardine = SardineFactory.begin(username, password);

        sardine.put(url, byteArray);
    }

}
