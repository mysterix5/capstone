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

    public byte[] find(String cloudFileName) throws IOException {
        String url = baseUrl + cloudFileName;
        InputStream is = sardine.get(url);
        return is.readAllBytes();
    }

    public void save(String cloudFileName, byte[] byteArray) throws IOException {
        String url = baseUrl + cloudFileName;
        sardine.put(url, byteArray);
    }

    public void delete(String cloudFileName) throws IOException {
        String url = baseUrl + cloudFileName;
        sardine.delete(url);
    }

    public void move(String oldCloudFileName, String newCloudFileName) throws IOException {
        String oldUrl = baseUrl + oldCloudFileName;
        String newUrl = baseUrl + newCloudFileName;

        sardine.move(oldUrl, newUrl);
    }
}
