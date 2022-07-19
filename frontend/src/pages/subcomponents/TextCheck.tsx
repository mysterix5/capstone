import {Button, Grid} from "@mui/material";
import {TextResponse, WordAvail} from "../../services/model";

interface TextCheckProps {
    splitText: TextResponse
}

export default function TextCheck(props: TextCheckProps) {

    function getWordButton(word: WordAvail) {
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

        return (
            <Button variant={"contained"}
                        size={"small"}
                        sx={{color: "#000",
                            backgroundColor: myColor,
                        textDecoration: myTextDecoration}}>
                {word.word}
            </Button>
        )
    }

    return (
        <Grid container justifyContent={"center"}>
            {
                props.splitText &&
                props.splitText!.textWords.map((r, i) =>
                    <Grid item key={i} margin={0.5}>
                        {getWordButton(r)}
                    </Grid>
                )}
        </Grid>
    )
}