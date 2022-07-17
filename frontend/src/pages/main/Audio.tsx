import {Button, Grid} from "@mui/material";

interface AudioProps {
    getAudio: () => any,
    audioFile: any
}

export default function Audio(props: AudioProps) {

    return (
        <Grid container justifyContent={"center"}>
            <Grid item margin={2}>
                <Button variant={"contained"} onClick={props.getAudio}>get audio file</Button>
            </Grid>
            <Grid item margin={2}>
                {props.audioFile &&
                    <audio src={props.audioFile} autoPlay={false} controls={true}>

                    </audio>
                }
            </Grid>
        </Grid>
    )
}