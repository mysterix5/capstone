package com.github.mysterix5.vover.audio_processing;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioProcessingService {

    public ByteArrayOutputStream processAudio(InputStream audio) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            FFmpeg.atPath()
                    .addInput(PipeInput.pumpFrom(audio))
                    .addArguments("-codec:a", "libmp3lame")
                    .addArguments("-b:a", "128k")
                    .addArguments("-ar", "44100")
                    .addArguments("-ac", "1")
                    .addOutput(
                            PipeOutput.pumpTo(byteArrayOutputStream)
                                    .setFormat("mp3")
                    )
                    .execute();

            return byteArrayOutputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] mergeAudioWithJaffree(List<InputStream> inputStreams) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            for (InputStream inputStream : inputStreams) {
                FFmpeg.atPath()
                        .addInput(PipeInput.pumpFrom(inputStream))
                        .addOutput(
                                PipeOutput.pumpTo(byteArrayOutputStream)
                                        .setFormat("mp3")
                        )
                        .execute();
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
