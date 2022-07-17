import {Box, Typography} from "@mui/material";
import RecorderControls from "./RecorderControls";
import useRecorder from "./useRecorder";
import {UseRecorder} from "./model";


export default function Record() {

    const {recorderState, ...handlers}: UseRecorder = useRecorder();
    const {audio} = recorderState;

    return (
        <Box>
            <Typography variant={"h4"} align={"center"}>
                Record page
            </Typography>
            <RecorderControls recorderState={recorderState} handlers={handlers}/>
            <>
                {audio &&
                    <audio src={audio} autoPlay={false} controls={true}/>
                }
            </>
        </Box>
    )
}