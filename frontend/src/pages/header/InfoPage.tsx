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
            <Divider/>
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
    const TextListItem = (title: any, text: string) => {
        return (
            <Box display={"flex"} flexWrap={"nowrap"} alignItems={"center"}>
                <Typography fontWeight={900} display={"inline"} mr={1}>{title}</Typography>
                <Typography display={"inline"} variant={"body2"}>{text}</Typography>
            </Box>
        )
    }

    const ColorItem = (thecolor: string) => {
        return <Box width={"15px"} height={"15px"} bgcolor={thecolor}/>;
    }

    return (
        <Box>
            <InfoIcon color={'primary'} onClick={handleClick}/>
            <Popper id={id} open={open} anchorEl={anchorEl} transition={false} sx={{zIndex: 1800}}>
                <Box border={1} padding={1} bgcolor={"black"} mt={4} ml={3} mr={3} onClick={() => setAnchorEl(null)}>
                    <Header>Vover info page</Header>
                    <Text>With Vover you can create an amazing roboter-like voice messages with the voices of your
                        friends. </Text>

                    <SubHeader>Vover messages</SubHeader>
                    <Text>Enter a text you want to send as audio to your friends and click send. </Text>
                    <Text>The words of your text appear again as select menus. You can see if the words you entered are
                        all allowed and then choose from a list of recordings with the information about creator and
                        tag. Each recording is colored with the following meaning: </Text>
                    {TextListItem(ColorItem("#008609"), "recording is in scope")}
                    {TextListItem(ColorItem("#a7e006"), "recording is from a friend but not in scope")}
                    {TextListItem(ColorItem("#d39104"), "a user who is not your friend recorded this")}
                    {TextListItem(ColorItem("#0930b0"), "you recorded this yourself")}
                    <Divider sx={{my: 0.4}}/>
                    {TextListItem(ColorItem("#b43535"), "not available, record the word!")}
                    {TextListItem(ColorItem("#881111"), "invalid, only letters are allowed")}

                    <SubHeader>Friends and scope</SubHeader>
                    <Text>On your Userpage (upper right corner) there is a friends section. You can connect with other
                        users here by sending them a friendship request. After they accept it in the same place your
                        friends recordings will automatically be preferred and you can use their recordings with
                        accessibility 'FRIENDS'. </Text>
                    <Text>On top of the main page you can choose a selection of your friends as your scope to prefer
                        their recordings this time.</Text>

                    <SubHeader>Recording</SubHeader>
                    <Text>Record a word and add it to the database. You must fill in the following metadata for each
                        record: </Text>
                    {TextListItem("word", "which word did you record")}
                    {TextListItem("tag", "add a fitting tag like 'normal', 'accent' or 'funny'")}
                    {TextListItem("accessibility", "decide if everyone, only your friends or only yourself can use this recording in their messages")}
                    <Text>There are different ways to get to the record page: </Text>
                    {TextListItem(
                        <span>&#8226;</span>, "Click on the record symbol in the upper left corner; you can simply record a few words here and navigate to another place afterwards.")}
                    {TextListItem(
                        <span>&#8226;</span>, "After submitting a text open one of the word select menus an click on record. You are navigated to a special record page where you can record this single word and back to the main page with your previously entered text.")}
                    {TextListItem(
                        <span>&#8226;</span>, "After submitting a text click on the 'record missing words' or 'record all words' button. Just like in the case before you are navigated to the special record page and can record all the words you need comfortably in a row.")}
                    <Text>You can hear again, edit and delete your recordings on the user page. </Text>

                    <SubHeader>History</SubHeader>
                    <Text>Also on you user page there is a 'History' page. Here you can see your last requested audios
                        and by clicking on a history element you are navigated to the main page with the exact same text
                        and recordings selected like the last time you requested this. </Text>

                    <SubHeader>Hints</SubHeader>
                    {TextListItem("navigation", "You can always click on the Vover title to get to the main page. ")}
                    {TextListItem("history", "The url of an history item can be shared")}
                    {TextListItem("recording", "If you are not satisfied with your recording just press the record button again. The last recording is overwritten. ")}
                </Box>
            </Popper>
        </Box>
    )
}
