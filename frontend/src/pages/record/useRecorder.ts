import {useEffect, useState} from "react";
import {Recorder} from "./model";

const initialState: Recorder = {
    recordingNow: false,
    recordingSeconds: 0,
    mediaStream: null,
    mediaRecorder: null,
    audio: null
}
type Interval = null | number | ReturnType<typeof setInterval>;

export default function useRecorder() {
    const [recorderState, setRecorderState] = useState<Recorder>(initialState);


    useEffect(() => {
        const MAX_RECORDER_TIME = 15;
        let recordingInterval: Interval = null;
        if (recorderState.recordingNow)
            recordingInterval = setInterval(() => {
                setRecorderState((prevState: Recorder) => {
                    if (prevState.recordingSeconds >= MAX_RECORDER_TIME) {
                        typeof recordingInterval === "number" && clearInterval(recordingInterval);
                        return prevState;
                    }
                    if (prevState.recordingSeconds >= 0) {
                        return {
                            ...prevState,
                            recordingSeconds: prevState.recordingSeconds + 1,
                        };
                    } else {
                        return prevState;
                    }
                });
            }, 1000);
        else typeof recordingInterval === "number" && clearInterval(recordingInterval);

        return () => {
            typeof recordingInterval === "number" && clearInterval(recordingInterval);
        };

    });

    useEffect(() => {
        setRecorderState((prevState) => {
            if (prevState.mediaStream)
                return {
                    ...prevState,
                    mediaRecorder: new MediaRecorder(prevState.mediaStream, {mimeType: 'audio/wav;codecs=pcm'}),
                };
            else return prevState;
        });
    }, [recorderState.mediaStream]);

    useEffect(() => {
        const recorder = recorderState.mediaRecorder;
        let chunks: Blob[] = [];

        if (recorder && recorder.state === "inactive") {
            recorder.start();

            recorder.ondataavailable = (e: { data: Blob }) => {
                chunks.push(e.data);
            };

            recorder.onstop = () => {
                const blob = new Blob(chunks, {type: "audio/wav; codecs=0"});
                chunks = [];

                setRecorderState((prevState: Recorder) => {
                    if (prevState.mediaRecorder)
                        return {
                            ...initialState,
                            audio: window.URL.createObjectURL(blob),
                        };
                    else return initialState;
                });
            };
        }

        return () => {
            if (recorder) recorder.stream.getAudioTracks().forEach((track: MediaStreamTrack) => track.stop());
        };
    }, [recorderState.mediaRecorder]);

    async function startRecording() {
        try {
            const stream: MediaStream = await navigator.mediaDevices.getUserMedia({audio: true});

            setRecorderState((prevState) => {
                return {
                    ...prevState,
                    recordingNow: true,
                    mediaStream: stream,
                };
            });
        } catch (err) {
            console.log(err);
        }
    }

    function saveRecording(recorder: MediaRecorder) {
        if (recorder.state !== "inactive") recorder.stop();
    }

    return {
        recorderState,
        startRecording: () => startRecording(),
        saveRecording: () => saveRecording(recorderState.mediaRecorder!)
    }
}