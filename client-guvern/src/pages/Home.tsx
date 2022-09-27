import React from 'react'
import {Box, Button, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";

export const Home = () => {
    const navigate = useNavigate()

    return (
        <>
            <Typography mb={2} mt={4} variant="h5">
                Creare documente virtuale
            </Typography>

            <Box display={'flex'} gap={4}>
                <Button variant={"contained"} onClick={() => navigate("/create/id_card")}>
                    Card de identitate
                </Button>

                <Button variant={"contained"} onClick={() => navigate("/create/driver_license")}>
                    Permis de conducere
                </Button>

                <Button variant={"contained"} onClick={() => navigate("/create/medical_card")}>
                    Card de sănătate
                </Button>
            </Box>

            <Typography mb={2} mt={4} variant="h5">
                Căutare cetățean
            </Typography>
        </>
    );
}
