import {useEffect, useState} from "react";
import {useAuth} from "../../usermanagement/AuthProvider";
import {apiGetRecordPage} from "../../services/apiServices";
import {RecordPage} from "../../services/model";
import {Grid} from "@mui/material";
import {useNavigate} from "react-router-dom";
import RecordDetails from "./RecordDetails";


export default function Recordings(){

    const [recordPage, setRecordPage] = useState<RecordPage>();

    const {getToken, username} = useAuth();

    const nav = useNavigate();

    useEffect(() => {
        if (!username) {
            nav("/login")
        }
    }, [username, nav])

    useEffect(() => {
        apiGetRecordPage(getToken(), 0, 10, "")
            .then(r => setRecordPage(r));
    }, [getToken])

    return (
        <Grid container>
            {
                recordPage?.records.map(r=>
                    <RecordDetails key={r.id} record={r}/>
                )
            }
        </Grid>
    )
}