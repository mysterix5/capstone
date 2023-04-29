import { useEffect, useRef, useState } from "react";
import { Button, Grid } from "@mui/material";

const mimeType = "audio/webm";
const audioBitsPerSecond = 128000;

interface RecordProps {
    setAudio: (audioUrl: string) => void,
    setAudioBlob: (audio: Blob) => void,
}

export default function Record(props: RecordProps) {
    const [isRecording, setIsRecording] = useState(false);
    const [permission, setPermission] = useState(false);
    const [stream, setStream] = useState<MediaStream | null>(null);
    const [audioChunks, setAudioChunks] = useState<BlobPart[]>([]);

    const mediaRecorder = useRef<MediaRecorder | null>(null);



    useEffect(() => {
        getMicrophonePermission();
    }, [])

    const getMicrophonePermission = async () => {
        if ("MediaRecorder" in window) {
            try {
                const streamData = await navigator.mediaDevices.getUserMedia({
                    audio: true,
                    video: false,
                });
                setPermission(true);
                setStream(streamData);
            } catch (err: any) {
                alert(err.message);
            }
        } else {
            alert("The MediaRecorder API is not supported in your browser.");
        }
    };

    const startRecording = async () => {
        if (!stream) {
            return;
        }
        setIsRecording(true);
        //create new Media recorder instance using the stream
        const media = new MediaRecorder(stream, { mimeType: mimeType, audioBitsPerSecond: audioBitsPerSecond, });
        //set the MediaRecorder instance to the mediaRecorder ref
        mediaRecorder.current = media;
        //invokes the start method to start the recording process
        mediaRecorder.current.start();
        let localAudioChunks: BlobPart[] = [];
        mediaRecorder.current.ondataavailable = (event) => {
            if (typeof event.data === "undefined") return;
            if (event.data.size === 0) return;
            localAudioChunks.push(event.data);
        };
        setAudioChunks(localAudioChunks);
    };

    const stopRecording = () => {
        if (!mediaRecorder || !mediaRecorder.current) {
            console.log("stop recording failed: mediaRecorder not defined");
            return;
        }
        setIsRecording(false);
        //stops the recording instance
        mediaRecorder.current.stop();
        mediaRecorder.current.onstop = () => {
            //creates a blob file from the audiochunks data
            const audioBlob = new Blob(audioChunks, { type: mimeType });
            //creates a playable URL from the blob file.
            const audioUrl = URL.createObjectURL(audioBlob);
            props.setAudioBlob(audioBlob);
            props.setAudio(audioUrl);
            setAudioChunks([]);
        };
    };

    return (
        <Grid container alignContent={"center"} flexDirection={"column"}>
            {!permission ? (
                <Button variant="contained" onClick={getMicrophonePermission}>
                    Get Microphone
                </Button>
            ) : null}
            {permission && isRecording === false ? (
                <Button variant="contained" onClick={startRecording}>
                    Start Recording
                </Button>
            ) : null}
            {isRecording === true ? (
                <Button variant="contained" onClick={stopRecording}>
                    Stop Recording
                </Button>
            ) : null}
        </Grid>
    )
}
