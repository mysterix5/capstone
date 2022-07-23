import axios, {AxiosResponse} from "axios";
import {TextSend, TextResponse, UserDTO, LoginResponse, UserRegisterDTO} from "./model";

function createHeaders(){
    return {
        headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`}
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


export function apiSendTextToBackend(text: TextSend) {
    return axios.post("/api/main",
        text,
        createHeaders()
        )
        .then((response: AxiosResponse<TextResponse>) => response.data);
}

export function apiGetAudio(ids: string[]) {
    return axios.post("/api/main/audio",
        ids,
        createHeaders()
    )
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}


export function apiSaveAudio(word: string, tag: string, accessibility: string, audioBlob: Blob) {
    const formData = new FormData();

    formData.append("word", word);
    formData.append("tag", tag);
    formData.append("accessibility", accessibility);
    formData.append("audio", audioBlob);

    return axios.post("/api/addword",
        formData,
        createHeaders()
    );
}

