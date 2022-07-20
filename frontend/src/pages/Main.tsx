import {Grid} from "@mui/material";
import TextSubmit from "./subcomponents/TextSubmit";
import {useState} from "react";
import {TextResponse} from "../services/model";
import TextCheck from "./subcomponents/TextCheck";
import Audio from "./subcomponents/Audio";
import {apiGetAudio} from "../services/apiServices";


export default function Main() {
    const [splitText, setSplitText] = useState<TextResponse>();
    const [audioFile, setAudioFile] = useState<any>();

    function isAvailable(availability: string){
        return availability==="PUBLIC";
    }

    function checkSplitText(){
        for(const word of splitText!.textWords){
            if(!isAvailable(word.availability)){
                return false;
            }
        }
        return true;
    }

    function getAudio(){
        apiGetAudio(splitText!.textWords)
            .then(setAudioFile);
    }


    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item>
                <TextSubmit setSplitText={setSplitText}/>
            </Grid>
            <Grid item ml={2} mr={2}>
                {
                    splitText &&
                    <TextCheck splitText={splitText}/>
                }
            </Grid>
            <Grid item>
                {
                    splitText && checkSplitText() &&
                    <Audio getAudio={getAudio} audioFile={audioFile}/>
                }
            </Grid>
        </Grid>
    )
}