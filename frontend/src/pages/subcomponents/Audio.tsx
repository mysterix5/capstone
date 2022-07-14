import {Button, Grid} from "@mui/material";

interface AudioProps {
    getAudio: () => any
}

export default function Audio(props: AudioProps) {

    return (
        <Grid container justifyContent={"center"}>
            <Grid item margin={2}>
                <Button variant={"contained"} onClick={props.getAudio}>get audio file</Button>
            </Grid>
        </Grid>
    )
}