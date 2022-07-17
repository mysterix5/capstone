import {Box, Button, Typography} from "@mui/material";
import {Recorder} from "./model";
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import SaveIcon from '@mui/icons-material/Save';

interface RecorderControlsProps {
    recorderState: Recorder,
    handlers: {
        startRecording: () => void,
        saveRecording: () => void
    }
}

export default function RecorderControls({recorderState, handlers}: RecorderControlsProps) {
    const {recordingSeconds, recordingNow} = recorderState;
    const {startRecording, saveRecording} = handlers;

    return (
        <Box>
            <Typography>
                Recorder controls
            </Typography>
            <div>
                {recordingNow ? (
                    <Button onClick={saveRecording}>
                        <SaveIcon sx={{color: "red"}}/>
                    </Button>
                ) : (
                    <Button onClick={startRecording}>
                        <RadioButtonCheckedIcon sx={{color: "red"}}/>
                    </Button>
                )}
            </div>
        </Box>
    )
}