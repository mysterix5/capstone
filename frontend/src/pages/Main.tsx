import {Grid} from "@mui/material";
import TextSubmit from "./subcomponents/TextSubmit";
import {useEffect, useState} from "react";
import {TextResponse} from "../services/model";
import TextCheck from "./subcomponents/TextCheck";
import Audio from "./subcomponents/Audio";
import {apiGetAudio} from "../services/apiServices";
import {isAvailable} from "../globalTools/helpers";
import {useAuth} from "../usermanagement/AuthProvider";
import {useNavigate} from "react-router-dom";


export default function Main() {
    const [splitText, setSplitText] = useState<TextResponse>();
    const [audioFile, setAudioFile] = useState<any>();
    const [ids, setIds] = useState<string[]>([])

    const {username, getToken, setError} = useAuth();
    const nav = useNavigate();

    useEffect(() => {
        if (!username) {
            nav("/login")
        }
    }, [username, nav])

    function checkSplitText() {
        for (const word of splitText!.textWords) {
            if (!isAvailable(word.availability)) {
                return false;
            }
        }
        return true;
    }

    function getAudio() {
        apiGetAudio(getToken(), ids)
            .then(setAudioFile)
            .catch((err) => {
                if (err.response) {
                    const enc = new TextDecoder('utf-8')
                    const res = JSON.parse(enc.decode(new Uint8Array(err.response.data)))
                    setError(res);
                }
            });
    }


    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item>
                <TextSubmit setSplitText={setSplitText} setIds={setIds}/>
            </Grid>
            <Grid item ml={2} mr={2}>
                {
                    splitText &&
                    <TextCheck splitText={splitText} ids={ids} setIds={setIds}/>
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