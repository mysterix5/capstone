import {ReactNode, useCallback, useContext, useEffect, useState} from "react";
import AuthContext from "./AuthContext";
import {useNavigate} from "react-router-dom";
import {VoverError} from "../services/model";
import axios, {AxiosError} from "axios";

export default function AuthProvider({children}: { children: ReactNode }) {
    const nav = useNavigate();
    const [token, setToken] = useState(localStorage.getItem('jwt') ?? '');
    const [roles, setRoles] = useState<string[]>([]);
    const [username, setUsername] = useState('');
    const [error, setErrorState] = useState<VoverError>({message: "", subMessages: []});
    const [errorTimer, setErrorTimer] = useState(-1);
    const [errorTimerGoal, setErrorTimerGoal] = useState(-1);

    useEffect(() => {
        if (token) {
            const decoded = window.atob(token.split('.')[1]);
            const decodeJWT = JSON.parse(decoded);
            setUsername(decodeJWT.sub);
            setRoles(decodeJWT.roles)
        }
    }, [token]);

    const logout = useCallback(() => {
        // const logout = () => {
        console.log("logout")
        localStorage.removeItem('jwt');
        setToken('');
        setRoles([]);
        setUsername('');
        nav("/login");
    }, [nav]);

    const login = (gotToken: string) => {
        localStorage.setItem('jwt', gotToken);
        setToken(gotToken);
    };

    useEffect(() => {
        if (errorTimer >= errorTimerGoal) {
            setErrorState(({message: "", subMessages: []}));
            setErrorTimer(-1);
            setErrorTimerGoal(-2);
        }
        if (errorTimer < errorTimerGoal) {
            setTimeout(() => setErrorTimer((e) => e + 1), 1000);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [errorTimer])

    const setError = useCallback((err: VoverError) => {
        setErrorState(err);
        if (errorTimer < 0) {
            setErrorTimerGoal(7);
            setErrorTimer(0);
        } else {
            setErrorTimerGoal(errorTimer + 7);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const defaultApiResponseChecks = useCallback((err: Error | AxiosError) => {
        // const defaultApiResponseChecks = (err: Error | AxiosError) => {
        console.log("default api response check: ");
        console.log(err);
        if (axios.isAxiosError(err)) {
            console.log("is axios err");
            // Access to config, request, and response
            if (err.response?.status === 403) {
                logout();
            }
        } else {
            console.log("is stock err");
            // Just a stock error
        }
    }, [logout])
    // }


    return <AuthContext.Provider
        value={
            {
                username,
                roles,
                error,
                setError,
                logout,
                login,
                defaultApiResponseChecks
            }
        }
    >{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
