import {Button, Grid} from "@mui/material";
import TextSubmit from "./TextSubmit";
import {useEffect, useState} from "react";
import {RecordMetaData, TextMetadataResponse} from "../../services/model";
import TextCheck from "./TextCheck";
import Audio from "./Audio";
import {
    apiGetFriendsAndScope,
    apiGetHistoryEntryById,
    apiGetMergedAudio,
    apiSubmitTextToBackend
} from "../../services/apiServices";
import {useAuth} from "../../usermanagement/AuthProvider";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import Share from "./Share";
import Scope from "./Scope";

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
    // TODO friends and scope will be working with the next commit
    // eslint-disable-next-line
    const [friends, setFriends] = useState<string[]>([]);
    const [scope, setScope] = useState<string[]>([]);

    const [text, setText] = useState("")
    const [textMetadataResponse, setTextMetadataResponse] = useState<TextMetadataResponse>(initialMetadataResponse);
    const [audioBlobPart, setAudioBlobPart] = useState<BlobPart>();

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
                    apiSubmitTextToBackend({text: h.text, scope: scope})
                        .then(textMetadataResponseFromBackend => {
                            removeDeprecatedContent(2);
                            console.log(textMetadataResponseFromBackend);
                            for (let i = 0; i < textMetadataResponseFromBackend.textWords.length; i++) {
                                const choice = h.choices[i];
                                const word = textMetadataResponseFromBackend.textWords[i];
                                if (word.availability==="AVAILABLE") {
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
        } else if (searchParams.get("text")) {
            setText(searchParams.get("text")!);
            apiSubmitTextToBackend({text: searchParams.get("text")!, scope: scope})
                .then(r => {
                    setTextMetadataResponse(r);
                }).catch(err => {
                    defaultApiResponseChecks(err);
                }
            );
        }
    }, [historyId, setError, defaultApiResponseChecks, searchParams, scope])

    useEffect(() => {
        if (!localStorage.getItem("jwt")) {
            nav("/login")
        }
    }, [nav])

    useEffect(() => {
        apiGetFriendsAndScope()
            .then(fs => {
                // TODO change this after implementing scopes in frontend
                setFriends(fs.friends);
                setScope(fs.scope);
            })
            .catch(err => {
                defaultApiResponseChecks(err);
            });
    }, [defaultApiResponseChecks])

    useEffect(() => {
        removeDeprecatedContent(1);
    }, [text])

    /**
     * removes content that is deprecated
     * <p>level == 0 -> removes text and following (everything)
     * <p>level == 1 -> removes text check and following
     * <p>level == 2 -> removes audio
     * @param level
     */
    function removeDeprecatedContent(level: number) {
        if(level<1) {
            setText("");
        }
        if(level<2) {
            setTextMetadataResponse(initialMetadataResponse);
        }
        if(level<3) {
            setAudioBlobPart(undefined);
        }
    }

    function submitText() {
        removeDeprecatedContent(1)
        apiSubmitTextToBackend({text: text, scope: scope})
            .then(r => {
                console.log(r);
                setTextMetadataResponse(() => r);
            }).catch(err => {
                defaultApiResponseChecks(err);
                if (err.response) {
                    setError(err.response.data);
                }
            }
        );
    }

    function checkTextResponseAvailability() {
        if (!textMetadataResponse || textMetadataResponse.textWords.length === 0) {
            return false;
        }
        for (const word of textMetadataResponse!.textWords) {
            if (!(word.availability==="AVAILABLE")) {
                return false;
            }
        }
        return true;
    }

    function getAudio() {
        apiGetMergedAudio(textMetadataResponse?.defaultIds!)
            .then(setAudioBlobPart)
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
            removeDeprecatedContent(1);
            setTextMetadataResponse(current => {
                let tmp = {...current};
                tmp.defaultIds[index] = choiceId;
                return tmp;
            });
        }
    }

    function recordMissingWords() {
        const wordsArray = textMetadataResponse.textWords
            .filter((wordAvail) => !(wordAvail.availability==="AVAILABLE"))
            .filter((wordAvail) => wordAvail.availability !== "INVALID")
            .map(wordAvail => wordAvail.word);
        const searchParamRecordWords = createSearchParamsFromArrayToArray("words", wordsArray);
        const searchParamText = createSearchParamsFromArrayToText("text",
            textMetadataResponse.textWords.map(wordAvail => wordAvail.word));
        nav("/batch?" + searchParamRecordWords + "&" + searchParamText);
    }

    function recordAllWords() {
        const wordsArray = textMetadataResponse.textWords
            .filter((wordAvail) => wordAvail.availability !== "INVALID")
            .map(wordAvail => wordAvail.word);
        const searchParamRecordWords = createSearchParamsFromArrayToArray("words", wordsArray);
        const searchParamText = createSearchParamsFromArrayToText("text",
            textMetadataResponse.textWords.map(wordAvail => wordAvail.word));
        nav("/batch?" + searchParamRecordWords + "&" + searchParamText);
    }

    function recordSingleWord(dropdownWord: string) {
        const searchParamRecordWords = createSearchParamsFromArrayToArray("words", [dropdownWord]);
        const searchParamText = createSearchParamsFromArrayToText("text",
            textMetadataResponse.textWords.map(wordAvail => wordAvail.word));
        nav("/batch?" + searchParamRecordWords + "&" + searchParamText);
    }

    return (
        <Grid container alignItems={"center"} flexDirection={"column"}>
            <Grid item>
                <Scope friends={friends} scope={scope} setScope={setScope}/>
            </Grid>
            <Grid item>
                <TextSubmit text={text} setText={setText} submitText={submitText}/>
            </Grid>
            <Grid item ml={2} mr={2}>
                {
                    textMetadataResponse &&
                    <TextCheck
                        key={"textcheck"}
                        textMetadataResponse={textMetadataResponse}
                        setId={setId}
                        singleWordRecord={recordSingleWord}
                    />
                }
            </Grid>
            {textMetadataResponse && textMetadataResponse.textWords.length !== 0 &&
                <>
                    <Grid item>
                        <Button onClick={recordAllWords}>
                            record all words
                        </Button>
                    </Grid>
                    <Grid item>
                        {
                            checkTextResponseAvailability() ?
                                <Audio
                                    getAudio={getAudio}
                                    audioBlobPart={audioBlobPart!}
                                />
                                :
                                <Button onClick={recordMissingWords}>
                                    record missing words
                                </Button>
                        }
                    </Grid>
                </>
            }
            <Grid item>
                {audioBlobPart &&
                    <Share
                        audioBlobPart={new File(
                            [audioBlobPart!],
                            'vover.mp3',
                            {type: 'audio/mp3'})}
                    />
                }
            </Grid>
        </Grid>
    )
}