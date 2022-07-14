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
        .then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'file.wav'); //or any other extension
            document.body.appendChild(link);
            link.click();
        })
}
