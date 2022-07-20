import {
    Box,
    Button,
    FormControl,
    Grid,
    InputLabel,
    MenuItem,
    Select,
    SelectChangeEvent
} from "@mui/material";
import {TextResponse, WordAvail} from "../../services/model";
import {isAvailable} from "./helpers";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

interface TextCheckProps {
    splitText: TextResponse,
    ids: string[],
    setIds: (ids: string[]) => void
}

export default function TextCheck(props: TextCheckProps) {
    const [age, setAge] = useState('');

    const handleChange = (event: SelectChangeEvent) => {
        setAge(event.target.value as string);
    };

    const [ids, setIds] = useState<string[]>([]);

    useEffect(()=>{
        setIds(props.ids);
    },[])

    useEffect(()=>{
        props.setIds(ids);
    }, [ids])

    const nav = useNavigate();

    function chooseWord(event: SelectChangeEvent<string>, index: number) {
        console.log(event.target.value);
        let localIds = ids;
        localIds[index] = event.target.value;
        setIds(localIds);
        console.log(ids);
    }

    function getWordButton(word: WordAvail, index: number) {
        let myColor: string = "#fff";
        // let myTextDecoration: string = "none";

        if (word.availability === "PUBLIC") {
            myColor = "#12670c";
        } else if (word.availability === "INVALID") {
            myColor = "#881111";
            // myTextDecoration = "line-through";
        } else if (word.availability === "ABSENT") {
            myColor = "#b43535";
        }

        return (
            <Box sx={{backgroundColor: myColor}}>
                <FormControl variant="filled" size="small">
                    <InputLabel id="demo-simple-select-label">
                        {word.word.toUpperCase()}
                    </InputLabel>
                    <Select
                        variant={"filled"}
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        autoWidth
                        defaultValue={isAvailable(word.availability) ? props.ids.at(index): 'record'}
                        value={isAvailable(word.availability) ? ids.at(index) : 'record'}
                        label={word.word.toUpperCase()}
                        onChange={(e: SelectChangeEvent<string>) => chooseWord(e, index)}
                    >
                        {isAvailable(word.availability) ?
                            props.splitText.wordMap[word.word].map(wmd =>
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
            </Box>
        )
    }

    return (
        <>
            <Grid container justifyContent={"center"}>
                {
                    props.splitText &&
                    props.splitText!.textWords.map((r, i) =>
                        <Grid item key={i} margin={0.5}>
                            {getWordButton(r, i)}
                        </Grid>
                    )}
            </Grid>
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">Age</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={age}
                    label="Age"
                    onChange={handleChange}
                >
                    <MenuItem value={10}>Ten</MenuItem>
                    <MenuItem value={20}>Twenty</MenuItem>
                    <MenuItem value={30}>Thirty</MenuItem>
                </Select>
            </FormControl>
        </>
    )
}