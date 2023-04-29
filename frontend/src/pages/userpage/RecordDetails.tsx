import {
    Box,
    Button,
    Card,
    CardActions,
    CardContent,
    Grid,
    Switch,
    TextField, ToggleButton, ToggleButtonGroup,
    Typography
} from "@mui/material";
import { RecordInfo } from "../../services/model";
import { ChangeEvent, FormEvent, MouseEvent, useState } from "react";
import { useAuth } from "../../usermanagement/AuthProvider";
import {
    apiChangeRecord,
    apiDeleteRecord,
    apiGetSingleRecordedAudio,
    apiUpdateAudio
} from "../../services/apiServices";
import { AxiosError } from "axios";
import Waveform from "../record/Waveform";

interface RecordDetailsProps {
    record: RecordInfo,
    accessibilityChoices: string[],
    getRecordPage: () => void
}

export default function RecordDetails(props: RecordDetailsProps) {
    const [edit, setEdit] = useState(false);

    const [word, setWord] = useState(props.record.word);
    const [tag, setTag] = useState(props.record.tag);
    const [accessibility, setAccessibility] = useState(props.record.accessibility);

    const [audioBlob, setAudioBlob] = useState<Blob>();
    const [audioUrl, setAudioUrl] = useState<string>("");
    const [cut, setCut] = useState<boolean>(false);

    const { setError, defaultApiResponseChecks } = useAuth();

    const handleEditSwitch = (event: ChangeEvent<HTMLInputElement>) => {
        setEdit(event.target.checked);
    };

    const handleAccessibility = (
        event: MouseEvent<HTMLElement>,
        newAccessibility: string,
    ) => {
        setAccessibility(newAccessibility);
    };

    function saveWord() {
        apiChangeRecord({ id: props.record.id, word: word, tag: tag, accessibility: accessibility })
            .then(props.getRecordPage)
            .then(() => setEdit(false))
            .catch((err) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    setError(err.response.data);
                }
            });
    }

    function getAudio() {
        apiGetSingleRecordedAudio(props.record.id)
            .then(blob => {
                setAudioBlob(blob);
                setAudioUrl(URL.createObjectURL(blob));
            }).catch((err: AxiosError<ArrayBuffer>) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    const enc = new TextDecoder('utf-8')
                    const res = JSON.parse(enc.decode(new Uint8Array(err.response.data)))
                    setError(res);
                }
            }
            );
    }

    function deleteRecord() {
        apiDeleteRecord(props.record.id)
            .then(props.getRecordPage)
            .catch(err => {
                defaultApiResponseChecks(err);
            });
    }

    function setAudioUrlWrapper(url: string) {
        setAudioUrl(url);
        setCut(true);
    }

    function updateAudio(event: FormEvent) {
        event.preventDefault();
        console.log("save audio");

        apiUpdateAudio(props.record.id, audioBlob!)
            .then(() => {
                setAudioUrl("");
                setAudioBlob(undefined);
                setWord("");
                setCut(false);
            })
            .catch((err) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    setError(err.response.data);
                }
            });
    }

    return (
        <Grid item xs={6} sm={4} md={3} lg={2}>
            <Card sx={{ m: 0.4, boxShadow: 4 }}>
                <CardContent>
                    {edit ?
                        <Grid container direction={"column"}>
                            <Grid item>
                                <TextField size={"small"} value={word}
                                    onChange={event => setWord(event.target.value)} />
                            </Grid>
                            <Grid item>
                                <TextField size={"small"} value={tag}
                                    onChange={event => setTag(event.target.value)} />
                            </Grid>
                            <Grid item>
                                <ToggleButtonGroup
                                    size={"small"}
                                    value={accessibility}
                                    exclusive
                                    onChange={handleAccessibility}
                                >
                                    {
                                        props.accessibilityChoices.map(acc =>
                                            <ToggleButton key={acc} value={acc} size={"small"}>
                                                {acc}
                                            </ToggleButton>
                                        )
                                    }
                                </ToggleButtonGroup>
                            </Grid>
                            <Grid item>
                                <Button onClick={saveWord}>
                                    save
                                </Button>
                            </Grid>
                        </Grid>
                        :
                        <>
                            <Typography display={'inline'}>word: {props.record.word}</Typography>
                            <Typography>tag: {props.record.tag}</Typography>
                            <Typography>access: {props.record.accessibility}</Typography>
                        </>

                    }
                </CardContent>
                <CardActions sx={{ flexDirection: "column" }}>
                    <Grid container direction={"column"}>
                        <Grid item>
                            <Box>
                                edit:
                                <Switch
                                    checked={edit}
                                    onChange={handleEditSwitch}
                                    inputProps={{ 'aria-label': 'controlled' }}
                                />
                            </Box>

                        </Grid>
                        <Grid item>
                            <Button onClick={getAudio}>get audio</Button>
                        </Grid>
                        {audioUrl &&
                            <Waveform audio={audioUrl} setAudio={setAudioUrlWrapper} setAudioBlob={setAudioBlob} />
                        }
                        {cut &&
                            <Grid item>
                                <Button onClick={updateAudio}>save audio</Button>
                            </Grid>
                        }
                        <Grid item>
                            <Button onClick={deleteRecord}>delete</Button>
                        </Grid>
                    </Grid>
                </CardActions>
            </Card>
        </Grid>
    )
}
