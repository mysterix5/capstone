import {useEffect, useState} from "react";
import {apiGetUsers} from "../../services/apiServices";
import {AllUsersForFriendPageResponse, UserDTO} from "../../services/model";
import {Box, Divider, TextField, Typography} from "@mui/material";
import {useAuth} from "../../usermanagement/AuthProvider";
import FriendUserCard from "./FriendUserCard";
import PendingSentUserCard from "./PendingSentUserCard";
import PendingReceivedUserCard from "./PendingReceivedUserCard";
import UnrelatedUserCard from "./UnrelatedUserCard";


export default function Friends() {
    const [friendUsers, setFriendUsers] = useState<UserDTO[]>([]);
    const [pendingSentUsers, setPendingSentUsers] = useState<UserDTO[]>([]);
    const [pendingReceivedUsers, setPendingReceivedUsers] = useState<UserDTO[]>([]);
    const [unrelatedUsers, setUnrelatedUsers] = useState<UserDTO[]>([]);


    const [searchString, setSearchString] = useState("");

    const {setError, defaultApiResponseChecks} = useAuth();

    useEffect(() => {
        refreshData();
    }, [])

    function refreshData() {
        apiGetUsers()
            .then(u => {
                usersFilter(u);
            }).catch(err => {
            defaultApiResponseChecks(err);
            if (err.response) {
                setError(err.response.data);
            }
        });
    }

    function usersFilter(allUsersInfo: AllUsersForFriendPageResponse) {
        setFriendUsers(allUsersInfo.users.filter(u => allUsersInfo.friends.indexOf(u.username) !== -1));
        setPendingSentUsers(allUsersInfo.users.filter(u => allUsersInfo.friendRequests.indexOf(u.username) !== -1));
        setPendingReceivedUsers(allUsersInfo.users.filter(u => allUsersInfo.friendRequestsReceived.indexOf(u.username) !== -1));
        setUnrelatedUsers(allUsersInfo.users
            .filter(u => allUsersInfo.friends.indexOf(u.username) === -1)
            .filter(u => allUsersInfo.friendRequests.indexOf(u.username) === -1)
            .filter(u => allUsersInfo.friendRequestsReceived.indexOf(u.username) === -1)
        )
    }

    return (
        <Box>
            <Typography variant={"h6"} textAlign={"center"} color={"secondary"}>
                Friends
            </Typography>
            <Box m={1} display={"flex"} flexDirection={"column"} alignContent={"center"}>
                { friendUsers &&
                    friendUsers.map((u, i) =>
                        <FriendUserCard key={i}
                                        user={u}
                                        refresh={refreshData}
                        />
                    )
                }
            </Box>
            <Divider/>
            <Typography variant={"h6"} textAlign={"center"} color={"secondary"}>
                Received friendship requests
            </Typography>
            <Box m={1} display={"flex"} flexDirection={"column"} alignContent={"center"}>
                {pendingReceivedUsers &&
                    pendingReceivedUsers.map((u, i) =>
                        <PendingReceivedUserCard key={i}
                                                 user={u}
                                                 refresh={refreshData}
                        />
                    )
                }
            </Box>
            <Divider/>
            <Typography variant={"h6"} textAlign={"center"} color={"secondary"}>
                Sent friendship requests
            </Typography>
            <Box m={1} display={"flex"} flexDirection={"column"} alignContent={"center"}>
                {pendingSentUsers &&
                    pendingSentUsers.map((u, i) =>
                        <PendingSentUserCard key={i}
                                             user={u}
                                             refresh={refreshData}
                        />
                    )
                }
            </Box>
            <Divider/>
            <Typography variant={"h6"} textAlign={"center"} color={"secondary"}>
                All other users
            </Typography>
            <Box m={1} display={"flex"} flexDirection={"column"} alignContent={"center"}>
                <Box display={"flex"} justifyContent={"center"}>
                    <TextField
                        label="search for user"
                        variant="outlined"
                        onChange={event => setSearchString(event.target.value)}
                    />
                </Box>
                {unrelatedUsers &&
                    unrelatedUsers
                        .filter(u => u.username.toLowerCase().includes(searchString.toLowerCase()))
                        .map((u, i) =>
                            <UnrelatedUserCard key={i}
                                               user={u}
                                               refresh={refreshData}
                            />
                        )
                }
            </Box>
        </Box>
    )
}