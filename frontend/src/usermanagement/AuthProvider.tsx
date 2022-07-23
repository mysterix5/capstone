import {ReactNode, useContext, useEffect, useState} from "react";
import AuthContext from "./AuthContext";
import {useNavigate} from "react-router-dom";

export default function AuthProvider({children}:{children :ReactNode}) {
    const nav = useNavigate();
    const [token, setToken] = useState(localStorage.getItem('jwt') ?? '');
    const [roles, setRoles] = useState<string[]>([]);
    const [username, setUsername] = useState('');
    const [expired, setExpired] = useState<number>(0);

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
        if(expired - (Math.floor(Date.now() / 1000))){
            logout();
            return '';
        }
        return token;
    }

    return <AuthContext.Provider value={{roles, username, getToken, logout, login}} >{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
