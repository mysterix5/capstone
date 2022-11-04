import {Box, Button, Grid, Link, Slider, styled, Typography} from "@mui/material";
import {Download, Pause, PlayArrow, Stop} from "@mui/icons-material";
import {useRef, useState} from "react";

function computeTime(progress: number, duration: number){
    return progress * duration / 100.0;
}

interface CustomAudioPlayerProps {
    audiofile: string,
    download: boolean,
    slider: boolean,
    autoPlay: boolean,
    audioRange: number[]
}

export default function CustomAudioPlayer(props: CustomAudioPlayerProps) {

    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(1.0)

    let audioRef = useRef<HTMLAudioElement>(null);

    const onLoadedMetadata = () => {
        console.log("on loaded metadata " + audioRef.current!.currentTime + "   " + props.audioRange)
        console.log("duration: " + audioRef.current!.duration)
        // audioRef.current!.load();
        setDuration(audioRef.current!.duration);
        audioRef.current!.currentTime = computeTime(props.audioRange[0], audioRef.current!.duration);
        if (props.autoPlay) {
            playAudio();
        }
        console.log("on loaded metadata after " + audioRef.current!.currentTime + "   " + props.audioRange)
    };

    function onEnded(){
        audioRef.current!.pause();
        audioRef.current!.currentTime = computeTime(props.audioRange[0], audioRef.current!.duration);
    }

    function playAudio() {
        console.log("play audio: " + audioRef.current!.currentTime + " duration: " + audioRef.current!.duration)
        audioRef.current!.play();
    }

    function pauseAudio() {
        audioRef.current!.pause();
    }

    function stopAudio() {
        audioRef.current!.pause();
        audioRef.current!.currentTime = computeTime(props.audioRange[0], audioRef.current!.duration);
        setCurrentTime(audioRef.current!.currentTime);
    }

    function onPlaying() {
        console.log("on playing: " + audioRef.current!.currentTime + " duration: " + audioRef.current!.duration)
        // setCurrentTime(audioRef.current!.currentTime);
    }

    const TinyText = styled(Typography)({
        fontSize: '0.75rem',
        opacity: 0.38,
        fontWeight: 500,
        letterSpacing: 0.2,
    });

    return (
        <Box border={2} borderRadius={5} m={1}>
            <audio
                ref={audioRef}
                src={props.audiofile!}
                onLoadedMetadata={onLoadedMetadata}
                onTimeUpdate={onPlaying}
                controls={false}
                preload={"auto"}
                title={"vover.mp3"}
                onEnded={onEnded}
            >
                <source src={props.audiofile!} type="audio/mpeg"/>
            </audio>
            <Box mt={1} mb={1} mr={2} ml={2}>
                <Box>
                    {audioRef && audioRef.current &&
                        <Grid container direction={"row"} wrap={"nowrap"}>
                            {audioRef.current!.paused ?
                                <Grid item>
                                    <Button onClick={playAudio}
                                            variant="contained"
                                            size="small"
                                            sx={{minWidth: "45px"}}
                                    >
                                        <PlayArrow/>
                                    </Button>
                                </Grid>
                                :
                                <Grid item>
                                    <Button onClick={pauseAudio}
                                            variant="contained"
                                            size="small"
                                            sx={{minWidth: "45px"}}
                                    >
                                        <Pause/>
                                    </Button>
                                </Grid>
                            }
                            <Grid item ml={1}>
                                <Button onClick={stopAudio}
                                        variant="contained"
                                        size="small"
                                        sx={{minWidth: "45px"}}
                                >
                                    <Stop/>
                                </Button>
                            </Grid>
                            {props.download &&
                                <Grid item ml={1}>
                                    <Button
                                        variant="contained"
                                        size="small"
                                        component={Link}
                                        href={props.audiofile}
                                        download="vover.mp3"
                                        datatype={"audio/mp3"}
                                        sx={{minWidth: "45px"}}
                                    >
                                        <Download/>
                                    </Button>
                                </Grid>
                            }
                        </Grid>
                    }
                </Box>
            </Box>
            {props.slider &&
                <Box mt={1} mr={2} ml={2} mb={0.5}>
                    <Slider
                        size="small"
                        value={currentTime}
                        step={0.0001}
                        min={props.audioRange[0]}
                        max={duration}
                        aria-label="Small"
                        valueLabelDisplay="off"
                        onChangeCommitted={(e, v) => {
                            audioRef.current!.pause();
                            audioRef.current!.currentTime = (typeof v === "number") ? v : v[0];
                            setCurrentTime(audioRef.current!.currentTime);
                        }}
                    />
                    <Box
                        sx={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'space-between',
                            mt: -2,
                        }}
                    >
                        <TinyText>{currentTime.toFixed(2)}s</TinyText>
                        <TinyText>{duration.toFixed(2)}s</TinyText>
                    </Box>
                </Box>
            }
        </Box>
    )
}

CustomAudioPlayer.defaultProps = {
    audioRange: [0.0,100.0]
}
