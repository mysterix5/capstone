import {Box, Button} from "@mui/material";

interface ShareProps {
    audioBlobPart: any
}

export default function Share(props: ShareProps) {

    const handleSharing = async () => {
        if (navigator.share) {
            try {
                await navigator
                    .share({title: "Vover voice message", files: [props.audioBlobPart]})
                    .then(() =>
                        console.log("sharing worked")
                    );
            } catch (error) {
                console.log(`Oops! I couldn't share to the world because: ${error}`);
            }
        } else {
            // fallback code
        }
    };

    return (
        <Box>
            { props.audioBlobPart && navigator.canShare && navigator.canShare({ files: [props.audioBlobPart] }) &&
                <Button variant={"contained"} onClick={handleSharing}>
                    share
                </Button>
            }
        </Box>
    )
}