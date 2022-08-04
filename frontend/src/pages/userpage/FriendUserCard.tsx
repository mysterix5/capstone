import {Button, Card, CardActions, CardContent, Grid} from "@mui/material";
import {UserDTO} from "../../services/model";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function FriendUserCard(props: UserCardProps) {

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
                        <Button>
                            end friendship
                        </Button>
                    </CardActions>
                </Grid>
            </Grid>
        </Card>
    )
}