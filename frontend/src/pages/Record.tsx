import AudioAnalyser from "react-audio-analyser"
import {FormEvent, useState} from "react";
import {Box, Button, Grid, TextField} from "@mui/material";
import {apiSaveAudio} from "../services/apiServices";

export default function Record() {
    const [status, setStatus] = useState<string>();
    const [audioSrc, setAudioSrc] = useState<any>();
    const [audioBlob, setAudioBlob] = useState<any>();

    const [word, setWord] = useState("");
    const [creator, setCreator] = useState("Lukas");
    const [tag, setTag] = useState("normal");

    function saveAudio(event: FormEvent) {
        event.preventDefault();
        console.log("save audio");

        apiSaveAudio(word, creator, tag, audioBlob)
            .then(() => {
                setAudioSrc(null);
                setAudioBlob(null);
                setWord("");
            });
    }

    function controlAudio(st: string) {
        setStatus(st)
    }

    const audioProps = {
        audioType: "audio/mp3",
        // audioOptions: {sampleRate: 30000}, // 设置输出音频采样率
        status,
        audioSrc,
        timeslice: 1000, // timeslice（https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder/start#Parameters）
        startCallback: (e: any) => {
            console.log("succ start", e)
        },
        pauseCallback: (e: any) => {
            console.log("succ pause", e)
        },
        stopCallback: (e: any) => {
            setAudioSrc(window.URL.createObjectURL(e));
            setAudioBlob(e);
            console.log("succ stop", e);
        },
        onRecordCallback: (e: any) => {
            console.log("recording", e)
        },
        errorCallback: (err: any) => {
            console.log("error", err)
        }
    }
    return (
        <Grid container alignItems={"center"} alignContent={"center"} flexDirection={"column"}>
            <Grid item xs={4}>
                <AudioAnalyser {...audioProps}/>
            </Grid>
            <Grid item>
                <Grid container>
                    <Grid item>
                        {status !== "recording" &&
                            <Button onClick={() => controlAudio("recording")}>
                                start
                            </Button>
                        }
                    </Grid>
                    <Grid item>
                        {status === "recording" &&
                            <Button onClick={() => controlAudio("paused")}>
                                pause
                            </Button>
                        }
                    </Grid>
                    <Grid item>
                        <Button onClick={() => controlAudio("inactive")}>
                            stop
                        </Button>
                    </Grid>
                </Grid>
            </Grid>
            <div>
                {
                    audioSrc &&
                    <Box component={"form"} onSubmit={saveAudio} sx={{mt: 7}}>
                        <Grid item>
                            <TextField
                                label="Word"
                                variant="outlined"
                                onChange={event => setWord(event.target.value)}
                            />
                        </Grid>
                        <Grid item>
                            <TextField
                                label="Creator"
                                variant="outlined"
                                value={creator}
                                onChange={event => setCreator(event.target.value)}
                            />
                        </Grid>
                        <Grid item>
                            <TextField
                                label="Tag"
                                variant="outlined"
                                value={tag}
                                onChange={event => setTag(event.target.value)}
                            />
                        </Grid>

                        <Grid item>
                            <Button
                                type="submit"
                                variant="contained"
                            >
                                save audio to db
                            </Button>
                        </Grid>
                    </Box>
                }
            </div>
        </Grid>
    )

}