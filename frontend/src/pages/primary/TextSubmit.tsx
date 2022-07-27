import {Box, Button, Grid, TextField} from "@mui/material";
import {FormEvent, useState} from "react";
import {apiSendTextToBackend} from "../../services/apiServices";
import {TextMetadataResponse} from "../../services/model";
import {useAuth} from "../../usermanagement/AuthProvider";

interface TextSubmitProps{
    setTextMetadataResponse: (textResponse: TextMetadataResponse)=>void
}

export default function TextSubmit(props: TextSubmitProps){

    const [text, setText] = useState("");
    const {getToken} = useAuth();

    const sendTextToBackend = (event: FormEvent) => {
        event.preventDefault();
        console.log("send text to backend");
        apiSendTextToBackend(getToken(), {text})
            .then(r=>{
                console.log(r);
                props.setTextMetadataResponse(r);
                return r;
            });
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