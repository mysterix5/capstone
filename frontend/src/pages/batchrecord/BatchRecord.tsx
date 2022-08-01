import {Box, Button} from "@mui/material";
import {useEffect} from "react";
import {useNavigate, useSearchParams} from "react-router-dom";


export default function BatchRecord() {
    const [searchParams, setSearchParams] = useSearchParams();

    const nav = useNavigate();

    function createSearchParamsFromArrayToArray(name: string, array: string[]) {
        let returnString: string = "";
        for(const arg of array) {
            if(returnString){
                returnString += `&${name}=${arg}`;
            }else{
                returnString += `${name}=${arg}`;
            }
        }
        return returnString;
    }

    function createSearchParamsFromArrayToText(name: string, array: string[]){
        return `${name}=${array.join("+")}`;
    }

    const wordArray = [
        "eins",
        "zwei",
        "drei"
    ]

    useEffect(() => {
        console.log(searchParams.getAll("words"));
        searchParams.getAll("words")
    }, [])

    return (
        <Box>
            Batch record
            <div>
                {searchParams.getAll("words").map((sp)=><div>{sp}</div>)}
            </div>
            <div>
                {searchParams.get("text")}
            </div>
            <Button onClick={()=>nav(`/batch?${createSearchParamsFromArrayToArray("words", wordArray)}&${createSearchParamsFromArrayToText("text", wordArray)}`)}>
                click
            </Button>

        </Box>
    )
}