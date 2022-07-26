import {Recorder} from "vmsg";
import {Box, Button, Grid, TextField, ToggleButton, ToggleButtonGroup, Typography} from "@mui/material";
import {FormEvent, MouseEvent, useState} from "react";
import {apiSaveAudio} from "../services/apiServices";
import {useAuth} from "../usermanagement/AuthProvider";

const recorder = new Recorder({
    wasmURL: "https://unpkg.com/vmsg@0.4.0/vmsg.wasm"
});

export default function Record() {
    const [isLoading, setIsLoading] = useState(false);
    const [isRecording, setIsRecording] = useState(false);

    const [audioLink, setAudioLink] = useState("");
    const [audioBlob, setAudioBlob] = useState<Blob>();

    const [word, setWord] = useState("");
    const [tag, setTag] = useState("normal");
    const [accessibility, setAccessibility] = useState("PUBLIC");

    const {getToken} = useAuth();

    const record = async () => {
        setIsLoading(true);

        if (isRecording) {
            const blob = await recorder.stopRecording();
            setIsLoading(false);
            setIsRecording(false);
            setAudioBlob(blob);
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

    function saveAudio(event: FormEvent) {
        event.preventDefault();
        console.log("save audio");

        apiSaveAudio(getToken(), word, tag, accessibility, audioBlob!)
            .then(() => {
                setAudioLink("");
                setAudioBlob(undefined);
                setWord("");
            });
    }

    const handleAccessibility = (
        event: MouseEvent<HTMLElement>,
        newAccessibility: string,
    ) => {
        setAccessibility(newAccessibility);
    };

    return (
        <>
            <Typography variant={"h4"} align={"center"} mb={2}>
                Record new words
            </Typography>
            <Grid container alignItems={"center"} alignContent={"center"} flexDirection={"column"}>
                <Grid item xs={4}>
                    <Button variant="contained" disabled={isLoading} onClick={record}>
                        {isRecording ? "Stop" : "Record"}
                    </Button>
                </Grid>
                <Box mt={2}>
                    {audioLink &&
                        <audio src={audioLink} autoPlay={false} controls={true} title="vover.mp3"/>
                    }
                </Box>
                <div>
                    {
                        audioBlob &&
                        <Box component={"form"} onSubmit={saveAudio} sx={{mt: 7}}>
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
                                    exclusive
                                    onChange={handleAccessibility}
                                >
                                    <ToggleButton value={"PUBLIC"}>
                                        public
                                    </ToggleButton>
                                    <ToggleButton value={"FRIENDS"}>
                                        friends
                                    </ToggleButton>
                                </ToggleButtonGroup>
                            </Grid>
                            <Grid item m={0.5}>
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
        </>
    )
}