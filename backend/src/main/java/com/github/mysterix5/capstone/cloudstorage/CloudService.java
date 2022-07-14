package com.github.mysterix5.capstone.cloudstorage;

import com.github.mysterix5.capstone.model.AudioResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {
    private final CloudRepository cloudRepository;

    private AudioResponseDTO createAudioResponseDTO(AudioInputStream audioIn) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AudioSystem.write(audioIn, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
        byte[] arrayWithHeader = byteArrayOutputStream.toByteArray();

        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(arrayWithHeader);
        audioResponseDTO.setContentLength((int) (audioIn.getFrameLength()*audioIn.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO loadListFromCloudAndMerge(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {

        List<InputStream> audioStreamList = new ArrayList<>();
        int frameLength = 0;

        ListIterator<String> iterator = cloudFilePaths.listIterator();
        if(!iterator.hasNext()){
            throw new IllegalArgumentException();
        }
        byte[] fileByteArray = cloudRepository.find(iterator.next());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileByteArray);
        AudioFileFormat format = AudioSystem.getAudioFileFormat(byteArrayInputStream);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format.getFormat(), format.getFrameLength());
        audioStreamList.add(audioInputStream);
        frameLength += format.getFrameLength();

        while(iterator.hasNext()){
            fileByteArray = cloudRepository.find(iterator.next());
            byteArrayInputStream = new ByteArrayInputStream(fileByteArray);
            format = AudioSystem.getAudioFileFormat(byteArrayInputStream);
            audioInputStream = new AudioInputStream(byteArrayInputStream, format.getFormat(), format.getFrameLength());
            audioStreamList.add(audioInputStream);
            frameLength += format.getFrameLength();
        }

        SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(audioStreamList));

        AudioInputStream appended =
                new AudioInputStream(
                        sequenceInputStream,
                        format.getFormat(),
                        frameLength);

        return createAudioResponseDTO(appended);
    }

}
