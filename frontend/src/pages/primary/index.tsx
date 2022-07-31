import {Grid} from "@mui/material";
import TextSubmit from "./TextSubmit";
import {useCallback, useEffect, useState} from "react";
import {TextMetadataResponse} from "../../services/model";
import TextCheck from "./TextCheck";
import Audio from "./Audio";
import {apiGetHistoryEntryById, apiGetMergedAudio, apiSendTextToBackend} from "../../services/apiServices";
import {isAvailable} from "../../globalTools/helpers";
import {useAuth} from "../../usermanagement/AuthProvider";
import {useNavigate, useParams} from "react-router-dom";


export default function Primary() {
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>({
        textWords: [],
        defaultIds: [],
        wordRecordMap: {}
    });
    const [audioFile, setAudioFile] = useState<any>();
    const [text, setText] = useState("")

    const {getToken, setError} = useAuth();
    const nav = useNavigate();

    const {id} = useParams();

    const fetchFromPathHistoryId = useCallback(()=>{
        if(id){
            console.log("have id in primary: "+ id);
            apiGetHistoryEntryById(getToken(), id)
                .then(h=>{
                    console.log(h);
                    setText(h.text);
                    apiSendTextToBackend(getToken(), {text: h.text})
                        .then(textMetadataResponse=>{
                            setAudioFile(null);
                            textMetadataResponse.defaultIds = h.choices;
                            setTextMetadataResponse(textMetadataResponse);
                        })
                })
            ;
        }
    }, [getToken, id])

    useEffect(()=>{
        fetchFromPathHistoryId();
    }, [fetchFromPathHistoryId]);

    useEffect(() => {
        if (!getToken()) {
            nav("/login")
        }
    }, [getToken, nav])

    function submitText(){
        apiSendTextToBackend(getToken(), {text: text})
            .then(r=>{
                console.log(r);
                setTextMetadataResponse(r);
                setAudioFile(null);
                return r;
            });
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
                <TextSubmit text={text} setText={setText} submitText={submitText}/>
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