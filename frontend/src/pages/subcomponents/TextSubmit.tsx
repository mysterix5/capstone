import {Box, Button, Grid, TextField} from "@mui/material";
import {FormEvent, useState} from "react";
import {apiSendTextToBackend} from "../../services/apiServices";
import {TextResponse} from "../../services/model";

interface TextSubmitProps{
    setSplitText: (textResponse: TextResponse)=>void,
    setIds: (ids: string[])=>void
}

export default function TextSubmit(props: TextSubmitProps){

    const [text, setText] = useState("");

    const sendTextToBackend = (event: FormEvent) => {
        event.preventDefault();
        console.log("send text to backend");
        apiSendTextToBackend({text})
            .then(r=>{
                console.log(r);
                props.setSplitText(r);
                return r;
            })
            .then(textResponse => {
                const ids: string[] = [];
                for(const word of textResponse.textWords){
                    const mdl = textResponse.wordMap[word.word];
                    if(mdl && mdl.length>0){
                        ids.push(mdl.at(0)!.id);
                    }else{
                        ids.push('');
                    }
                }
                props.setIds(ids);
            })
        ;
    }

    return (
        <Box component={"form"} onSubmit={sendTextToBackend}>
            <Grid container alignItems="center">
                <Grid item margin={2} sx={{boxShadow: 5}}>
                    <TextField
                        id="outlined-multiline-static"
                        label="text to audio"
                        multiline
                        rows={4}
                        value={text}
                        margin={"normal"}
                        sx={{margin: 1}}
                        onChange={event => setText(event.target.value)}
                    />
                </Grid>

                <Grid item>
                    <Button
                        type="submit"
                        variant="contained"
                    >
                        send
                    </Button>
                </Grid>
            </Grid>
        </Box>
    );
}