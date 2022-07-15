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