import axios, {AxiosResponse} from "axios";
import {TextSend, TextMetadataResponse, LoginDTO, LoginResponse, RegisterDTO, RecordPage, RecordInfo} from "./model";

function createHeaders(token: string) {
    return {
        headers: {Authorization: `Bearer ${token}`}
    }
}

export function sendRegister(user: RegisterDTO) {
    return axios.post("/api/auth/register", user)
        .then(r => r.data);
}

export function sendLogin(user: LoginDTO) {
    return axios.post("/api/auth/login", user)
        .then((response: AxiosResponse<LoginResponse>) => response.data)
}

export function apiSendTextToBackend(token: string, text: TextSend) {
    return axios.post("/api/primary",
        text,
        createHeaders(token)
    )
        .then((response: AxiosResponse<TextMetadataResponse>) => response.data);
}

export function apiGetMergedAudio(token: string, ids: string[]) {
    return axios.post("/api/primary/audio",
        ids,
        {
            headers: {Authorization: `Bearer ${token}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}

export function apiGetSingleRecordedAudio(token: string, id: string) {
    return axios.get(`/api/record/audio/${id}`,
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

    return axios.post("/api/record",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
                Authorization: `Bearer ${token}`
            }
        }
    )
}

export function apiGetRecordPage(token: string, page: number, size: number, searchTerm: string) {
    return axios.get(`/api/record/${page}/${size}?searchTerm=${searchTerm}`,
        createHeaders(token)
    )
        .then((response: AxiosResponse<RecordPage>) => response.data);
}

export function apiDeleteRecord(token: string, id: string) {
    return axios.delete(`/api/record/${id}`,
        createHeaders(token)
    );
}
export function apiChangeRecord(token: string, recordInfo: RecordInfo) {
    return axios.put(`/api/record`,
        recordInfo,
        createHeaders(token)
    );
}
export function apiGetHistory(token: string){
    return axios.get(`/api/userdetails/history`,
        createHeaders(token)
    ).then(r=>r.data)
        .then(h=>{
            let locHist = [...h];
            locHist.map(h=>{
                h.requestTime = parseISOString(h.requestTime);
                return h;
            })
            return locHist;
        })
        ;
}

// LocalDateTime from java has been converted to String for the request, this creates a js Date from it
function parseISOString(s: string) {
    const b = s.split(/\D+/);
    let month = Number(b[1]);
    return new Date(Date.UTC(Number(b[0]), --month, Number(b[2]), Number(b[3]), Number(b[4]), Number(b[5]), Number(b[6])));
}