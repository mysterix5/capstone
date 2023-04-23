import { useState, FormEvent } from "react";
import { Button, Grid, Typography } from "@mui/material";

import { useAuth } from "../../usermanagement/AuthProvider";
import { apiSaveAudio } from "../../services/apiServices";
import Waveform from "./Waveform";
import RecordInfo from "./RecordInfo";
import Record from "./Record";

export default function RecordPage() {
    const [audio, setAudio] = useState<string>("");
    const [audioBlob, setAudioBlob] = useState<Blob>();

    const [word, setWord] = useState("");
    const [tag, setTag] = useState("normal");
    const [accessibility, setAccessibility] = useState("PUBLIC");

    const { setError, defaultApiResponseChecks } = useAuth();

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

    return (
        <>
            <Typography variant={"h4"} align={"center"} mb={2}>
                Record a new word
            </Typography>
            <Grid
                container
                alignItems={"center"}
                alignContent={"center"}
                flexDirection={"column"}
            >
                <Grid mt={2}>
                    <Record setAudio={setAudio} setAudioBlob={setAudioBlob} />
                </Grid>
                {audio &&
                    <Grid mt={2}>
                        <div className="audio-container">
                            <Waveform audio={audio} />
                        </div>
                    </Grid>
                }
                {audioBlob &&
                    <Grid component={"form"} onSubmit={saveAudio} sx={{ mt: 2 }}>
                        <RecordInfo word={word} setWord={setWord} tag={tag} setTag={setTag} accessibility={accessibility} setAccessibility={setAccessibility} />
                        <Grid item m={0.5}>
                            <Button type="submit" variant="contained">
                                save audio to db
                            </Button>
                        </Grid>
                    </Grid>
                }
            </Grid >
        </>
    );
};
