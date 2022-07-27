import {Box, Button, Grid, Link, Slider} from "@mui/material";
import {Download, Pause, PlayArrow, Stop} from "@mui/icons-material";
import {useRef, useState} from "react";

interface CustomAudioPlayerProps {
    audiofile: string
}

export default function CustomAudioPlayer(props: CustomAudioPlayerProps) {

    const [currentTime, setCurrentTime] = useState(0.0);
    const [duration, setDuration] = useState(1.0)

    let audioRef = useRef<HTMLAudioElement>(null);

    const onLoadedMetadata = () => {
        setDuration(audioRef.current!.duration);
    };

    function playAudio() {
        audioRef.current!.play();
    }

    function pauseAudio() {
        audioRef.current!.pause();
    }

    function stopAudio() {
        audioRef.current!.pause();
        audioRef.current!.currentTime = 0;
        setCurrentTime(0);
    }

    function onPlaying() {
        setCurrentTime(audioRef.current!.currentTime);
    }

    return (
        <Box border={2} borderRadius={5} m={2}>
            <Box mt={2} mb={1} mr={2} ml={2}>
                <audio
                    ref={audioRef}
                    src={props.audiofile!}
                    onLoadedMetadata={onLoadedMetadata}
                    onTimeUpdate={onPlaying}
                    controls={false}
                    title={"vover.mp3"}
                />
                {audioRef && audioRef.current &&
                    <>
                        <Grid container direction={"row"} wrap={"nowrap"}>
                            {audioRef.current!.paused ?
                                <Grid item mr={1}>
                                    <Button onClick={playAudio}
                                            variant="contained"
                                            size="small"
                                    >
                                        <PlayArrow/>
                                    </Button>
                                </Grid>
                                :
                                <Grid item mr={1}>
                                    <Button onClick={pauseAudio}
                                            variant="contained"
                                            size="small"
                                    >
                                        <Pause/>
                                    </Button>
                                </Grid>
                            }
                            <Grid item mr={1}>
                                <Button onClick={stopAudio}
                                        variant="contained"
                                        size="small"
                                >
                                    <Stop/>
                                </Button>
                            </Grid>
                            <Grid item>
                                <Button
                                    variant="contained"
                                    size="small"
                                    component={Link}
                                    href={props.audiofile}
                                    download="vover.mp3"
                                    datatype={"audio/mp3"}
                                >
                                    <Download/>
                                </Button>
                            </Grid>
                        </Grid>
                        <Box mt={1}>
                            <Slider
                                size="medium"
                                value={currentTime}
                                step={0.0001}
                                min={0.0}
                                max={duration}
                                aria-label="Small"
                                valueLabelDisplay="auto"
                                onChangeCommitted={(e, v) => {
                                    audioRef.current!.currentTime = (typeof v === "number") ? v : v[0];
                                    setCurrentTime(audioRef.current!.currentTime);
                                }}
                            />
                        </Box>
                    </>
                }
            </Box>
        </Box>
    )
}