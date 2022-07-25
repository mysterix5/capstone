import {
    Button,
    Card,
    CardActions,
    CardContent,
    Grid,
    Switch,
    TextField, ToggleButton, ToggleButtonGroup,
    Typography
} from "@mui/material";
import {RecordInfo} from "../../services/model";
import {ChangeEvent, MouseEvent, useState} from "react";
import {useAuth} from "../../usermanagement/AuthProvider";
import {apiDeleteRecord, apiGetSingleRecordedAudio} from "../../services/apiServices";

interface RecordDetailsProps {
    record: RecordInfo,
    accessibilityChoices: string[],
    getRecordPage: ()=>void
}

export default function RecordDetails(props: RecordDetailsProps) {
    const [edit, setEdit] = useState(false);

    const [word, setWord] = useState(props.record.word);
    const [tag, setTag] = useState(props.record.tag);
    const [accessibility, setAccessibility] = useState(props.record.accessibility);

    const [audioFile, setAudioFile] = useState<any>();

    const {getToken, setError} = useAuth();

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

    }

    function getAudio() {
        apiGetSingleRecordedAudio(getToken(), props.record.id)
            .then(setAudioFile)
            .then(props.getRecordPage)
            .catch((err) => {
                    if (err.response) {
                        const enc = new TextDecoder('utf-8')
                        const res = JSON.parse(enc.decode(new Uint8Array(err.response.data)))
                        setError(res);
                    }
                }
            );
    }

    function deleteRecord(){
        apiDeleteRecord(getToken(), props.record.id);//TODO refresh
    }

    return (
        <Grid item>
            <Card sx={{m: 1, boxShadow: 4}}>
                <CardContent>
                    {edit ?
                        <Grid container direction={"column"}>
                            <Grid item>
                                <TextField size={"small"} value={word}
                                           onChange={event => setWord(event.target.value)}/>
                            </Grid>
                            <Grid item>
                                <TextField size={"small"} value={tag}
                                           onChange={event => setTag(event.target.value)}/>
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
                <CardActions sx={{flexDirection: "column"}}>
                    <Grid container direction={"column"}>
                        <Grid item>
                            <Switch
                                checked={edit}
                                onChange={handleEditSwitch}
                                inputProps={{'aria-label': 'controlled'}}
                            />
                        </Grid>
                        <Grid item>
                            <Button onClick={getAudio}>get audio</Button>
                        </Grid>
                        {audioFile &&
                            <audio src={audioFile} autoPlay={true} controls={true} title="vover.mp3"/>
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
