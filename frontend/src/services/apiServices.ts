import axios, {AxiosResponse} from "axios";
import {WordResponse, TextSend} from "./model";


export function apiSendTextToBackend(text: TextSend) {
    return axios.put("/api/main", text)
        .then((response: AxiosResponse<WordResponse[]>) => response.data);
}

export function apiGetAudio(words: WordResponse[]) {
    return axios.post("/api/main/audio",
        words,
        {
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}


export function apiSaveAudio(word: string, creator: string, tag: string, audioBlob: Blob) {
    const formData = new FormData();

    formData.append("word", word);
    formData.append("creator", creator);
    formData.append("tag", tag);
    formData.append("audio", audioBlob);

    return axios.post("/api/addword",
        formData,
        {headers: {"Content-Type": "multipart/form-data"}}
    )
}

