import {Grid} from "@mui/material";
import TextSubmit from "./subcomponents/TextSubmit";
import {useEffect, useState} from "react";
import {TextMetadataResponse} from "../services/model";
import TextCheck from "./subcomponents/TextCheck";
import Audio from "./subcomponents/Audio";
import {apiGetMergedAudio} from "../services/apiServices";
import {isAvailable} from "../globalTools/helpers";
import {useAuth} from "../usermanagement/AuthProvider";
import {useNavigate} from "react-router-dom";


export default function Primary() {
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>();
    const [audioFile, setAudioFile] = useState<any>();
    const [ids, setIds] = useState<string[]>([])

    const {username, getToken, setError} = useAuth();
    const nav = useNavigate();

    useEffect(() => {
        if (!username) {
            nav("/login")
        }
    }, [username, nav])

    function handleTextMetadataResponse(textMetadataResponse: TextMetadataResponse){
        setTextMetadataResponse(textMetadataResponse);
        setAudioFile(null);
    }

    function checkTextResponseAvailability() {
        for (const word of textMetadataResponse!.textWords) {
            if (!isAvailable(word.availability)) {
                return false;
            }
        }
        return true;
    }

    function getAudio() {
        apiGetMergedAudio(getToken(), ids)
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
                <TextSubmit setTextMetadataResponse={handleTextMetadataResponse} setIds={setIds}/>
            </Grid>
            <Grid item ml={2} mr={2}>
                {
                    textMetadataResponse &&
                    <TextCheck textMetadataResponse={textMetadataResponse} ids={ids} setIds={setIds}/>
                }
            </Grid>
            <Grid item>
                {
                    textMetadataResponse && checkTextResponseAvailability() &&
                    <Audio getAudio={getAudio} audioFile={audioFile}/>
                }
            </Grid>
        </Grid>
    )
}