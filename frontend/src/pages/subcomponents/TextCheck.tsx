import {
    Box,
    Button,
    FormControl,
    Grid,
    InputLabel,
    MenuItem,
    Select,
    SelectChangeEvent,
    Typography
} from "@mui/material";
import {TextResponse, WordAvail} from "../../services/model";
import {isAvailable} from "./helpers";
import {useNavigate} from "react-router-dom";

interface TextCheckProps {
    splitText: TextResponse,
    ids: string[],
    setIds: (ids: string[])=>void
}

export default function TextCheck(props: TextCheckProps) {

    const nav = useNavigate();

    function chooseWord(event: SelectChangeEvent) {
        console.log(event.target.value);
    }

    function getWordButton(word: WordAvail, index: number) {
        let myColor: string = "#fff";
        let myTextDecoration: string = "none";

        if (word.availability === "PUBLIC") {
            myColor = "#12670c";
        } else if (word.availability === "INVALID") {
            myColor = "#881111";
            myTextDecoration = "line-through";
        } else if (word.availability === "ABSENT") {
            myColor = "#b43535";
        }

        let firstVal;
        if(isAvailable(word.availability) && props.splitText.wordMap[word.word].length>0){
            firstVal = props.splitText.wordMap[word.word].at(0)!.id;
        }else{
            firstVal = "";
        }

        return (
            <Box sx={{backgroundColor: myColor}}>
                <FormControl variant="filled" size="small">
                    <InputLabel id="demo-simple-select-label" >
                        {word.word}
                    </InputLabel>
                    <Select
                        variant={"filled"}
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        autoWidth
                        defaultValue={firstVal}
                        value={firstVal}
                        label="word"
                        onChange={chooseWord}
                    >
                        {isAvailable(word.availability) ?
                            props.splitText.wordMap[word.word].map(wmd =>
                                <MenuItem key={wmd.id} value={wmd.id}>
                                    {wmd.creator} - {wmd.tag}
                                </MenuItem>
                            )
                            :
                            <MenuItem>
                                <Button value={""} onClick={()=>nav("/record")}>
                                    record
                                </Button>
                            </MenuItem>
                        }
                    </Select>
                </FormControl>
            </Box>
        )
    }

    return (
        <Grid container justifyContent={"center"}>
            {
                props.splitText &&
                props.splitText!.textWords.map((r, i) =>
                    <Grid item key={i} margin={0.5}>
                        <Typography hidden={true}>{r.word}</Typography>
                        {getWordButton(r, i)}
                    </Grid>
                )}
        </Grid>
    )
}