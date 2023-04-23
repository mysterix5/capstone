import { useEffect, useRef, useState } from 'react'
import WaveSurfer from 'wavesurfer.js'
import { Pause, PlayArrow, Stop } from "@mui/icons-material";
import { Box, Slider, Typography } from '@mui/material';

interface WaveformProps {
    audio: string,
}

const minDistance = 10;

const Waveform = (props: WaveformProps) => {
    const containerRef = useRef<HTMLDivElement>(null);
    const waveSurferRef = useRef<WaveSurfer>();
    const [isPlaying, setIsPlaying] = useState(false);
    const [startCut, setStartCut] = useState(0.0);
    const [endCut, setEndCut] = useState(1.0);
    const [duration, setDuration] = useState(1.0);
    const [start, setStart] = useState(0.0);
    const [end, setEnd] = useState(100.0);

    const handleChange = (
        event: Event,
        newValue: number | number[],
        activeThumb: number,
    ) => {
        if (!Array.isArray(newValue)) {
            return;
        }

        var localStart = newValue[0];
        var localEnd = newValue[1];

        if (activeThumb === 0) {
            localStart = Math.min(localStart, end - minDistance);
            localEnd = end;
        } else {
            localStart = start;
            localEnd = Math.max(localEnd, start + minDistance)
        }

        setStart(localStart);
        setEnd(localEnd);
        setStartCut(localStart / 100.0 * duration)
        setEndCut(localEnd / 100.0 * duration)

        stop();
    };


    const stop = () => {
        if (waveSurferRef.current) {
            waveSurferRef.current.stop();
            waveSurferRef.current.setCurrentTime(startCut);
            waveSurferRef.current.setPlayEnd(endCut);
            setIsPlaying(false);
        }
    }

    const play = () => {
        if (waveSurferRef.current) {
            const start1 = Math.max(startCut, waveSurferRef.current.getCurrentTime());
            waveSurferRef.current.play(start1, endCut);
            setIsPlaying(true);
        }
    }

    const pause = () => {
        if (waveSurferRef.current) {
            waveSurferRef.current.pause();
            setIsPlaying(false);
        }
    }

    useEffect(() => {
        if (containerRef && containerRef.current) {
            const waveSurfer = WaveSurfer.create({
                container: containerRef.current,
                responsive: true,
                cursorWidth: 1,
                barWidth: 1,
                barHeight: 5,
            })
            waveSurfer.load(props.audio)
            waveSurfer.on('ready', () => { waveSurferRef.current = waveSurfer })
            waveSurfer.on('ready', () => { setEndCut(waveSurfer.getDuration()) })
            waveSurfer.on('ready', () => { setDuration(waveSurfer.getDuration()) })
            waveSurfer.on('finish', () => { setIsPlaying(false) })


            return () => {
                waveSurfer.destroy()
            }
        }
    }, [props.audio])

    return (<Box border={2} borderRadius={5} m={1}>
        <Box mt={1} mb={1} mr={2} ml={2}>
            {isPlaying ?
                <button onClick={() => {
                    pause();
                }} type="button"
                >
                    <Pause />
                </button>
                :
                <button onClick={() => {
                    play()
                }} type="button"
                >
                    <PlayArrow />
                </button>
            }
            <button onClick={() => {
                stop()
            }} type="button"
            >
                <Stop />
            </button>
            <div ref={containerRef} />
                <Slider
                    getAriaLabel={() => 'Minimum distance'}
                    value={[start, end]}
                    onChange={handleChange}
                    valueLabelDisplay="auto"
                    // getAriaValueText={valuetext}
                    disableSwap
                />
        </Box>
    </Box>
    )
}


export default Waveform