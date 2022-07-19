import {useNavigate} from "react-router-dom";
import {Button, Grid, Typography} from "@mui/material";
import MicIcon from '@mui/icons-material/Mic';

export default function Header() {
    const nav = useNavigate();
    return (
        <Grid container justifyContent={"center"}>
            <Grid item xs={11} border={0.1} textAlign={"center"}
                  borderColor={"lightgrey"} borderRadius={2} marginTop={1} marginLeft={1} marginRight={1} marginBottom={2}>
                <Button onClick={() => nav("/")}>
                    <Typography variant={"h3"} color={"lightseagreen"}>{process.env.REACT_APP_APPLICATION_NAME}</Typography>
                </Button>

                <Button onClick={() => nav("/record")}>
                    <MicIcon/>
                </Button>
            </Grid>
        </Grid>
    )
}