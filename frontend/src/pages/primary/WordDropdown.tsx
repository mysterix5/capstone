import {FormControl, Grid, InputLabel, MenuItem, Select, SelectChangeEvent, Typography} from "@mui/material";
import {isAvailable} from "../../globalTools/helpers";
import {WordAvail, RecordMetaData} from "../../services/model";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

interface WordDropdownProps {
    wordAvail: WordAvail,
    setId: (id: string) => void
    choicesList: RecordMetaData[],
    id: string
}

export default function WordDropdown(props: WordDropdownProps) {

    const [id, setId] = useState(props.id);

    useEffect(() => {
        props.setId(id);
    }, [id, props, props.setId])

    const nav = useNavigate();

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
                value={isAvailable(props.wordAvail.availability) ? id : 'record'}
                label={props.wordAvail.word.toUpperCase()}
                IconComponent={() => null}
                onChange={(e: SelectChangeEvent) => setId(e.target.value)}
                sx={{textAlign: "center"}}
            >
                {isAvailable(props.wordAvail.availability) ?
                    props.choicesList.map(wmd =>
                        <MenuItem key={wmd.id} value={wmd.id}>
                            <div>
                                <Typography>{wmd.creator}</Typography>
                            </div>
                            <div>
                                <Typography>{wmd.tag}</Typography>
                            </div>
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