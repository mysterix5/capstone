import {AxiosError} from "axios";

export interface TextSend{
    text: string
}

export interface WordRecordMap {
    [key: string]: RecordMetaData[]
}

export interface RecordMetaData {
    id: string,
    word: string,
    creator: string,
    tag: string
}

export interface WordAvail {
    word: string,
    availability: string
}

export interface TextMetadataResponse {
    textWords: WordAvail[],
    wordRecordMap: WordRecordMap,
    defaultIds: string[]
}

export interface AuthInterface {
    username : string,
    roles : string[],
    error: VoverError,
    setError: (error: VoverError)=>void,
    logout: () => void,
    login: (token: string) => void,
    defaultApiResponseChecks: (err: Error | AxiosError) => void
}

export interface LoginResponse {
    token: string;
}

export interface LoginDTO {
    username: string,
    password: string
}

export interface RegisterDTO {
    username: string,
    password: string,
    passwordRepeat: string
}

export interface VoverError {
    message: string,
    subMessages: string[]
}

export interface RecordPage {
    page: number,
    noPages: number,
    size: number,
    searchTerm: string,
    records: RecordInfo[],
    accessibilityChoices: string[]
}

export interface RecordInfo {
    id: string,
    word: string,
    tag: string,
    accessibility: string
}

export interface HistoryEntryTextDate {
    id: string,
    text: string,
    requestTime: Date
}
export interface HistoryEntryTextChoices {
    text: string,
    choices: string[]
}