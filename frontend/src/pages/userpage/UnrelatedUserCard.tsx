import {Button, Card, CardActions, CardContent, Grid} from "@mui/material";
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
            <Grid container>
                <Grid item flexGrow={1}>
                    <CardContent>
                        {props.user.username}
                    </CardContent>
                </Grid>
                <Grid item>
                    <CardActions>
                        <Button onClick={sendFriendRequest}>
                            send friend request
                        </Button>
                    </CardActions>
                </Grid>
            </Grid>
        </Card>
    )
}