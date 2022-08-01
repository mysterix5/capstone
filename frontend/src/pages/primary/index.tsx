import {Button, Grid} from "@mui/material";
import TextSubmit from "./TextSubmit";
import {useEffect, useState} from "react";
import {RecordMetaData, TextMetadataResponse} from "../../services/model";
import TextCheck from "./TextCheck";
import Audio from "./Audio";
import {apiGetHistoryEntryById, apiGetMergedAudio, apiSendTextToBackend} from "../../services/apiServices";
import {isAvailable} from "../../globalTools/helpers";
import {useAuth} from "../../usermanagement/AuthProvider";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";

function createSearchParamsFromArrayToArray(name: string, array: string[]) {
    let returnString: string = "";
    for (const arg of array) {
        if (returnString) {
            returnString += `&${name}=${arg}`;
        } else {
            returnString += `${name}=${arg}`;
        }
    }
    return returnString;
}

function createSearchParamsFromArrayToText(name: string, array: string[]) {
    return `${name}=${array.join("+")}`;
}


const initialMetadataResponse = {
    textWords: [],
    defaultIds: [],
    wordRecordMap: {}
};

export default function Primary() {
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>(initialMetadataResponse);
    const [audioFile, setAudioFile] = useState<any>();
    const [text, setText] = useState("")

    const {setError, defaultApiResponseChecks} = useAuth();
    const {historyId} = useParams();

    const nav = useNavigate();
    const [searchParams] = useSearchParams();

    function isIdInChoices(theId: string, theChoices: RecordMetaData[]) {
        for (const recordMD of theChoices) {
            if (theId === recordMD.id) {
                return true
            }
        }
        return false;
    }

    useEffect(() => {
        if (historyId) {
            console.log("have history id in primary: " + historyId);
            apiGetHistoryEntryById(historyId)
                .then(h => {
                    console.log(h);
                    setText(h.text);
                    apiSendTextToBackend({text: h.text})
                        .then(textMetadataResponseFromBackend => {
                            setAudioFile(null);
                            console.log(textMetadataResponseFromBackend);
                            for (let i = 0; i < textMetadataResponseFromBackend.textWords.length; i++) {
                                const choice = h.choices[i];
                                const word = textMetadataResponseFromBackend.textWords[i];
                                if (isAvailable(word.availability)) {
                                    const actualWordChoices = textMetadataResponseFromBackend.wordRecordMap[word.word];
                                    if (!isIdInChoices(choice, actualWordChoices)) {
                                        textMetadataResponseFromBackend.defaultIds[i] = actualWordChoices[0].id;
                                        console.log("change stuff")
                                        setError({
                                            message: "for some words the selected record is no longer available and was changed to default",
                                            subMessages: []
                                        });
                                    } else {
                                        textMetadataResponseFromBackend.defaultIds[i] = choice;
                                    }
                                } else {
                                    textMetadataResponseFromBackend.defaultIds[i] = "";
                                    setError({message: "for some words are no recordings available", subMessages: []});
                                }
                            }
                            setTextMetadataResponse(textMetadataResponseFromBackend);
                        }).catch(err => {
                        defaultApiResponseChecks(err);
                    });
                }).catch(err => {
                defaultApiResponseChecks(err);
            });
        }else if(searchParams.get("text")){
            setText(searchParams.get("text")!);
            apiSendTextToBackend({text: searchParams.get("text")!})
                .then(r => {
                    console.log(r);
                    setTextMetadataResponse(r);
                }).catch(err => {
                    defaultApiResponseChecks(err);
                }
            );
        }
    }, [historyId, setError, defaultApiResponseChecks, searchParams])

    useEffect(() => {
        if (!localStorage.getItem("jwt")) {
            nav("/login")
        }
    }, [nav])

    function submitText() {
        apiSendTextToBackend({text: text})
            .then(r => {
                console.log(r);
                setTextMetadataResponse(r);
                setAudioFile(null);
            }).catch(err => {
                defaultApiResponseChecks(err);
            }
        );
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
        apiGetMergedAudio(textMetadataResponse?.defaultIds!)
            .then(setAudioFile)
            .catch((err) => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    const enc = new TextDecoder('utf-8')
                    const res = JSON.parse(enc.decode(new Uint8Array(err.response.data)))
                    setError(res);
                }
            });
    }

    function setId(choiceId: string, index: number) {
        if (textMetadataResponse.defaultIds[index] !== choiceId) {
            setTextMetadataResponse(current => {
                let tmp = {...current};
                tmp.defaultIds[index] = choiceId;
                return tmp;
            });
        }
    }

    function recordMissingWords() {
        const wordsArray = textMetadataResponse.textWords
            .filter((wordAvail) => !isAvailable(wordAvail.availability))
            .filter((wordAvail) => wordAvail.availability!=="INVALID")
            .map(wordAvail => wordAvail.word);
        const searchParamRecordWords = createSearchParamsFromArrayToArray("words", wordsArray);
        const searchParamText = createSearchParamsFromArrayToText("text",
            textMetadataResponse.textWords.map(wordAvail => wordAvail.word));
        nav("/batch?" + searchParamRecordWords + "&" + searchParamText);
    }

    function recordAllWords() {
        const wordsArray = textMetadataResponse.textWords
            .filter((wordAvail) => wordAvail.availability!=="INVALID")
            .map(wordAvail => wordAvail.word);
        const searchParamRecordWords = createSearchParamsFromArrayToArray("words", wordsArray);
        const searchParamText = createSearchParamsFromArrayToText("text",
            textMetadataResponse.textWords.map(wordAvail => wordAvail.word));
        nav("/batch?" + searchParamRecordWords + "&" + searchParamText);
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
                <Button onClick={recordAllWords}>
                    record all words
                </Button>
            </Grid>
            <Grid item>
                {
                    (textMetadataResponse && checkTextResponseAvailability()) ?
                        <Audio getAudio={getAudio} audioFile={audioFile}/>
                        :
                        <Button onClick={recordMissingWords}>
                            record missing words
                        </Button>
                }
            </Grid>
        </Grid>
    )
}