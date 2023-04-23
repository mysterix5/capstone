import { useState, useRef, FormEvent, MouseEvent, useEffect } from "react";
import { Box, Button, Grid, TextField, ToggleButton, ToggleButtonGroup, Typography } from "@mui/material";

import { useAuth } from "../../usermanagement/AuthProvider";
import { apiSaveAudio } from "../../services/apiServices";
import CustomAudioPlayer from "../primary/CustomAudioPlayer";
import Waveform from "../primary/Waveform";

const mimeType = "audio/webm";
const audioBitsPerSecond = 128000;

export default function Record() {
    const [isRecording, setIsRecording] = useState(false);

    const [permission, setPermission] = useState(false);
    const mediaRecorder = useRef<MediaRecorder | null>(null);
    const [stream, setStream] = useState<MediaStream | null>(null);
    const [audioChunks, setAudioChunks] = useState<BlobPart[]>([]);
    const [audioBlob, setAudioBlob] = useState<Blob>();
    const [audio, setAudio] = useState<string>("");

    const [word, setWord] = useState("");
    const [tag, setTag] = useState("normal");
    const [accessibility, setAccessibility] = useState("PUBLIC");

    const { setError, defaultApiResponseChecks } = useAuth();

    useEffect(() => {
        getMicrophonePermission();
    }, [])


    const getMicrophonePermission = async () => {
        if ("MediaRecorder" in window) {
            try {
                const streamData = await navigator.mediaDevices.getUserMedia({
                    audio: true,
                    video: false,
                });
                setPermission(true);
                setStream(streamData);
            } catch (err: any) {
                alert(err.message);
            }
        } else {
            alert("The MediaRecorder API is not supported in your browser.");
        }
    };

    const startRecording = async () => {
        if (!stream) {
            return;
        }
        setIsRecording(true);
        //create new Media recorder instance using the stream
        const media = new MediaRecorder(stream, {mimeType: mimeType, audioBitsPerSecond: audioBitsPerSecond,});
        //set the MediaRecorder instance to the mediaRecorder ref
        mediaRecorder.current = media;
        //invokes the start method to start the recording process
        mediaRecorder.current.start();
        let localAudioChunks: BlobPart[] = [];
        mediaRecorder.current.ondataavailable = (event) => {
            if (typeof event.data === "undefined") return;
            if (event.data.size === 0) return;
            localAudioChunks.push(event.data);
        };
        setAudioChunks(localAudioChunks);
    };

    const stopRecording = () => {
        if (!mediaRecorder || !mediaRecorder.current) {
            console.log("stop recording failed: mediaRecorder not defined");
            return;
        }
        setIsRecording(false);
        //stops the recording instance
        mediaRecorder.current.stop();
        mediaRecorder.current.onstop = () => {
            //creates a blob file from the audiochunks data
            const audioBlob = new Blob(audioChunks, { type: mimeType });
            //creates a playable URL from the blob file.
            const audioUrl = URL.createObjectURL(audioBlob);
            setAudioBlob(audioBlob);
            setAudio(audioUrl);
            setAudioChunks([]);
        };
    };

    function saveAudio(event: FormEvent) {
        event.preventDefault();
        console.log("save audio");

        apiSaveAudio(word, tag, accessibility, audioBlob!)
            .then(() => {
                setAudio("");
                setAudioBlob(undefined);
                setWord("");
            })
            .catch((err) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    setError(err.response.data);
                }
            });
    }

    const handleAccessibility = (event: MouseEvent<HTMLElement>, newAccessibility: string) => {
        setAccessibility(newAccessibility);
    };


    return (
        <>
            <Typography variant={"h4"} align={"center"} mb={2}>
                Record new words
            </Typography>
            <Grid
                container
                alignItems={"center"}
                alignContent={"center"}
                flexDirection={"column"}
            >
                <Grid item xs={4}>
                    <div className="audio-controls">
                        {!permission ? (
                            <Button variant="contained" onClick={getMicrophonePermission}>
                                Get Microphone
                            </Button>
                        ) : null}
                        {permission && isRecording === false ? (
                            <Button variant="contained" onClick={startRecording}>
                                Start Recording
                            </Button>
                        ) : null}
                        {isRecording === true ? (
                            <Button variant="contained" onClick={stopRecording}>
                                Stop Recording
                            </Button>
                        ) : null}
                    </div>
                </Grid>
                {audio && (
                    <div>
                        <Box mt={2}>
                            <div className="audio-container">
                                <audio src={audio} controls></audio>
                                <CustomAudioPlayer audiofile={audio} slider={true} download={false} autoPlay={false} />
                                <Waveform audio={audio} />
                            </div>
                        </Box>
                        <Box component={"form"} onSubmit={saveAudio} sx={{ mt: 7 }}>
                            <Grid item m={0.5}>
                                <TextField
                                    label="Word"
                                    variant="outlined"
                                    value={word}
                                    placeholder={"your word"}
                                    onChange={event => setWord(event.target.value)}
                                />
                            </Grid>
                            <Grid item m={0.5}>
                                <TextField
                                    label="Tag"
                                    variant="outlined"
                                    value={tag}
                                    placeholder={tag}
                                    onChange={event => setTag(event.target.value)}
                                />
                            </Grid>
                            <Grid item m={0.5}>
                                <ToggleButtonGroup
                                    value={accessibility}
                                    size={"small"}
                                    exclusive
                                    onChange={handleAccessibility}
                                >
                                    <ToggleButton value={"PUBLIC"}>public</ToggleButton>
                                    <ToggleButton value={"FRIENDS"}>friends</ToggleButton>
                                    <ToggleButton value={"PRIVATE"}>private</ToggleButton>
                                </ToggleButtonGroup>
                            </Grid>
                            <Grid item m={0.5}>
                                <Button type="submit" variant="contained">
                                    save audio to db
                                </Button>
                            </Grid>
                        </Box>
                    </div>
                )
                }
            </Grid >
        </>
    );
};
