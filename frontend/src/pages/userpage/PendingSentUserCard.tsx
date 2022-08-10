import {Card, Grid, Typography} from "@mui/material";
import {UserDTO} from "../../services/model";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function PendingSentUserCard(props: UserCardProps) {

    return (
        <Card sx={{m: 1}}>
            <Grid container wrap={"nowrap"}>
                <Grid item flexGrow={1} m={0.5}>
                    <Typography>
                        {props.user.username}
                    </Typography>
                </Grid>
                <Grid item m={0.5}>
                    <Typography>your friend request was not yet accepted</Typography>
                </Grid>
            </Grid>
        </Card>
)
}