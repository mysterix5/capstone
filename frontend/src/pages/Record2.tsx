import {Box, Button, Typography} from "@mui/material";
import {useEffect, useState} from "react";

import { createFFmpeg, fetchFile } from "ffmpeg";

export default function Record2() {

    // const [blob, setBlob] = useState<Blob>();
    // const [audioLink, setAudioLink] = useState<any>();
    // const [mediaStream, setMediaStream] = useState<MediaStream>();
    // const [recorder, setRecorder] = useState<MediaRecorder>();
    // const [recordingNow, setRecordingNow] = useState(false);
    //
    // useEffect(() => {
    //     if (mediaStream) {
    //         setRecorder(new MediaRecorder(mediaStream))
    //     }
    // }, [mediaStream]);
    //
    // useEffect(() => {
    //     const rec = recorder;
    //     let chunks: Blob[] = [];
    //
    //     if (rec && rec.state === "inactive") {
    //         rec.start();
    //
    //         rec.ondataavailable = (e: { data: Blob }) => {
    //             chunks.push(e.data);
    //         };
    //
    //         rec.onstop = () => {
    //             const blob = new Blob(chunks, {type: 'audio/mpeg-3'});
    //             chunks = [];
    //
    //             setAudioLink(window.URL.createObjectURL(blob));
    //             setBlob(blob);
    //         };
    //     }
    // }, [recorder]);
    //
    // async function startRecording() {
    //     try {
    //         const stream: MediaStream = await navigator.mediaDevices.getUserMedia({audio: true});
    //
    //         setRecordingNow(true);
    //         setMediaStream(stream);
    //     } catch
    //         (err) {
    //         console.log(err);
    //     }
    // }
    //
    // function saveRecording(recorder: MediaRecorder) {
    //     if (recorder.state !== "inactive") recorder.stop();
    // }
    //
    // const [secLink, setSecLink] = useState();
    //
    // async function convertToMp3(){
    //     const ffmpeg = createFFmpeg();
    //     await ffmpeg.load();
    //     ffmpeg.FS("writeFile", audioLink, blob);
    //     await ffmpeg.run("-i", audioLink, "output.mp3");
    //     const data = ffmpeg.FS("readFile", "output.mp3");
    //     const url = URL.createObjectURL(
    //         new Blob([data.buffer], { type: "audio/mp3" })
    //     );
    // }

    return (
        <Box>
            {/*<Typography>*/}
            {/*    Record page*/}
            {/*</Typography>*/}
            {/*<div>*/}
            {/*    {!recordingNow ?*/}
            {/*        <Button onClick={startRecording}>*/}
            {/*            start*/}
            {/*        </Button>*/}
            {/*        :*/}
            {/*        <Button onClick={() => saveRecording(recorder!)}>*/}
            {/*            stop*/}
            {/*        </Button>*/}
            {/*    }*/}
            {/*</div>*/}
            {/*<div>*/}
            {/*    {audioLink &&*/}
            {/*        <audio src={audioLink} autoPlay={false} controls={true} title="record.mp3"/>*/}
            {/*    }*/}
            {/*</div>*/}
            {/*<Button onClick={convertToMp3}>*/}
            {/*    convert*/}
            {/*</Button>*/}
            {/*<div>*/}
            {/*    {secLink &&*/}
            {/*        <audio src={secLink} autoPlay={false} controls={true} title="record2.mp3"/>*/}
            {/*    }*/}
            {/*</div>*/}

        </Box>
    )
}