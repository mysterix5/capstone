import {Box, Typography} from "@mui/material";
import {VoverError} from "../services/model";

interface CustomErrorDisplayProps {
    error: VoverError
}

export default function VoverErrorDisplay(props: CustomErrorDisplayProps) {

    return (
        <Box textAlign={'center'} m={1} border={0.3} borderRadius={2} borderColor={"#c41313"}>
            <Typography color={"#c41313"} variant={"h6"}>
                {props.error.message}
            </Typography>
            {props.error.subMessages &&
                <Box m={0.7}>
                    {
                        props.error.subMessages.map((s, i) => <Typography key={i} color={"#c41313"} border={0.2} borderRadius={0.5}
                                                                     m={0.3}>{s}</Typography>)
                    }
                </Box>
            }
        </Box>
    )
}