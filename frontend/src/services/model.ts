
export interface TextSend{
    text: string
}

export interface WordMap {
    [key: string]: WordMetaData[]
}

export interface WordMetaData {
    id: string,
    word: string,
    creator: string,
    tag: string
}

export interface WordAvail {
    word: string,
    availability: string
}

export interface TextResponse {
    textWords: WordAvail[],
    wordMap: WordMap
}

export interface AuthInterface {
    username : string,
    roles : string[],
    getToken: () => string,
    error: VoverError,
    setError: (error: VoverError)=>void,
    logout: () => void
    login: (token: string) => void
}

export interface LoginResponse {
    token: string;
}

export interface UserDTO{
    username: string,
    password: string
}

export interface UserRegisterDTO{
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
