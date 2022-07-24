import axios, {AxiosResponse} from "axios";
import {TextSend, TextResponse, UserDTO, LoginResponse, UserRegisterDTO} from "./model";

function createHeaders(token: string){
    return {
        headers: {Authorization: `Bearer ${token}`}
    }
}

export function sendRegister(user: UserRegisterDTO) {
    return axios.post("/api/auth/register", user)
        .then(r => r.data);
}

export function sendLogin(user: UserDTO) {
    return axios.post("/api/auth/login", user)
        .then((response: AxiosResponse<LoginResponse>) => response.data)
}

export function apiSendTextToBackend(token: string, text: TextSend) {
    return axios.post("/api/main",
        text,
        createHeaders(token)
        )
        .then((response: AxiosResponse<TextResponse>) => response.data);
}

export function apiGetAudio(token: string, ids: string[]) {
    return axios.post("/api/main/audio",
        ids,
        {
            headers: {Authorization: `Bearer ${token}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}


export function apiSaveAudio(token: string, word: string, tag: string, accessibility: string, audioBlob: Blob) {
    const formData = new FormData();

    formData.append("word", word);
    formData.append("tag", tag);
    formData.append("accessibility", accessibility);
    formData.append("audio", audioBlob);

    return axios.post("/api/word",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: `Bearer ${token}`
            }
        }
    )
}

export function apiGetRecordPage(page: number, size: number, searchTerm: string) {
    return axios.get("/api/word",
    {page, size, searchTerm},

)
}