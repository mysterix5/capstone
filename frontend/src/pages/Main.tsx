import {Grid} from "@mui/material";
import TextSubmit from "./subcomponents/TextSubmit";
import {useState} from "react";
import {WordResponse} from "../services/model";
import TextCheck from "./subcomponents/TextCheck";
import Audio from "./subcomponents/Audio";
import {apiGetAudio} from "../services/apiServices";


export default function Main() {

    const [splitText, setSplitText] = useState<WordResponse[]>();

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
        apiGetAudio(splitText!);
    }

    return (
        <Grid container alignContent={"center"} flexDirection={"column"}>
            <Grid item>
                <TextSubmit setSplitText={setSplitText}/>
            </Grid>
            <Grid item>
                {
                    splitText &&
                    <TextCheck splitText={splitText}/>
                }
            </Grid>
            <Grid item>
                {
                    splitText && checkSplitText() &&
                    <Audio getAudio={getAudio}/>
                }
            </Grid>
        </Grid>
    )
}