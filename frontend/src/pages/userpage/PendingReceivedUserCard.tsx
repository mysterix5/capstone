import {Button, Card, Grid, Typography} from "@mui/material";
import {UserDTO} from "../../services/model";
import {apiAcceptFriendship} from "../../services/apiServices";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function PendingReceivedUserCard(props: UserCardProps) {

    function acceptFriendRequest() {
        apiAcceptFriendship(props.user.username)
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
                        <Button onClick={acceptFriendRequest} size={"small"}>
                            accept friend request
                        </Button>
                </Grid>
            </Grid>
        </Card>
    )
}