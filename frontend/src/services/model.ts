
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
    token : string,
    username : string,
    roles : string[],
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