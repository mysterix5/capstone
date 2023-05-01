import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../usermanagement/AuthProvider";
import { apiGetRecordPage } from "../../services/apiServices";
import { RecordPage } from "../../services/model";
import { Box, Button, Grid, TextField } from "@mui/material";
import { useNavigate } from "react-router-dom";
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

    const { defaultApiResponseChecks } = useAuth();

    const nav = useNavigate();

    const getSpecificRecordPage = useCallback((page: number, size: number, searchTerm: string) => {
        apiGetRecordPage(page, size, searchTerm)
            .then((r: RecordPage) => setRecordPage(r))
            .catch(err => {
                defaultApiResponseChecks(err);
            });
    }, [defaultApiResponseChecks]);

    const updateRecordPage = useCallback(() => {
        getSpecificRecordPage(recordPage.page, recordPage.size, recordPage.searchTerm);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [getSpecificRecordPage])

    useEffect(() => {
        updateRecordPage();
    }, [nav, updateRecordPage])

    function setSearchString(searchString: string) {
        recordPage.searchTerm = searchString;
        recordPage.page = 0;
        setRecordPage(recordPage);
        getSpecificRecordPage(0, recordPage.size, searchString);
    }

    return (
        <Box>
            <TextField
                label="search for user"
                variant="outlined"
                onChange={event => setSearchString(event.target.value)}
            />
            <Grid container>
                {
                    recordPage!.records.map(r =>
                        <RecordDetails key={r.id} record={r} accessibilityChoices={recordPage?.accessibilityChoices} getRecordPage={updateRecordPage} />
                    )
                }
            </Grid>
            {recordPage &&
                <>
                    {recordPage.page > 0 ?
                        <Button onClick={() => getSpecificRecordPage(recordPage.page - 1, recordPage.size, recordPage.searchTerm)}>
                            prev
                        </Button>
                        :
                        <Button disabled>
                        </Button>
                    }
                </>
            }
            {recordPage && recordPage.page < recordPage.noPages - 1 &&
                <Button onClick={() => getSpecificRecordPage(recordPage.page + 1, recordPage.size, recordPage.searchTerm)}>
                    next
                </Button>
            }
        </Box>
    )
}