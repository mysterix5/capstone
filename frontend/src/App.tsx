import React from 'react';
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./pages/Main";
import Header from "./pages/subcomponents/Header";
import {blueGrey} from "@mui/material/colors";

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
                <Header/>
                <Routes>
                    <Route path="/" element={<Main/>}/>
                </Routes>
            </BrowserRouter>
        </ThemeProvider>
    );
}