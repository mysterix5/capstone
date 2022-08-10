import {Button, Card, Grid, Typography} from "@mui/material";
import {UserDTO} from "../../services/model";
import {apiSendFriendRequest} from "../../services/apiServices";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function UnrelatedUserCard(props: UserCardProps) {

    function sendFriendRequest() {
        apiSendFriendRequest(props.user.username)
            .then(() => props.refresh());
    }

    return (
        <Card sx={{m: 1}}>
            <Grid container wrap={"nowrap"}>
                <Grid item flexGrow={1} m={0.5}>
                    <Typography>
                        {props.user.username}
                    </Typography>
                </Grid>
                <Grid item m={0.5}>
                    <Button onClick={sendFriendRequest} size={"small"}>
                        send friend request
                    </Button>
                </Grid>
            </Grid>
        </Card>
    )
}