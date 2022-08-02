import {Button, Card, CardActions, CardContent, Grid} from "@mui/material";
import {UserDTO} from "../../services/model";
import {apiSendFriendRequest} from "../../services/apiServices";

interface UserCardProps {
    user: UserDTO
}

export default function UserCard(props: UserCardProps) {

    function sendFriendRequest() {
        apiSendFriendRequest(props.user.username);
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