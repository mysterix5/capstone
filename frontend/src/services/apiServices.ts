import axios, {AxiosResponse} from "axios";
import {
    TextSend,
    TextMetadataResponse,
    LoginDTO,
    LoginResponse,
    RegisterDTO,
    RecordPage,
    RecordInfo,
    HistoryEntryTextChoices
} from "./model";

function createHeaders() {
    return {
        headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`}
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


export function apiSendTextToBackend(text: TextSend) {
    return axios.post("/api/primary",
        text,
        createHeaders()
    )
        .then((response: AxiosResponse<TextMetadataResponse>) => response.data);
}

export function apiGetMergedAudio(ids: string[]) {
    return axios.post("/api/primary/audio",
        ids,
        {
            headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}

export function apiGetSingleRecordedAudio(id: string) {
    return axios.get(`/api/record/audio/${id}`,
        {
            headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => window.URL.createObjectURL(new Blob([data])));
}


export function apiSaveAudio(word: string, tag: string, accessibility: string, audioBlob: Blob) {
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
                Authorization: `Bearer ${localStorage.getItem('jwt')}`
            }
        }
    )
}

export function apiGetRecordPage(page: number, size: number, searchTerm: string) {
    return axios.get(`/api/record/${page}/${size}?searchTerm=${searchTerm}`,
        createHeaders()
    )
        .then((response: AxiosResponse<RecordPage>) => response.data);
}

export function apiDeleteRecord(id: string) {
    return axios.delete(`/api/record/${id}`,
        createHeaders()
    );
}

export function apiChangeRecord(recordInfo: RecordInfo) {
    return axios.put(`/api/record`,
        recordInfo,
        createHeaders()
    );
}

export function apiGetHistory() {
    return axios.get(`/api/userdetails/history`,
        createHeaders()
    ).then(r => r.data)
        .then(h => {
            let locHist = [...h];
            locHist.map(h => {
                h.requestTime = parseISOString(h.requestTime);
                return h;
            })
            return locHist;
        })
        ;
}

export function apiGetHistoryEntryById(id: string) {
    console.log(`get: /api/history/${id}`);
    return axios.get(`/api/history/${id}`,
        createHeaders()
    ).then((response: AxiosResponse<HistoryEntryTextChoices>) => response.data);
}


// LocalDateTime from java has been converted to String for the request, this creates a js Date from it
function parseISOString(s: string) {
    const b = s.split(/\D+/);
    let month = Number(b[1]);
    return new Date(Date.UTC(Number(b[0]), --month, Number(b[2]), Number(b[3]), Number(b[4]), Number(b[5]), Number(b[6])));
}