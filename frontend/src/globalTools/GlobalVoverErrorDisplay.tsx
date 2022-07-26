import {Box, Typography} from "@mui/material";
import {useAuth} from "../usermanagement/AuthProvider";

export default function GlobalVoverErrorDisplay() {

    const {error} = useAuth();

    return (
        <Box mt={4}>
            {error && error.message &&
                <Box textAlign={'center'} m={1} border={0.3} borderRadius={2} borderColor={"#c41313"}>
                    <Typography color={"#c41313"} variant={"h6"}>
                        {error.message}
                    </Typography>
                    {error.subMessages &&
                        <Box m={0.7}>
                            {
                                error.subMessages.map((s, i) =>
                                    <Typography key={i} color={"#c41313"} border={0.2} borderRadius={0.5} m={0.3}>
                                        {s}
                                    </Typography>)
                            }
                        </Box>
                    }
                </Box>
            }
        </Box>
    )
}