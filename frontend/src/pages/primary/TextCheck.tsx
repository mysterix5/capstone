import {
    Grid
} from "@mui/material";
import {TextMetadataResponse} from "../../services/model";
import WordDropdown from "./WordDropdown";

interface TextCheckProps {
    textMetadataResponse: TextMetadataResponse,
    setId: (id: string, index: number) => void
}

export default function TextCheck(props: TextCheckProps) {

    function generateIdSetter(index: number){
        return (id: string) => {
            props.setId(id, index);
        }
    }

    return (
        <>
            <Grid container justifyContent={"center"}>
                {
                    props.textMetadataResponse &&
                    props.textMetadataResponse!.textWords.map((wordAvail, i) =>
                        <Grid item key={i} margin={0.5}>
                            <WordDropdown wordAvail={wordAvail} setId={generateIdSetter(i)} choicesList={props.textMetadataResponse.wordRecordMap[wordAvail.word]} id={props.textMetadataResponse.defaultIds[i]}/>
                        </Grid>
                    )}
            </Grid>
        </>
    )
}