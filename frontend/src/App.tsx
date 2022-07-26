import React from 'react';
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./pages/Main";
import Header from "./pages/subcomponents/Header";
import {blueGrey} from "@mui/material/colors";
import Record from "./pages/Record";
import RegisterPage from "./usermanagement/RegisterPage";
import LoginPage from "./usermanagement/LoginPage";
import AuthProvider from "./usermanagement/AuthProvider";
import GlobalVoverErrorDisplay from "./globalTools/GlobalVoverErrorDisplay";

const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: blueGrey,
        background: {
            default: blueGrey["900"]
        }
    },
});

export default function App() {

    return (
        <ThemeProvider theme={darkTheme}>
            <CssBaseline/>
            <BrowserRouter>
                <AuthProvider>
                    <Header/>
                    <Routes>
                        <Route path="/" element={<Main/>}/>
                        <Route path="/record" element={<Record/>}/>
                        <Route path="/login" element={<LoginPage/>}/>
                        <Route path="/register" element={<RegisterPage/>}/>
                    </Routes>
                    <GlobalVoverErrorDisplay/>
                </AuthProvider>
            </BrowserRouter>
        </ThemeProvider>
    );
}