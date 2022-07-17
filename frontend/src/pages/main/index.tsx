import {Grid} from "@mui/material";
import {useState} from "react";
import {WordResponse} from "../../services/model";
import {apiGetAudio} from "../../services/apiServices";
import TextSubmit from "./TextSubmit";
import TextCheck from "./TextCheck";
import Audio from "./Audio";

export default function Main() {

    const [splitText, setSplitText] = useState<WordResponse[]>();
    const [audioFile, setAudioFile] = useState<any>();

    function isAvailable(availability: string){
        return availability==="PUBLIC";
    }

    function checkSplitText(){
        for(const word of splitText!){
            if(!isAvailable(word.availability)){
                return false;
            }
        }
        return true;
    }

    function getAudio(){
        apiGetAudio(splitText!)
            .then(setAudioFile);
    }


    return (
        <Grid container alignContent={"center"} flexDirection={"column"}>
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