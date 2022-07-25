import {useEffect, useState} from "react";
import {useAuth} from "../../usermanagement/AuthProvider";
import {apiGetRecordPage} from "../../services/apiServices";
import {RecordPage} from "../../services/model";
import {Grid} from "@mui/material";
import {useNavigate} from "react-router-dom";
import RecordDetails from "./RecordDetails";


export default function Recordings() {

    const [recordPage, setRecordPage] = useState<RecordPage>(
        {
            page: 0,
            noPages: 0,
            size: 10,
            searchTerm: "",
            records: [],
            accessibilityChoices: []
        });

    const {getToken, username} = useAuth();

    const nav = useNavigate();

    useEffect(() => {
        if (!username) {
            nav("/login")
        }
    }, [username, nav])

    useEffect(() => {
        getRecordPage();
    }, [getToken])

    function getRecordPage(){
        apiGetRecordPage(getToken(), recordPage.page, recordPage.size, recordPage.searchTerm)
            .then((r: RecordPage) => setRecordPage(r));
    }

    return (
        <Grid container>
            {
                recordPage?.records.map(r =>
                    <RecordDetails key={r.id} record={r} accessibilityChoices={recordPage?.accessibilityChoices} getRecordPage={getRecordPage}/>
                )
            }
        </Grid>
    )
}