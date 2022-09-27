import { Card, Divider, Grid, Typography } from "@mui/material";
import Image from "next/image";
import React from "react";
import { cardColor } from "../../colors";
import { getBirthDateFromCnp } from "../../helpers/documentHelpers";

export const DriverLicense = () => {
  return (
    <Card
      sx={{
        background: cardColor,
        minHeight: 200,
        maxWidth: 400,
        maxHeight: 350,
        padding: "10px",
      }}
      raised
      elevation={7}
      id="healthIssuranceCard"
    >
      <Grid container spacing={0.5}>
        <Grid container item xs={3}>
          <Grid item xs={12}>
            <Image
              src="/images/permisConducere.png"
              width="100%"
              height="77px"
              objectFit="contain"
              objectPosition="top"
            />
          </Grid>
          <Grid item xs={12}>
            <Image
              src="/images/portrait.jpeg"
              width="77px"
              height="100%"
              objectFit="contain"
            />
          </Grid>
          <Grid item container xs={12} spacing={0.5}>
            <Grid item>
              <Typography
                display="inline-block"
                textTransform="uppercase"
                fontSize="0.7rem"
              >
                9.
              </Typography>
            </Grid>
            {["AM", "A", "B"].map((category, index) => (
              <Grid item key={index}>
                <Typography
                  display="inline-block"
                  fontSize="0.7rem"
                  border="1px solid black"
                >
                  {category}
                </Typography>
              </Grid>
            ))}
          </Grid>
        </Grid>
        <Grid item xs={9} container maxHeight="150px">
          <Grid container item xs={12} justifyContent="space-between">
            <Grid item>
              <Typography color="#1C27A7" variant="h6">
                PERMIS DE CONDUCERE
              </Typography>
            </Grid>
            <Grid item>
              <Typography color="#1C27A7" variant="h6">
                ROMANIA
              </Typography>
            </Grid>
          </Grid>
          <Grid item xs={12}>
            <Typography textTransform="uppercase" fontSize="0.7rem">
              1. ISPILANTE
            </Typography>
          </Grid>
          <Grid item xs={12}>
            <Typography textTransform="uppercase" fontSize="0.7rem">
              2. Sentiment Brusli
            </Typography>
          </Grid>
          <Grid item xs={12}>
            <Typography textTransform="uppercase" fontSize="0.7rem">
              3. 22.06.2002 Sibiu, SB
            </Typography>
          </Grid>
          <Grid container item xs={12} spacing={2}>
            <Grid item>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                4a. 22.06.2022
              </Typography>
            </Grid>
            <Grid item>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                4c. SRPCIV Sibiu
              </Typography>
            </Grid>
          </Grid>
          <Grid container item xs={12} spacing={2}>
            <Grid item>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                4b. 22.06.2032
              </Typography>
            </Grid>
            <Grid item>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                4d. 89237491083471
              </Typography>
            </Grid>
          </Grid>
          <Grid item xs={12}>
            <Typography textTransform="uppercase" fontSize="0.7rem">
              5. SS023402
            </Typography>
          </Grid>
        </Grid>
        <Grid container item xs={3}></Grid>
      </Grid>
    </Card>
  );
};
