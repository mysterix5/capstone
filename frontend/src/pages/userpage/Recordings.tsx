import {useEffect, useState} from "react";
import {useAuth} from "../../usermanagement/AuthProvider";
import {apiGetRecordPage} from "../../services/apiServices";
import {RecordPage} from "../../services/model";
import {Box, Button, Grid} from "@mui/material";
import {useNavigate} from "react-router-dom";
import RecordDetails from "./RecordDetails";


export default function Recordings() {

    const [recordPage, setRecordPage] = useState<RecordPage>(
        {
            page: 0,
            noPages: 0,
            size: 6,
            searchTerm: "",
            records: [],
            accessibilityChoices: []
        });

    const {getToken} = useAuth();

    const nav = useNavigate();

    useEffect(() => {
        if (!getToken()) {
            nav("/login")
        }
        updateRecordPage();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [nav])

    function getSpecificRecordPage(page: number) {
        apiGetRecordPage(getToken(), page, recordPage.size, recordPage.searchTerm)
            .then((r: RecordPage) => setRecordPage(r));
    }

    function updateRecordPage() {
        getSpecificRecordPage(recordPage.page);
    }

    return (
        <Box>
            <Grid container>
                {
                    recordPage!.records.map(r =>
                        <RecordDetails key={r.id} record={r} accessibilityChoices={recordPage?.accessibilityChoices}
                                       getRecordPage={updateRecordPage}/>
                    )
                }
            </Grid>
            {recordPage && recordPage.page > 0 &&
                <Button onClick={() => getSpecificRecordPage(recordPage.page-1)}>
                    prev
                </Button>
            }
            {recordPage && recordPage.page < recordPage.noPages - 1 &&
                <Button onClick={() => getSpecificRecordPage(recordPage.page+1)}>
                    next
                </Button>
            }
        </Box>
    )
}