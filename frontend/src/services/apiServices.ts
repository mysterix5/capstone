import axios, {AxiosResponse} from "axios";
import {
    TextSubmit,
    TextMetadataResponse,
    LoginDTO,
    LoginResponse,
    RegisterDTO,
    RecordPage,
    RecordInfo,
    HistoryEntryTextChoices, UserDTO, AllUsersForFriendPageResponse, FriendsAndScope
} from "./model";

function createHeaders() {
    return {
        headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`}
    }
}

export function sendRegister(user: RegisterDTO) {
    const url = `/api/auth/register`;
    console.log(`post: ${url}: user=${user}`);
    return axios.post(url, user)
        .then(r => r.data);
}

export function sendLogin(user: LoginDTO) {
    const url = `/api/auth/login`;
    console.log(`post: ${url}: user=${user}`);
    return axios.post(url, user)
        .then((response: AxiosResponse<LoginResponse>) => response.data)
}


export function apiSubmitTextToBackend(textSubmit: TextSubmit) {
    const url = `/api/primary/textsubmit`;
    console.log(`post: ${url}: text=${textSubmit}`);
    return axios.post(url,
        textSubmit,
        createHeaders()
    )
        .then((response: AxiosResponse<TextMetadataResponse>) => response.data);
}

export function apiGetMergedAudio(ids: string[]) {
    const url = `/api/primary/getaudio`;
    console.log(`post: ${url}: ids=${ids}`);
    return axios.post(url,
        ids,
        {
            headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data);
}

export function apiGetSingleRecordedAudio(id: string) {
    const url = `/api/record/audio/${id}`;
    console.log(`get: ${url}`);
    return axios.get(url,
        {
            headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
            responseType: 'arraybuffer'
        })
        .then((response) => response.data)
        .then(data => new Blob([data]));
}
//
// export function apiGetSingleRecordedAudio(id: string) {
//     const url = `/api/record/audio/${id}`;
//     console.log(`get: ${url}`);
//     return axios.get(url,
//         {
//             headers: {Authorization: `Bearer ${localStorage.getItem('jwt')}`},
//             responseType: 'arraybuffer'
//         })
//         .then((response) => response.data)
//         .then(data => window.URL.createObjectURL(new Blob([data])));
// }


export function apiSaveAudio(word: string, tag: string, accessibility: string, audioBlob: Blob) {
    const formData = new FormData();

    formData.append("word", word);
    formData.append("tag", tag);
    formData.append("accessibility", accessibility);
    formData.append("audio", audioBlob);

    const url = `/api/record`;
    console.log(`post: ${url}: formdata: word=${word}, tag=${tag}, accessibility=${accessibility}, audiodata`);

    return axios.post(url,
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
    const url = `/api/record/${page}/${size}?searchTerm=${searchTerm}`;
    console.log(`get: ${url}`);
    return axios.get(url,
        createHeaders()
    )
        .then((response: AxiosResponse<RecordPage>) => response.data);
}

export function apiDeleteRecord(id: string) {
    const url = `/api/record/${id}`;
    console.log(`delete: ${url}`);
    return axios.delete(url,
        createHeaders()
    );
}

export function apiChangeRecord(recordInfo: RecordInfo) {
    const url = `/api/record`;
    console.log(`put: ${url}: recordInfo=${recordInfo}`);
    return axios.put(url,
        recordInfo,
        createHeaders()
    );
}

export function apiGetHistory() {
    const url = `/api/userdetails/history`;
    console.log(`get: ${url}`);
    return axios.get(url,
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
    const url = `/api/history/${id}`;
    console.log(`get: ${url}`);
    return axios.get(url,
        createHeaders()
    ).then((response: AxiosResponse<HistoryEntryTextChoices>) => response.data);
}

export function apiGetUsers() {
    const url = `/api/userdetails/friendsinfo`;
    console.log(`get: ${url}`);
    return axios.get(url,
        createHeaders()
    ).then((response: AxiosResponse<AllUsersForFriendPageResponse>) => response.data);
}

export function apiGetFriendsAndScope() {
    const url = `/api/userdetails/friendsandscope`;
    console.log(`get: ${url}`);
    return axios.get(url,
        createHeaders()
    ).then((response: AxiosResponse<FriendsAndScope>) => response.data);
}

export function apiSendFriendRequest(username: string) {
    const url = `/api/userdetails/friendrequest`;
    console.log(`post: ${url}: username=${username}`);
    return axios.post(url,
        username,
        {
            headers: {
                "Content-Type": "text/plain",
                Authorization: `Bearer ${localStorage.getItem('jwt')}`
            }
        }
    ).then((response: AxiosResponse<UserDTO[]>) => response.data);
}

export function apiAcceptFriendship(username: string) {
    const url = `/api/userdetails/acceptfriend`;
    console.log(`put: ${url}: username=${username}`);
    return axios.put(url,
        username,
        {
            headers: {
                "Content-Type": "text/plain",
                Authorization: `Bearer ${localStorage.getItem('jwt')}`
            }
        }
    );
}

export function apiEndFriendship(username: string) {
    const url = `/api/userdetails/endfriendship`;
    console.log(`put: ${url}: username=${username}`);
    return axios.put(url,
        username,
        {
            headers: {
                "Content-Type": "text/plain",
                Authorization: `Bearer ${localStorage.getItem('jwt')}`
            }
        }
    );
}

// LocalDateTime from java has been converted to String for the request, this creates a js Date from it
function parseISOString(s: string) {
    const b = s.split(/\D+/);
    let month = Number(b[1]);
    return new Date(Date.UTC(Number(b[0]), --month, Number(b[2]), Number(b[3]), Number(b[4]), Number(b[5]), Number(b[6])));
}
