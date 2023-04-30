import { useEffect, useRef, useState } from 'react'
import WaveSurfer from 'wavesurfer.js'
import { Pause, PlayArrow, Stop } from "@mui/icons-material";
import { Box, Button, Grid, Slider } from '@mui/material';
import { WaveSurferParams } from 'wavesurfer.js/types/params';

interface WaveSurferInstance extends WaveSurfer {
    getBuffer(): any;
}

class WaveSurferExtended extends WaveSurfer {
    static create(params: WaveSurferParams): WaveSurferExtended {
        const instance = new WaveSurferExtended(params);
        return instance.init() as WaveSurferInstance;
    }

    public getBuffer(): any {
        // @ts-ignore: Ignores access to the private property
        return this.backend.buffer;
    }
}

interface WaveformProps {
    audio: string,
    setAudio: (audio: string) => void,
    setAudioBlob: (blob: Blob) => void,
}

const minDistance = 10;

export default function Waveform(props: WaveformProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const waveSurferRef = useRef<WaveSurfer>();

    const [isPlaying, setIsPlaying] = useState(false);
    const [startCut, setStartCut] = useState(0.0);
    const [endCut, setEndCut] = useState(1.0);
    const [duration, setDuration] = useState(1.0);
    const [start, setStart] = useState(0.0);
    const [end, setEnd] = useState(100.0);

    const handleSliderChange = (
        event: Event,
        newValue: number | number[],
        activeThumb: number,
    ) => {
        if (!Array.isArray(newValue)) {
            return;
        }

        var localStart = newValue[0];
        var localEnd = newValue[1];

        if (activeThumb === 0) {
            localStart = Math.min(localStart, end - minDistance);
            localEnd = end;
        } else {
            localStart = start;
            localEnd = Math.max(localEnd, start + minDistance)
        }

        setStart(localStart);
        setEnd(localEnd);
        setStartCut(localStart / 100.0 * duration)
        setEndCut(localEnd / 100.0 * duration)

        stop();
    };


    const stop = () => {
        if (waveSurferRef.current) {
            waveSurferRef.current.stop();
            waveSurferRef.current.setCurrentTime(startCut);
            waveSurferRef.current.setPlayEnd(endCut);
            setIsPlaying(false);
        }
    }

    const play = () => {
        if (waveSurferRef.current) {
            const start1 = Math.max(startCut, waveSurferRef.current.getCurrentTime());
            waveSurferRef.current.play(start1, endCut);
            setIsPlaying(true);
        }
    }

    const pause = () => {
        if (waveSurferRef.current) {
            waveSurferRef.current.pause();
            setIsPlaying(false);
        }
    }

    useEffect(() => {
        if (containerRef && containerRef.current) {
            waveSurferRef.current = WaveSurferExtended.create({
                container: containerRef.current,
                responsive: true,
                cursorWidth: 1,
                barWidth: 1,
                barHeight: 5,
            })
            waveSurferRef.current.load(props.audio)
            waveSurferRef.current.on('ready', () => {
                if (waveSurferRef.current) {
                    setEndCut(waveSurferRef.current.getDuration());
                    setDuration(waveSurferRef.current.getDuration());
                }
            })
            waveSurferRef.current.on('finish', () => { stop() })
            waveSurferRef.current.on('error', (e) => {
                console.log("error");
                console.log(e);
            })

            if (waveSurferRef && waveSurferRef.current) {
                return () => waveSurferRef.current?.destroy();
            }
        }
        // eslint-disable-next-line
    }, [props.audio, waveSurferRef, containerRef])

    async function bufferToBlob(audioBuffer: AudioBuffer): Promise<Blob> {
        return new Promise(async (resolve, reject) => {
            try {
                const audioContext = new AudioContext();
                const bufferSource = audioContext.createBufferSource();
                bufferSource.buffer = audioBuffer;

                const mediaStreamDestination = audioContext.createMediaStreamDestination();
                bufferSource.connect(mediaStreamDestination);
                bufferSource.start();

                const options = {
                    mimeType: "audio/webm",
                    audioBitsPerSecond: 128000,
                };

                const mediaRecorder = new MediaRecorder(mediaStreamDestination.stream, options);
                const chunks: BlobPart[] = [];

                mediaRecorder.ondataavailable = (event) => {
                    if (event.data.size > 0) {
                        chunks.push(event.data);
                    }
                };

                mediaRecorder.onstop = () => {
                    const blob = new Blob(chunks, { type: 'audio/webm' });
                    resolve(blob);
                };

                mediaRecorder.onerror = (error) => {
                    reject(error);
                };

                mediaRecorder.start();

                bufferSource.onended = () => {
                    mediaRecorder.stop();
                    audioContext.close();
                };
            } catch (error) {
                reject(error);
            }
        });
    }

    function copyBuffer(fromBuffer: AudioBuffer, fromStart: number, fromEnd: number) {
        var sampleRate = fromBuffer.sampleRate
        var frameCount = (fromEnd - fromStart) * sampleRate
        var newBuffer = new AudioContext().createBuffer(1, frameCount, sampleRate)
        var fromChanData = fromBuffer.getChannelData(0)
        var toChanData = new Float32Array(frameCount);
        for (var j = 0, f = Math.round(fromStart * sampleRate), t = 0; j < frameCount; j++, f++, t++) {
            toChanData[t] = fromChanData[f]
        }
        newBuffer.copyToChannel(toChanData, 0);
        return newBuffer;
    }

    async function cutAudio() {
        var originalBuffer = (waveSurferRef.current as WaveSurferExtended).getBuffer();

        var newBuffer = copyBuffer(originalBuffer, startCut, endCut);

        setStart(0.0);
        setEnd(100.0);
        setIsPlaying(false);
        setStartCut(0.0);
        setEndCut(1.0);
        waveSurferRef.current?.stop();

        waveSurferRef.current?.loadDecodedBuffer(newBuffer);

        bufferToBlob(newBuffer).then((blob: Blob) => {
            props.setAudio(URL.createObjectURL(blob));
            props.setAudioBlob(blob);
        })

    }

    return (
        <Box border={2} borderRadius={5} mt={2} alignContent={"center"} justifyContent={"center"}>
            <Box mt={1} mb={1} mr={2} ml={2}>
                <Grid container direction={"row"} wrap={"nowrap"}>
                    {isPlaying ?
                        <Grid item>
                            <Button onClick={pause}
                                variant="contained"
                                size="small"
                                sx={{ minWidth: "45px" }}
                            >
                                <Pause />
                            </Button>
                        </Grid>
                        :
                        <Grid item>
                            <Button onClick={play}
                                variant="contained"
                                size="small"
                                sx={{ minWidth: "45px" }}
                            >
                                <PlayArrow />
                            </Button>
                        </Grid>
                    }
                    <Grid item ml={1}>
                        <Button onClick={stop}
                            variant="contained"
                            size="small"
                            sx={{ minWidth: "45px" }}
                        >
                            <Stop />
                        </Button>
                    </Grid>
                </Grid>
                <div id="waveform" ref={containerRef} />
                <Slider
                    getAriaLabel={() => 'Minimum distance'}
                    value={[start, end]}
                    onChange={handleSliderChange}
                    valueLabelDisplay="auto"
                    disableSwap
                />
            </Box>
            <Box mb={2} ml={1}>
                <Button onClick={cutAudio}
                    variant="contained"
                    size="small"
                    sx={{ minWidth: "45px" }}
                >
                    cut audio
                </Button>
            </Box>
        </Box>
    )
}
