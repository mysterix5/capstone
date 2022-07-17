
export type Recorder = {
    recordingNow: boolean,
    recordingSeconds: number,
    mediaStream: MediaStream | null,
    mediaRecorder: MediaRecorder | null,
    audio: string | null
};


export type UseRecorder = {
    recorderState: Recorder,
    startRecording: () => void,
    saveRecording: () => void
};