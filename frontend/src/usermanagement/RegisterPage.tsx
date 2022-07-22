import {Box, Button, Grid, InputAdornment, TextField, Typography} from "@mui/material";
import {FormEvent, useState} from "react";
import {
    AccountCircle, Key
} from "@mui/icons-material";
import {useNavigate} from "react-router-dom";
import {sendRegister} from "../services/apiServices";
import {VoverError} from "../services/model";
import VoverErrorDisplay from "../globalTools/VoverErrorDisplay";

export default function RegisterPage() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordRepeat, setPasswordRepeat] = useState("");

    const [error, setError] = useState<VoverError>();

    const nav = useNavigate();

    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();
        sendRegister({username, password, passwordRepeat})
            .then(() => nav("/login"))
            .catch((error) => {
                if (error.response) {
                    setError(error.response.data);
                }
            });
    };

    return (
        <>
            <Typography variant={"h5"} align={'center'} mb={3}>
                REGISTER
            </Typography>
            <Box component={"form"} onSubmit={handleSubmit}>
                <Grid container alignItems="center" spacing={2}>
                    <Grid item xs={12} sm={3} textAlign={"center"}>
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
                    <Grid item xs={12} sm={3} textAlign={"center"}>
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
                    <Grid item xs={12} sm={3} textAlign={"center"}>
                        <TextField
                            label="Repeat password"
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
                            onChange={event => setPasswordRepeat(event.target.value)}
                        />
                    </Grid>
                    <Grid item xs={12} sm={3} textAlign={"center"}>
                        <Button
                            type="submit"
                            variant="contained"
                            sx={{height: "39px"}}
                        >
                            Register
                        </Button>
                    </Grid>
                </Grid>
            </Box>
            {error &&
                <VoverErrorDisplay error={error}/>
            }
        </>
    )
}