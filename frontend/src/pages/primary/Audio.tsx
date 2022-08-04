import {Button, Grid} from "@mui/material";
import CustomAudioPlayer from "./CustomAudioPlayer";

interface AudioProps {
    getAudio: () => any,
    audioBlobPart: BlobPart
}

export default function Audio(props: AudioProps) {

    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item margin={2}>
                <Button variant={"contained"} onClick={props.getAudio}>get audio file</Button>
            </Grid>
            <Grid item margin={2}>
                {props.audioBlobPart &&
                    <CustomAudioPlayer audiofile={window.URL.createObjectURL(new Blob([props.audioBlobPart!]))} slider={true} download={true} autoPlay={true}/>
                }
            </Grid>
        </Grid>
    )
}