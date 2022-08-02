import {Recorder} from "vmsg";
import {Box, Button, Grid, TextField, ToggleButton, ToggleButtonGroup, Typography} from "@mui/material";
import {FormEvent, MouseEvent, useState} from "react";
import CustomAudioPlayer from "../primary/CustomAudioPlayer";

const recorder = new Recorder({
    wasmURL: "https://unpkg.com/vmsg@0.4.0/vmsg.wasm"
});

interface RecordProps {
    word: string,
    setWord: (w: string) => void,
    tag: string,
    setTag: (t: string) => void,
    accessibility: string,
    setAccessibility: (acc: string) => void,
    audioBlob: Blob | undefined,
    setAudioBlob: (blob: Blob | undefined) => void,
    saveAudio: (event: FormEvent) => void
}

export default function Record(props: RecordProps) {
    const [isLoading, setIsLoading] = useState(false);
    const [isRecording, setIsRecording] = useState(false);

    const [audioLink, setAudioLink] = useState("");

    const record = async () => {
        setIsLoading(true);

        if (isRecording) {
            const blob = await recorder.stopRecording();
            setIsLoading(false);
            setIsRecording(false);
            props.setAudioBlob(blob);
            setAudioLink(URL.createObjectURL(blob));
        } else {
            try {
                await recorder.initAudio();
                await recorder.initWorker();
                recorder.startRecording();
                setIsLoading(false);
                setIsRecording(true);
            } catch (e) {
                console.error(e);
                setIsLoading(false);
            }
        }
    };

    const handleAccessibility = (
        event: MouseEvent<HTMLElement>,
        newAccessibility: string,
    ) => {
        props.setAccessibility(newAccessibility);
    };

    return (
        <>
            <Typography variant={"h6"} align={"center"} mb={2}>
                Record your missing words
            </Typography>
            <Grid container alignContent={"center"} flexDirection={"column"}>
                <div>
                    {
                        <Box component={"form"} onSubmit={props.saveAudio} sx={{mt: 1}}>
                            <Grid item m={0.5} display={"flex"} justifyContent={"center"}>
                                <TextField
                                    label="Word"
                                    variant="outlined"
                                    value={props.word}
                                    placeholder={"your word"}
                                    onChange={event => props.setWord(event.target.value)}
                                />
                            </Grid>
                            <Grid item m={0.5} display={"flex"} justifyContent={"center"}>
                                    <TextField
                                        label="Tag"
                                        variant="outlined"
                                        value={props.tag}
                                        placeholder={props.tag}
                                        onChange={event => props.setTag(event.target.value)}
                                    />
                            </Grid>
                            <Grid item m={0.5} display={"flex"} justifyContent={"center"}>
                                <ToggleButtonGroup
                                    size={"small"}
                                    value={props.accessibility}
                                    exclusive
                                    onChange={handleAccessibility}
                                >
                                    <ToggleButton value={"PUBLIC"}>
                                        public
                                    </ToggleButton>
                                    <ToggleButton value={"FRIENDS"}>
                                        friends
                                    </ToggleButton>
                                    <ToggleButton value={"PRIVATE"}>
                                        private
                                    </ToggleButton>
                                </ToggleButtonGroup>
                            </Grid>

                            <Grid item display={"flex"} justifyContent={"center"} mt={3}>
                                <Button variant="contained" disabled={isLoading} onClick={record}>
                                    {isRecording ? "Stop" : "Record"}
                                </Button>
                            </Grid>

                            {props.audioBlob &&
                                <Grid item mt={1} display={"flex"} justifyContent={"center"}>
                                    <CustomAudioPlayer
                                        audiofile={audioLink} slider={true} download={false} autoPlay={false}/>
                                </Grid>
                            }
                            {props.audioBlob &&
                                <Grid item m={1} display={"flex"} justifyContent={"center"}>
                                    <Button
                                        type="submit"
                                        variant="contained"
                                    >
                                        save audio to db
                                    </Button>
                                </Grid>
                            }
                        </Box>
                    }
                </div>
            </Grid>
        </>
    )
}