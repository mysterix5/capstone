import {
    Button,
    Card,
    CardActions,
    CardContent,
    Grid,
    Switch,
    TextField,
    Typography
} from "@mui/material";
import {RecordInfo} from "../../services/model";
import {ChangeEvent, useState} from "react";

interface RecordDetailsProps {
    record: RecordInfo
}

export default function RecordDetails(props: RecordDetailsProps) {
    const [edit, setEdit] = useState(false);

    const [word, setWord] = useState(props.record.word);
    const [tag, setTag] = useState(props.record.tag);
    const [accessibility, setAccessibility] = useState(props.record.accessibility);

    const handleEditSwitch = (event: ChangeEvent<HTMLInputElement>) => {
        setEdit(event.target.checked);
    };

    return (
        <Grid item>
            <Card sx={{m: 1, boxShadow: 4}}>
                <CardContent>
                    {edit ?
                        <>
                            <Typography display={'inline'}>word: <TextField size={"small"} value={word}
                                                                            onChange={event => setWord(event.target.value)}/></Typography>
                            <Typography>tag: <TextField size={"small"} value={tag}
                                                        onChange={event => setTag(event.target.value)}/></Typography>
                            <Typography>access: <TextField size={"small"} value={accessibility}
                                                           onChange={event => setAccessibility(event.target.value)}/></Typography>
                        </>
                        :
                        <>
                            <Typography display={'inline'}>word: {props.record.word}</Typography>
                            <Typography>tag: {props.record.tag}</Typography>
                            <Typography>access: {props.record.accessibility}</Typography>
                        </>

                    }
                </CardContent>
                <CardActions sx={{flexDirection: "column"}}>
                    <Button>get audio</Button>
                    <Button>delete</Button>
                    <Switch
                        checked={edit}
                        onChange={handleEditSwitch}
                        inputProps={{'aria-label': 'controlled'}}
                    />
                </CardActions>
            </Card>
        </Grid>
    )
}
