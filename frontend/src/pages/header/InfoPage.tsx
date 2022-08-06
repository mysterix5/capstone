import {Box, Divider, Popper, Typography} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import {useState, MouseEvent} from "react";

export default function InfoPage() {
    const [anchorEl, setAnchorEl] = useState<null | SVGSVGElement>(null);

    const handleClick = (event: MouseEvent<SVGSVGElement>) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const open = Boolean(anchorEl);
    const id = open ? 'scope-popper' : undefined;

    const MyDivider = () => (
        <Divider
            color={"white"}
            sx={{marginBottom: 1}}
        />
    );
    const Header = (props: any) => (
        <>
            <Typography variant={"h5"} textAlign={"center"}>
                {props.children}
            </Typography>
            <MyDivider/>
        </>
    );

    const SubHeader = (props: any) => (
        <>
            <Typography variant={"h6"} textAlign={"center"}>
                {props.children}
            </Typography>
            <MyDivider/>
        </>
    );


    const Text = (props: any) => (
            <Typography variant={"body2"}>
                {props.children}
            </Typography>
    );


    return (
        <Box>
            <InfoIcon color={'primary'} onClick={handleClick}/>
            <Popper id={id} open={open} anchorEl={anchorEl} transition={false} sx={{zIndex: 1800}}>
                <Box border={1} padding={1} bgcolor={"black"} mt={4} ml={3} mr={3} onClick={() => setAnchorEl(null)}>
                    <Header>Vover info page</Header>
                    <SubHeader>Get started</SubHeader>
                    <Text>You can always click on the 'Vover' title to get to the main page. </Text>
                    <Text>Enter a text you want to send as audio to your friends</Text>
                </Box>
            </Popper>
        </Box>
    )
}