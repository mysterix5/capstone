import React from "react";
import {Pause, PlayArrow as Play} from "@mui/icons-material";

interface AudioControlsProps {
    isPlaying: boolean,
    onPlayPauseClick: any
}

export default function AudioControls(props: AudioControlsProps) {

    return (
        <div className="audio-controls">
            {props.isPlaying ? (
                <button
                    type="button"
                    className="pause"
                    onClick={() => props.onPlayPauseClick(false)}
                    aria-label="Pause"
                >
                    <Pause/>
                </button>
            ) : (
                <button
                    type="button"
                    className="play"
                    onClick={() => props.onPlayPauseClick(true)}
                    aria-label="Play"
                >
                    <Play/>
                </button>
            )}
        </div>
    )
}
