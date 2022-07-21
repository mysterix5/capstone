import {Button, FormControl, InputLabel, MenuItem, Select, SelectChangeEvent} from "@mui/material";
import {isAvailable} from "./helpers";
import {WordAvail, WordMetaData} from "../../services/model";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

interface WordDropdownProps {
    wordAvail: WordAvail,
    setIdInArray: (id: string)=>void
    choicesList: WordMetaData[],
    id: string
}

export default function WordDropdown(props: WordDropdownProps) {

    const [id, setId] = useState(props.id);

    useEffect(()=>{
        props.setIdInArray(id);
    }, [id, props, props.setIdInArray])

    const nav = useNavigate();

    return (
        <FormControl variant="filled" size="small">
            <InputLabel id="demo-simple-select-label">
                {props.wordAvail.word.toUpperCase()}
            </InputLabel>
            <Select
                variant={"filled"}
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                autoWidth
                value={isAvailable(props.wordAvail.availability) ? id : 'record'}
                label={props.wordAvail.word.toUpperCase()}
                onChange={(e: SelectChangeEvent) => setId(e.target.value)}
            >
                {isAvailable(props.wordAvail.availability) ?
                    props.choicesList.map(wmd =>
                        <MenuItem key={wmd.id} value={wmd.id}>
                            {wmd.creator} - {wmd.tag}
                        </MenuItem>
                    )
                    :
                    <MenuItem key={'record'} value={'record'}>
                        <Button onClick={() => nav("/record")}>
                            record
                        </Button>
                    </MenuItem>
                }
            </Select>
        </FormControl>
    )
}