import axios, {AxiosResponse} from "axios";
import {TextSend, TextResponse, UserDTO, LoginResponse} from "./model";

export function sendRegister(user: UserDTO) {
    return axios.post("/api/auth/register", user)
        .then(r => r.data);
}

export function sendLogin(user: UserDTO) {
    return axios.post("/api/auth/login", user)
        .then((response: AxiosResponse<LoginResponse>) => response.data)
}


export function apiSendTextToBackend(text: TextSend) {
    return axios.post("/api/main",
        text,
        {headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`}})
        .then((response: AxiosResponse<TextResponse>) => response.data);
}

export function apiGetAudio(ids: string[]) {
    return axios.post("/api/main/audio",
        ids,
        {
            headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
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
        {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: `Bearer ${localStorage.getItem('jwt')}`
            }
        }
    )
}

