import {Button, Card, Grid, Typography} from "@mui/material";
import {UserDTO} from "../../services/model";
import {apiEndFriendship} from "../../services/apiServices";

interface UserCardProps {
    user: UserDTO,
    refresh: () => void
}

export default function FriendUserCard(props: UserCardProps) {

    function endFriendship() {
        apiEndFriendship(props.user.username)
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
                    <Button size={"small"} onClick={endFriendship}>
                        end friendship
                    </Button>
                </Grid>
            </Grid>
        </Card>
    )
}