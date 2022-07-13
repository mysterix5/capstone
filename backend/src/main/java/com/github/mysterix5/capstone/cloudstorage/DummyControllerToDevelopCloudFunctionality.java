package com.github.mysterix5.capstone.cloudstorage;

import com.github.mysterix5.capstone.model.AudioResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cloud")
@Slf4j
public class DummyControllerToDevelopCloudFunctionality {
    private final CloudService cloudService;

    @GetMapping("/one")
    public void loadOneAudio(HttpServletResponse httpResponse) throws UnsupportedAudioFileException, IOException {
        String cloudFilePath = "got_easy.wav";

        AudioResponseDTO audioResponseDTO = cloudService.loadOneFileWorking(cloudFilePath);

        httpResponse.setContentLength(audioResponseDTO.getContentLength());

        httpResponse.setContentType(audioResponseDTO.getContentType());

        httpResponse.getOutputStream().write(audioResponseDTO.getData());
    }

    @GetMapping("/file")
    public void workingFileLoad(HttpServletResponse httpResponse) throws UnsupportedAudioFileException, IOException {


        AudioResponseDTO audioResponseDTO = cloudService.workingFileLoad("asdf.wav");

        httpResponse.setContentLength(audioResponseDTO.getContentLength());

        httpResponse.setContentType(audioResponseDTO.getContentType());

        httpResponse.getOutputStream().write(audioResponseDTO.getData());

        log.info("length: {}", audioResponseDTO.getContentLength());
    }

    @GetMapping("/cloudfix")
    public void loadTwoFromCloudAndMerge(HttpServletResponse httpResponse) throws UnsupportedAudioFileException, IOException {
        List<String> filePaths = List.of("got_easy.wav", "got_easy.wav");

        AudioResponseDTO audioResponseDTO = cloudService.loadTwoFromCloudAndMerge(filePaths);

        httpResponse.setContentLength(audioResponseDTO.getContentLength());

        httpResponse.setContentType(audioResponseDTO.getContentType());

        httpResponse.getOutputStream().write(audioResponseDTO.getData());

        log.info("length: {}", audioResponseDTO.getContentLength());
    }
    @GetMapping("/cloudvar")
    public void loadListFromCloudAndMerge(HttpServletResponse httpResponse) throws UnsupportedAudioFileException, IOException {
        List<String> filePaths = List.of("got_easy.wav", "got_easy.wav", "got_hodor.wav");

        AudioResponseDTO audioResponseDTO = cloudService.loadListFromCloudAndMerge(filePaths);

        httpResponse.setContentLength(audioResponseDTO.getContentLength());

        httpResponse.setContentType(audioResponseDTO.getContentType());

        httpResponse.getOutputStream().write(audioResponseDTO.getData());

        log.info("length: {}", audioResponseDTO.getContentLength());
    }

}
