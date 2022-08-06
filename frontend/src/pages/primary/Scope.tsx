import {
    Box,
    Button,
    Checkbox,
    ClickAwayListener,
    FormControl,
    FormControlLabel,
    FormGroup,
    Popper
} from "@mui/material";
import {ChangeEvent, useState, MouseEvent} from "react";

interface ScopeProps {
    friends: string[],
    scope: string[],
    setScope: (scope: string[]) => void
}

export default function Scope(props: ScopeProps) {
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    const handleClick = (event: MouseEvent<HTMLElement>) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const open = Boolean(anchorEl);
    const id = open ? 'scope-popper' : undefined;

    const handleClickAway = () => {
        setAnchorEl(null);
    };

    const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
        console.log(`event name: ${event.target.name}, event checked: ${event.target.checked}`);
        console.log(props.scope);

        if (event.target.checked) {
            props.setScope([...props.scope, event.target.name]);
        } else {
            props.setScope(props.scope.filter(s => s !== event.target.name));
        }
    };

    return (
        <ClickAwayListener onClickAway={handleClickAway}>
            <div>
                <Button variant={"contained"} size={"small"} aria-describedby={id} type="button" onClick={handleClick}>
                    scope choice
                </Button>
                <Popper id={id} open={open} anchorEl={anchorEl}>
                    <Box border={1} padding={1} bgcolor={"black"}>
                        <FormControl>
                            <FormGroup>
                                {
                                    props.friends.map(f =>
                                        <FormControlLabel key={f} control={
                                            <Checkbox checked={props.scope.includes(f)} name={f}
                                                      onChange={handleChange}/>
                                        } label={f}/>
                                    )
                                }
                            </FormGroup>
                        </FormControl>
                    </Box>
                </Popper>
            </div>
        </ClickAwayListener>
    );
}