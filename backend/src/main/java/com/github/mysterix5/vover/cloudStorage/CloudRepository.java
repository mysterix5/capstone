package com.github.mysterix5.vover.cloudStorage;

import com.github.sardine.Sardine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.io.InputStream;

@Repository
@Slf4j
public class CloudRepository {
    private final Sardine sardine;
    private final String baseUrl;

    public CloudRepository(Sardine sardine,
                           @Value("${app.webdav.baseurl}") String baseUrl) {
        this.sardine = sardine;
        this.baseUrl = baseUrl;
    }

    public byte[] find(String filePath) throws IOException {
        String url = baseUrl + filePath;
        InputStream is = sardine.get(url);

        return is.readAllBytes();
    }

    public void save(String filePath, byte[] byteArray) throws IOException {
        String url = baseUrl + filePath;
        sardine.put(url, byteArray);
    }

}
