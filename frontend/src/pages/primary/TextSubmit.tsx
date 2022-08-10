import {Box, Button, Grid, TextField} from "@mui/material";
import {FormEvent} from "react";

interface TextSubmitProps{
    text: string,
    setText: (s: string) => void,
    submitText: ()=>void
}

export default function TextSubmit(props: TextSubmitProps){

    const sendTextToBackend = (event: FormEvent) => {
        event.preventDefault();
        console.log("send text to backend");
        props.submitText();
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
                        value={props.text}
                        margin={"normal"}
                        sx={{margin: 1}}
                        onChange={event => props.setText(event.target.value)}
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