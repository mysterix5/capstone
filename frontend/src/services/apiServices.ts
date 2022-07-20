import axios, {AxiosResponse} from "axios";
import {TextSend, TextResponse} from "./model";

export function apiSendTextToBackend(text: TextSend) {
    return axios.post("/api/main", text)
        .then((response: AxiosResponse<TextResponse>) => response.data);
}

export function apiGetAudio(ids: string[]) {
    return axios.post("/api/main/audio",
        ids,
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

