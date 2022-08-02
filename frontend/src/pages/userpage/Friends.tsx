import {useEffect, useState} from "react";
import {apiGetUsers} from "../../services/apiServices";
import {AllUsersForFriendPageResponse, UserDTO} from "../../services/model";
import UserCard from "./UserCard";
import {Box, TextField} from "@mui/material";


export default function Friends() {
    const [users, setUsers] = useState<AllUsersForFriendPageResponse>({users: [], friendRequestsReceived: [], friendRequests: []});
    const [usersShown, setUsersShown] = useState<UserDTO[]>([])

    useEffect(() => {
        apiGetUsers()
            .then(u=>{
                setUsers(u);
                setUsersShown(u.users);
            })
    }, [])

    function onChange(searchString: string) {
        setUsersShown(users.users.filter(u=>u.username.toLowerCase().includes(searchString.toLowerCase())))
    }

    return (
        <Box display={"flex"} flexDirection={"column"} alignContent={"center"}>
            <TextField
                label="search for user"
                variant="outlined"
                onChange={event => onChange(event.target.value)}
            />
            {
                usersShown.map((u, i) =>
                    <UserCard key={i} user={u}/>
                )
            }
        </Box>
    )
}