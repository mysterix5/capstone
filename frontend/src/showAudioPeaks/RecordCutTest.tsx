import {useEffect, useState} from "react";
import getPeaks from "./peaks";

import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import {Bar} from 'react-chartjs-2';
import {Slider} from "@mui/material";
import CustomAudioPlayer from "../pages/primary/CustomAudioPlayer";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
);

interface FileReadAsType {
    ArrayBuffer: ArrayBuffer;
    DataURL: string;
}

/**
 * FileReader in promise
 */
const readFile = <Type extends keyof FileReadAsType>(
    file: Blob,
    dataType: Type,
) => new Promise<FileReadAsType[Type]>((resolve, reject) => {
    const reader = new FileReader();
    (reader as any)[`readAs${dataType}`](file);
    reader.onload = () => resolve(reader.result as any);
    reader.onerror = (err) => reject(err);
});

/**
 * Read File/Blob to ArrayBuffer
 */
const readArrayBuffer = (file: Blob) => readFile(file, 'ArrayBuffer');

export async function decodeAudioBuffer(blob: Blob) {
    const arrayBuffer = await readArrayBuffer(blob);
    const audioBuffer = await new AudioContext().decodeAudioData(arrayBuffer);

    return audioBuffer;
}

const options = {
    responsive: true,
    plugins: {
        legend: {
            display: false,
            position: 'top' as const,
        },
        title: {
            display: false,
            text: 'Chart.js Bar Chart',
        },
    },
    scales: {
        x: {
            display: false,
        },
        y: {
            display: false,
        }
    },
};

function peakDataToChart(thePeaks: [min: Float32Array, max: Float32Array]) {
    const length = thePeaks[0].length;
    let peakChartData = new Array(length)
    let labels = new Array(length)

    for (let i = 0; i < length; i++) {
        peakChartData[i] = [thePeaks[0][i], thePeaks[1][i]];
        labels[i] = i;
    }
    const ret = {
        labels: labels,
        datasets: [
            {
                label: "audio peaks",
                data: peakChartData,
                backgroundColor: [
                    'rgba(255, 255, 255, 0.6)'
                ],
                borderWidth: 0,
            }
        ]
    }
    return ret;
}

//
// function peakDataToChart(thePeaks: [min: Float32Array, max: Float32Array]) {
//     const length = thePeaks[0].length;
//     let peakChartData = new Array(length)
//
//     for(let i = 0; i<length; i++){
//         peakChartData[i] = {"index": i, "min": thePeaks[0][i],  "max": thePeaks[1][i]};
//     }
//     return peakChartData;
// }


const chartColors: string[] = [
    "#ff6358",
    "#ffd246",
    "#78d237",
    "#28b4c8",
    "#2d73f5",
    "#aa46be",
];

interface RecordCutTestProps {
    blob: Blob
}

export default function RecordCutTest(props: RecordCutTestProps) {
    const [audioBuffer, setAudioBuffer] = useState<AudioBuffer>();
    const [width, setWidth] = useState(100);
    const [peaks, setPeaks] = useState<[min: Float32Array, max: Float32Array]>();
    const [audioRange, setAudioRange] = useState<[start: number, end: number]>([0.0,100.0]);

    const handleChange1 = (
        event: Event,
        newValue: number | number[],
        activeThumb: number,
    ) => {
        if (!Array.isArray(newValue)) {
            return;
        }

        const minDistance = 10;
        if (activeThumb === 0) {
            setAudioRange([Math.min(newValue[0], audioRange[1] - minDistance), audioRange[1]]);
        } else {
            setAudioRange([audioRange[0], Math.max(newValue[1], audioRange[0] + minDistance)]);
        }
    };

    useEffect(() => {
            decodeAudioBuffer(props.blob)
                .then((b) => {
                    setAudioBuffer(b);
                    return b;
                })
                .then(b => {
                    let p = getPeaks(width, b.getChannelData(0));
                    setPeaks(p);
                    console.log(peakDataToChart(p));
                })
        }, []
    )
    useEffect(() => {
        console.log(peaks);
    }, [peaks])

    return (
        <>
            <CustomAudioPlayer audiofile={window.URL.createObjectURL(props.blob)} slider={false} download={false} autoPlay={true} audioRange={audioRange}/>
            {peaks &&
                <>
                    <Bar options={options} data={peakDataToChart(peaks)}/>

                    <Slider
                        size={"small"}
                        getAriaLabel={() => 'Minimum distance'}
                        value={audioRange}
                        onChange={handleChange1}
                        valueLabelDisplay="auto"
                        disableSwap
                    />
                </>
            }
        </>
    )

}

// <Chart seriesColors={chartColors}>
//     <ChartSeries>
//         <ChartSeriesItem
//             type="rangeColumn"
//             data={peakDataToChart(peaks)}
//             fromField="min"
//             toField="max"
//             categoryField="index"
//         >
//
//         </ChartSeriesItem>
//     </ChartSeries>
//     <ChartCategoryAxis><ChartCategoryAxisItem labels={{ rotation: "auto" }} /></ChartCategoryAxis>
// </Chart>
