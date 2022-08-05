import {FormControl, Grid, InputLabel, MenuItem, Select, Typography} from "@mui/material";
import {WordAvail, RecordMetaData} from "../../services/model";
import {useNavigate} from "react-router-dom";


interface WordDropdownProps {
    wordAvail: WordAvail,
    setId: (id: string) => void
    choicesList: RecordMetaData[],
    id: string
}

export default function WordDropdown(props: WordDropdownProps) {

    const nav = useNavigate();

    function getDropdownColor(availability: string) {

        console.log(`getDropdownColor: availability=${availability}, wordAvailability=${props.wordAvail.availability}`);

        if(props.wordAvail.availability==="AVAILABLE") {
            if (availability === "MYSELF") {
                return "#0930b0";
            } else if (availability === "PUBLIC") {
                return "#d39104";
            } else if (availability === "FRIENDS") {
                return "#a7e006";
            } else if (availability === "SCOPE") {
                return "#008609";
            }
        } else if(props.wordAvail.availability==="INVALID") {
            // myTextDecoration = "line-through";
            return "#881111";
        } else if(props.wordAvail.availability==="NOT_AVAILABLE") {
            return "#b43535";
        }
        // TODO something went wrong
        console.log("getDropdownColor failed");

    }

    console.log(props);

    return (
        <FormControl variant="filled" size="small">
            <InputLabel id="demo-simple-select-label">
                <Grid container direction={"row"} wrap={"nowrap"}>
                    { props.choicesList && props.choicesList.length > 1 &&
                        <Grid item mr={1} border={1}  bgcolor={"darkgrey"} color={"#577ee0"}>
                            {props.choicesList.length}
                        </Grid>
                    }
                    <Grid item>
                        {props.wordAvail.word.toUpperCase()}
                    </Grid>
                </Grid>
            </InputLabel>
            <Select
                size={"medium"}
                variant={"standard"}
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                autoWidth
                value={props.wordAvail.availability==="AVAILABLE" ? props.id : 'record'}
                label={props.wordAvail.word.toUpperCase()}
                IconComponent={() => null}
                onChange={e=>props.setId(e.target.value)}
                sx={{textAlign: "center"}}
            >
                {props.wordAvail.availability==="AVAILABLE" ?
                    props.choicesList.map(wmd =>
                        <MenuItem key={wmd.id} value={wmd.id}>
                            <Grid container direction={"column"} bgcolor={getDropdownColor(wmd.availability)}>
                                <Grid item>
                                    <Typography>{wmd.creator}</Typography>
                                </Grid>
                                <Grid item>
                                    <Typography>{wmd.tag}</Typography>
                                </Grid>
                            </Grid>
                        </MenuItem>
                    )
                    :
                    <MenuItem key={'record'} value={'record'}
                              sx={{justifyItems: "center", display: "f"}} onClick={() => nav("/record")}>
                        <Grid container alignItems={"center"} justifyItems={"center"}>
                            <Grid item>
                                <Typography>record</Typography>
                            </Grid>
                        </Grid>
                    </MenuItem>
                }
            </Select>
        </FormControl>
    )
}