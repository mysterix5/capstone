package com.github.mysterix5.capstone.cloudstorage;

import com.github.mysterix5.capstone.model.AudioResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {
    private final CloudRepository cloudRepository;

    public AudioResponseDTO loadOneFileWorking(String cloudFilePath) throws UnsupportedAudioFileException, IOException {
        byte[] fileByteArray = cloudRepository.find(cloudFilePath);
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileByteArray);
        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream1));

        audioInputStream1.reset();
        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(audioInputStream1.readAllBytes());
        audioResponseDTO.setContentLength((int) (audioInputStream1.getFrameLength()*audioInputStream1.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO workingFileLoad(String path) throws UnsupportedAudioFileException, IOException {

        File file = new File(path);

        AudioInputStream audioOutput = AudioSystem.getAudioInputStream(file);

        log.info("format: {}", audioOutput);

        return createAudioResponseDTO(audioOutput);
    }

    public AudioResponseDTO createAudioResponseDTO(AudioInputStream audioIn) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AudioSystem.write(audioIn, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
        byte[] arrayWithHeader = byteArrayOutputStream.toByteArray();


        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(arrayWithHeader);
        audioResponseDTO.setContentLength((int) (audioIn.getFrameLength()*audioIn.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO loadTwoFromCloudAndMerge(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {
        ListIterator<String> iterator = cloudFilePaths.listIterator();
        byte[] fileByteArray1 = cloudRepository.find(iterator.next());
        byte[] fileByteArray2 = cloudRepository.find(iterator.next());

        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileByteArray1);
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileByteArray2);
        AudioFileFormat format1 = AudioSystem.getAudioFileFormat(inputStream1);
        AudioFileFormat format2 = AudioSystem.getAudioFileFormat(inputStream2);

        AudioInputStream audioInputStream1 = new AudioInputStream(inputStream1, format1.getFormat(), format1.getFrameLength());
        AudioInputStream audioInputStream2 = new AudioInputStream(inputStream2, format2.getFormat(), format2.getFrameLength());

        AudioInputStream appendedFiles =
                new AudioInputStream(
                        new SequenceInputStream(audioInputStream1, audioInputStream2),
                        audioInputStream1.getFormat(),
                        audioInputStream1.getFrameLength() + audioInputStream2.getFrameLength());

        return createAudioResponseDTO(appendedFiles);
    }
    public AudioResponseDTO loadListFromCloudAndMerge(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {

        List<InputStream> audioStreamList = new ArrayList<>();
        int frameLength = 0;
        AudioFileFormat format = null;
        for(String path: cloudFilePaths){
            byte[] fileByteArray = cloudRepository.find(path);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileByteArray);
            format = AudioSystem.getAudioFileFormat(byteArrayInputStream);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format.getFormat(), format.getFrameLength());
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
    public AudioResponseDTO list4(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {
        ListIterator<String> iterator = cloudFilePaths.listIterator();
        byte[] fileByteArray1 = cloudRepository.find(iterator.next());
        byte[] fileByteArray2 = cloudRepository.find(iterator.next());

        byte[] c = new byte[fileByteArray1.length + fileByteArray2.length];
        System.arraycopy(fileByteArray1, 0, c, 0, fileByteArray1.length);
        System.arraycopy(fileByteArray2, 0, c, fileByteArray1.length, fileByteArray2.length);

        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(c);

        AudioFileFormat format1 = AudioSystem.getAudioFileFormat(inputStream1);

        AudioInputStream audioInputStream1 = new AudioInputStream(inputStream1, format1.getFormat(), format1.getFrameLength());

//        audioInputStream1.reset();
        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(audioInputStream1.readAllBytes());
        audioResponseDTO.setContentLength((int) (audioInputStream1.getFrameLength()*audioInputStream1.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO list3(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {
        ListIterator<String> iterator = cloudFilePaths.listIterator();
        byte[] fileByteArray1 = cloudRepository.find(iterator.next());
        byte[] fileByteArray2 = cloudRepository.find(iterator.next());

        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileByteArray1);
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileByteArray2);

        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStream1, inputStream2);
        AudioFileFormat format1 = AudioSystem.getAudioFileFormat(inputStream1);
        AudioFileFormat format2 = AudioSystem.getAudioFileFormat(inputStream2);

        AudioInputStream audioInputStream1 = new AudioInputStream(sequenceInputStream, format1.getFormat(), format1.getFrameLength()+format2.getFrameLength());

//        audioInputStream1.reset();
        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(audioInputStream1.readAllBytes());
        audioResponseDTO.setContentLength((int) (audioInputStream1.getFrameLength()*audioInputStream1.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO list2(List<String> cloudFilePaths) throws UnsupportedAudioFileException, IOException {
        ListIterator<String> iterator = cloudFilePaths.listIterator();
        byte[] fileByteArray1 = cloudRepository.find(iterator.next());
        byte[] fileByteArray2 = cloudRepository.find(iterator.next());

        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileByteArray1);
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileByteArray2);

        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStream1, inputStream2);

        ByteArrayInputStream inputStream1x = new ByteArrayInputStream(fileByteArray1);
        ByteArrayInputStream inputStream2x = new ByteArrayInputStream(fileByteArray2);

        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream1x));
        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream2x));

        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(sequenceInputStream.readAllBytes());
        audioResponseDTO.setContentLength((int) (audioInputStream1.getFrameLength()*audioInputStream1.getFormat().getFrameSize()) + (int) (audioInputStream2.getFrameLength()*audioInputStream2.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }
    public AudioResponseDTO list1(List<String> filePaths) throws UnsupportedAudioFileException, IOException {
        ListIterator<String> iterator = filePaths.listIterator();
        byte[] fileByteArray = cloudRepository.find(iterator.next());
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileByteArray);
        byte[] fileByteArray2 = cloudRepository.find(iterator.next());
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileByteArray2);

        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream1));
        audioInputStream1.reset();
        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream2));
        audioInputStream2.reset();

        SequenceInputStream sequenceInputStream = new SequenceInputStream(audioInputStream1, audioInputStream2);
        sequenceInputStream.reset();
        ByteArrayInputStream inputStream3 = new ByteArrayInputStream(sequenceInputStream.readAllBytes());
        inputStream3.reset();
        AudioInputStream audioInputStream3 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream3));

        audioInputStream3.reset();
        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
        audioResponseDTO.setData(audioInputStream3.readAllBytes());
        audioResponseDTO.setContentLength((int) (audioInputStream3.getFrameLength()*audioInputStream3.getFormat().getFrameSize()));
        audioResponseDTO.setContentType("audio/x-wav");

