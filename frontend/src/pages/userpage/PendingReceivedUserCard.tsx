import {Button, Card, CardActions, CardContent, Grid} from "@mui/material";
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
            <Grid container>
                <Grid item flexGrow={1}>
                    <CardContent>
                        {props.user.username}
                    </CardContent>
                </Grid>
                <Grid item>
                    <CardActions>
                        <Button onClick={acceptFriendRequest}>
                            accept friend request
                        </Button>
                    </CardActions>
                </Grid>
            </Grid>
        </Card>
    )
}