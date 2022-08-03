import {Box, Button} from "@mui/material";

interface ShareProps {
    audioFile: any
}

export default function Share(props: ShareProps) {

    const handleSharing = async () => {
        console.log(navigator)
        if (navigator.share) {
            try {
                await navigator
                    .share({title: "Vover voice message", files: [props.audioFile]})
                    // .share({title: "Vover voice message", text: "share share share"})
                    .then(() =>
                        console.log("Hooray! Your content was shared to tha world")
                    );
            } catch (error) {
                console.log(`Oops! I couldn't share to the world because: ${error}`);
            }
        } else {
            // fallback code
            console.log(
                "Web share is currently not supported on this browser. Please provide a fallback"
            );
        }
    };

    // const shareData = {
    //     title: 'MDN',
    //     text: 'Learn web development on MDN!',
    //     url: 'https://developer.mozilla.org'
    // }
    // async function handleSharing() {
    //     console.log(navigator)
    //     console.log(navigator.share)
    //     await window.navigator.share(shareData)
    //
    // }
    return (
        <Box>
            <Button variant={"contained"} onClick={handleSharing}>
                share
            </Button>
        </Box>
    )
}