import {ReactNode, useContext, useEffect, useState} from "react";
import AuthContext from "./AuthContext";
import {useNavigate} from "react-router-dom";
import {VoverError} from "../services/model";

export default function AuthProvider({children}:{children :ReactNode}) {
    const nav = useNavigate();
    const [token, setToken] = useState(localStorage.getItem('jwt') ?? '');
    const [roles, setRoles] = useState<string[]>([]);
    const [username, setUsername] = useState('');
    const [expired, setExpired] = useState<number>(0);
    const [error, setErrorState] = useState<VoverError>({message: "", subMessages: []});
    const [errorTimer, setErrorTimer] = useState(-1);
    const [errorTimerGoal, setErrorTimerGoal] = useState(-1);

    useEffect(() => {
        if (token) {
            const decoded = window.atob(token.split('.')[1]);
            const decodeJWT = JSON.parse(decoded);
            setUsername(decodeJWT.sub);
            setRoles(decodeJWT.roles)
            setExpired(decodeJWT.exp)
        }
    }, [token, nav]);

    const logout = () => {
        console.log("logout")
        localStorage.removeItem('jwt');
        setToken('');
        setRoles([]);
        setUsername('');
        setExpired(0);
        nav("/");
    };

    const login = (gotToken: string) => {
        localStorage.setItem('jwt', gotToken);
        setToken(gotToken);
    };

    const getToken = () => {
        if(expired && ((expired - (Math.floor(Date.now() / 1000)))<0)){
            logout();
            setError({message: "Your login token expired", subMessages: ["you need to login again"]})
            return '';
        }
        return token;
    }

    useEffect(() => {
        if(errorTimer>=errorTimerGoal){
            setErrorState(({message: "", subMessages: []}));
            setErrorTimer(-1);
            setErrorTimerGoal(-2);
        }
        if(errorTimer<errorTimerGoal){
            setTimeout(()=>setErrorTimer((e)=>e+1), 1000);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [errorTimer])

    const setError = (err: VoverError) => {
        setErrorState(err);
        if(errorTimer<0){
            setErrorTimerGoal(7);
            setErrorTimer(0);
        }else{
            setErrorTimerGoal(errorTimer + 7);
        }
    }

    return <AuthContext.Provider value={{roles, username, getToken, error, setError, logout, login}} >{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
