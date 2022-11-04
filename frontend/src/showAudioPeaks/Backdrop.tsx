import React from "react";

interface BackdropProps {
    isPlaying: boolean
}

export default function Backdrop(props: BackdropProps) {
    return <div className={`color-backdrop ${props.isPlaying ? "playing" : "idle"}`}/>;
};
