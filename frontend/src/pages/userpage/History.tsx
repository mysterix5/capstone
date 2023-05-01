import { useEffect, useState } from "react";
import { apiGetHistory } from "../../services/apiServices";
import { useAuth } from "../../usermanagement/AuthProvider";
import { HistoryEntryTextDate } from "../../services/model";
import { Grid, Typography } from "@mui/material";
import { formatDistance } from "date-fns";
import { useNavigate } from "react-router-dom";


export default function History() {
    const [history, setHistory] = useState<HistoryEntryTextDate[]>([]);

    const nav = useNavigate();

    const { defaultApiResponseChecks } = useAuth();

    useEffect(() => {
        apiGetHistory()
            .then(setHistory)
            .catch(err => {
                defaultApiResponseChecks(err);
            });
    }, [defaultApiResponseChecks])

    function fancyStringFromDate(date: Date) {
        try {
            return formatDistance(date, new Date()) + " ago";
        } catch (err) {
            console.log(err);
            return "1 hour ago";
        }
    }

    return (
        <Grid container direction={"column"}>
            {
                history?.map((h, i) =>
                    <Grid item key={i} mb={0.5} onClick={() => nav(`/h/${h.id}`)}>
                        <Grid container direction={"row"} wrap={"nowrap"}>
                            <Grid item ml={1} mr={1} xs={4}>
                                <Typography color={"lightgray"}>
                                    {fancyStringFromDate(h.requestTime)}
                                </Typography>
                            </Grid>
                            <Grid item border={1} borderRadius={5} xs={7}>
                                <Typography ml={1} mr={1}>
                                    {h.text}
                                </Typography>
                            </Grid>
                        </Grid>
                    </Grid>
                )
            }
        </Grid>
    )
}