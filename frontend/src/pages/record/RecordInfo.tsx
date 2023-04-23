import { Grid, TextField, ToggleButton, ToggleButtonGroup } from "@mui/material";
import { MouseEvent } from "react";

interface RecordInfoProps {
    word: string,
    setWord: (s: string) => void,
    tag: string,
    setTag: (s: string) => void,
    accessibility: string,
    setAccessibility: (s: string) => void,
}

export default function RecordInfo(props: RecordInfoProps) {

    const handleAccessibility = (event: MouseEvent<HTMLElement>, newAccessibility: string) => {
        props.setAccessibility(newAccessibility);
    };


    return (
        <Grid container alignContent={"center"} flexDirection={"column"}>
            <Grid item m={0.5}>
                <TextField
                    label="Word"
                    variant="outlined"
                    value={props.word}
                    placeholder={"your word"}
                    onChange={event => props.setWord(event.target.value)}
                />
            </Grid>
            <Grid item m={0.5}>
                <TextField
                    label="Tag"
                    variant="outlined"
                    value={props.tag}
                    placeholder={props.tag}
                    onChange={event => props.setTag(event.target.value)}
                />
            </Grid>
            <Grid item m={0.5}>
                <ToggleButtonGroup
                    value={props.accessibility}
                    size={"small"}
                    exclusive
                    onChange={handleAccessibility}
                >
                    <ToggleButton value={"PUBLIC"}>public</ToggleButton>
                    <ToggleButton value={"FRIENDS"}>friends</ToggleButton>
                    <ToggleButton value={"PRIVATE"}>private</ToggleButton>
                </ToggleButtonGroup>
            </Grid>
        </Grid>
    )
}