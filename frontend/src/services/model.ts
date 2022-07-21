
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
