import {Button, Grid} from "@mui/material";
import CustomAudioPlayer from "./CustomAudioPlayer";

interface AudioProps {
    getAudio: () => any,
    audioFile: any
}

export default function Audio(props: AudioProps) {

    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item margin={2}>
                <Button variant={"contained"} onClick={props.getAudio}>get audio file</Button>
            </Grid>
            <Grid item margin={2}>
                {props.audioFile &&
                    <CustomAudioPlayer audiofile={props.audioFile} slider={true} download={true} autoPlay={true}/>
                }
            </Grid>
        </Grid>
    )
}