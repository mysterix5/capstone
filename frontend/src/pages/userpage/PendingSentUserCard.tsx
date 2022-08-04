import {Card, CardActions, CardContent, Grid, Typography} from "@mui/material";
import {UserDTO} from "../../services/model";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function PendingSentUserCard(props: UserCardProps) {

    return (
        <Card sx={{m: 1}}>
            <Grid container>
                <Grid item flexGrow={1}>
                    <CardContent>
                        {props.user.username}
                    </CardContent>
                </Grid>
                <Grid item xs={6} xl={4}>
                    <CardActions>
                        <Typography>your friend request was not yet accepted</Typography>
                    </CardActions>
                </Grid>
            </Grid>
        </Card>
    )
}