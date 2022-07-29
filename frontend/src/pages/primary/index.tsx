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


export default function Primary() {
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>({
        textWords: [],
        defaultIds: [],
        wordRecordMap: {}
    });
    const [audioFile, setAudioFile] = useState<any>();

    const {getToken, setError} = useAuth();
    const nav = useNavigate();

    useEffect(() => {
        if (!getToken()) {
            nav("/login")
        }
    }, [getToken, nav])

    function handleTextMetadataResponse(textMetadataResponseLocal: TextMetadataResponse) {
        setTextMetadataResponse(textMetadataResponseLocal);
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
        setTextMetadataResponse(current=>{
            let tmp = {...current};
            tmp.defaultIds[index] = id;
            return tmp;
        });
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