//        AudioInputStream audioInputStream = new AudioInputStream(sequenceInputStream, audioInputStream1.getFormat(), audioInputStream1.getFrameLength()+audioInputStream2.getFrameLength());

//        BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
//        audioInputStream = new AudioInputStream(bufferedInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());
//        ByteArrayInputStream input = new ByteArrayInputStream(sequenceInputStream.readAllBytes());
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(input));

//        int contentLength = (int) (audioInputStream1.getFrameLength()*audioInputStream1.getFormat().getFrameSize());
//        SequenceInputStream sequenceInputStream = null;
//        while(iterator.hasNext()){
//            byte[] fileByteArray2 = cloudRepository.find(iterator.next());
//            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileByteArray2);
//            AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream2));
//            contentLength += (int) (audioInputStream2.getFrameLength()*audioInputStream2.getFormat().getFrameSize());
//            sequenceInputStream = new SequenceInputStream(audioInputStream1, audioInputStream2);
//        }
//        byte[] fileByteArray = cloudRepository.find(path);
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByteArray);
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, AudioSystem.getAudioInputStream(inputStream));

//        audioInputStream.mark(0);
//        audioInputStream.reset();
//        AudioResponseDTO audioResponseDTO = new AudioResponseDTO();
//        audioResponseDTO.setData(audioInputStream.readAllBytes());
//        audioResponseDTO.setContentLength((int) (audioInputStream.getFrameLength()*audioInputStream.getFormat().getFrameSize()));
//        audioResponseDTO.setContentType("audio/x-wav");

        return audioResponseDTO;
    }

    public byte[] mergeRawAndThenToAudio(){
        return null;
    }

}
