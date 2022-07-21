import {AccountCircle, Key} from "@mui/icons-material";
import {Box, Button, Grid, InputAdornment, TextField, Typography} from "@mui/material";
import {FormEvent, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "./AuthProvider";
import {sendLogin} from "../services/apiServices";

export default function LoginPage() {

    const {token, login} = useAuth();

    const nav = useNavigate();

    useEffect(() => {
            if (token) {
                nav("/")
            }
        }
        , [token, nav]);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");


    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();
        sendLogin({username, password})
            .then(r => login(r.token))
            .then(() => nav("/"));
    };

    return (
        <>
            <Typography variant={"h5"} align={'center'} mb={3}>
                LOGIN
            </Typography>
            <Box component={"form"} onSubmit={handleSubmit} sx={{mt: 7}}>
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={4} textAlign={"center"}>
                        <TextField
                            label="Username"
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <AccountCircle/>
                                    </InputAdornment>
                                ),
                            }}
                            onChange={event => setUsername(event.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} sm={4} textAlign={"center"}>
                        <TextField
                            label="Password"
                            variant="outlined"
                            size="small"
                            type={"password"}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Key/>
                                    </InputAdornment>
                                ),
                            }}
                            onChange={event => setPassword(event.target.value)}
                        />
                    </Grid>

                    <Grid item xs={12} sm={4} textAlign={"center"}>
                        <Button
                            type="submit"
                            variant="contained"
                            sx={{height: "39px"}}
                        >
                            Login
                        </Button>
                    </Grid>
                </Grid>
            </Box>
        </>
    )
}