import {Grid} from "@mui/material";
import TextSubmit from "./subcomponents/TextSubmit";
import {useState} from "react";
import {WordResponse} from "../services/model";
import TextCheck from "./subcomponents/TextCheck";


export default function Main() {

    const [splitText, setSplitText] = useState<WordResponse[]>();

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
        </Grid>
    )
}