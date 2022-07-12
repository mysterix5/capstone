import axios, {AxiosResponse} from "axios";
import {WordResponse, TextSend} from "./model";


export function apiSendTextToBackend(text: TextSend){
    return axios.put("/api/main", text)
        .then((response: AxiosResponse<WordResponse[]>)=>response.data);
}