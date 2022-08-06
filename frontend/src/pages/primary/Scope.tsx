import {Box, Checkbox, FormControl, FormControlLabel, FormGroup, Typography} from "@mui/material";
import {ChangeEvent} from "react";

interface ScopeProps {
    friends: string[],
    scope: string[],
    setScope: (scope: string[]) => void
}

export default function Scope(props: ScopeProps) {

    const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
        console.log(`event name: ${event.target.name}, event checked: ${event.target.checked}`);
        console.log(props.scope);

        if(event.target.checked){
            props.setScope([...props.scope, event.target.name]);
        }else{
            props.setScope(props.scope.filter(s => s!==event.target.name));
        }
    };

    return (
        <Box>
            <Typography>Scope choice</Typography>
            <FormControl>
                <FormGroup>
                    {
                        props.friends.map(f =>
                            <FormControlLabel key={f} control={
                                <Checkbox checked={props.scope.includes(f)} name={f} onChange={handleChange}/>
                            } label={f}/>
                        )
                    }
                </FormGroup>
            </FormControl>
        </Box>
    )
}