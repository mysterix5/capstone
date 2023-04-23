import { Box, Button, Grid, Typography } from "@mui/material";
import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { apiSaveAudio } from "../../services/apiServices";
import { useAuth } from "../../usermanagement/AuthProvider";
import Record from "../record/Record";
import RecordInfo from "../record/RecordInfo";
import Waveform from "../record/Waveform";


export default function BatchRecord() {
    const [searchParams] = useSearchParams();
    const [wordArray, setWordArray] = useState<Array<string>>([]);
    const [recordIndex, setRecordIndex] = useState<number>(0);

    const [audio, setAudio] = useState<string>("");
    const [audioBlob, setAudioBlob] = useState<Blob>();

    const [word, setWord] = useState("");
    const [tag, setTag] = useState("normal");
    const [accessibility, setAccessibility] = useState("PUBLIC");


    const { setError, defaultApiResponseChecks } = useAuth();

    const nav = useNavigate();

    useEffect(() => {
        const wordArrayTmp = Array.from(new Set<string>(searchParams.getAll("words")));
        if (wordArrayTmp.length === 0) {
            setError({ message: "the array of words to record was empty", subMessages: [] });
            nav("/?text=" + searchParams.get("text"));
        }
        setWordArray(wordArrayTmp);
        setWord(wordArrayTmp[0]);
    }, [nav, searchParams, setError])


    function saveAudio(event: FormEvent) {
        event.preventDefault();
        console.log("save audio");

        apiSaveAudio(word, tag, accessibility, audioBlob!)
            .then(() => {
                setAudioBlob(undefined);
                if (recordIndex < wordArray.length - 1) {
                    setWord(wordArray[recordIndex + 1]);
                    setRecordIndex((ri) => ri + 1);
                } else {
                    nav("/?text=" + searchParams.get("text"));
                }
            }).catch((err) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    setError(err.response.data);
                }
            });
    }

    return (
        <Box>
            <Grid container justifyContent={"center"}>
                {wordArray &&
                    wordArray.map((sp, i) =>
                        <Grid item key={i} color={i === recordIndex ? "#d70000" : "black"} m={1}>
                            <Typography variant={"h6"}>{sp}</Typography>
                        </Grid>)
                }
            </Grid>
            <Typography variant={"h6"} align={"center"} mb={2}>
                Record your missing words
            </Typography>
            <RecordInfo word={word} setWord={setWord} tag={tag} setTag={setTag} accessibility={accessibility} setAccessibility={setAccessibility} />
            <Record setAudio={setAudio} setAudioBlob={setAudioBlob} />
            {audio &&
                <Waveform audio={audio} />
            }
            {audioBlob &&
                <Grid component={"form"} justifyContent={"center"} onSubmit={saveAudio} sx={{ mt: 2 }}>
                    <Grid item m={0.5}>
                        <Button type="submit" variant="contained">
                            save audio to db
                        </Button>
                    </Grid>
                </Grid>
            }
        </Box>
    )
}