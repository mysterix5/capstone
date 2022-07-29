import {Grid} from "@mui/material";
import TextSubmit from "./TextSubmit";
import {useEffect, useState} from "react";
import {TextMetadataResponse} from "../../services/model";
import TextCheck from "./TextCheck";
import Audio from "./Audio";
import {apiGetMergedAudio} from "../../services/apiServices";
import {isAvailable} from "../../globalTools/helpers";
import {useAuth} from "../../usermanagement/AuthProvider";
import {useNavigate} from "react-router-dom";

const textMetadataResponse_initial = {
    textWords: [],
    defaultIds: [],
    wordRecordMap: {}
};
export default function Primary() {
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>(textMetadataResponse_initial);
    const [audioFile, setAudioFile] = useState<any>();

    const {username, getToken, setError} = useAuth();
    const nav = useNavigate();

    useEffect(() => {
        if (!username) {
            nav("/login")
        }
    }, [username, nav])

    function handleTextMetadataResponse(textMetadataResponse: TextMetadataResponse) {
        setTextMetadataResponse(textMetadataResponse_initial);
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
        apiGetMergedAudio(getToken(), textMetadataResponse?.defaultIds!)
            .then(setAudioFile)
            .catch((err) => {
                if (err.response) {
                    const enc = new TextDecoder('utf-8')
                    const res = JSON.parse(enc.decode(new Uint8Array(err.response.data)))
                    setError(res);
                }
            });
    }

    function setId(id: string, index: number) {
        let tmp = {...textMetadataResponse};
        tmp.defaultIds[index] = id
        setTextMetadataResponse(tmp);
    }

    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item>
                <TextSubmit setTextMetadataResponse={handleTextMetadataResponse}/>
            </Grid>
            <Grid item ml={2} mr={2}>
                {
                    textMetadataResponse &&
                    <TextCheck key={"textcheck"} textMetadataResponse={textMetadataResponse} setId={setId}/>
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