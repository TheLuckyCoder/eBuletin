import { Card, Divider, Grid, Typography } from "@mui/material";
import Image from "next/image";
import React, { useCallback } from "react";
import { cardColor } from "../../colors";
import { getBirthDateFromCnp } from "../../helpers/documentHelpers";

export const IdCard = ({ idCardInfo }) => {
  const downloadIdCard = useCallback(async () => {
    const container = document.getElementById("healthIssuranceCard");
    const root = container.create;
    const canvas = await html2canvas(container, {
      useCORS: true,
    });
    downloadjs(canvas.toDataURL(), "healthIssuranceCard.png");
  }, []);
  return (
    <Card
      sx={{
        background: cardColor,
        minHeight: "270px",
        maxWidth: 400,
        maxHeight: 350,
        padding: "10px",
      }}
      raised
      elevation={7}
    >
      <Grid container spacing={1} height="100%">
        <Grid container item xs={12} alignItems="center" spacing="7px">
          <Grid item xs={1.5}>
            <Image
              src="/images/steag.png"
              width="100%"
              height="100%"
              objectFit="contain"
            />
          </Grid>
          <Grid item container xs={9}>
            <Grid
              container
              spacing={1}
              justifyContent="space-between"
              item
              xs={12}
            >
              <Grid item>
                <Typography>Romania</Typography>
              </Grid>
              <Grid item>
                <Typography>CARTE DE IDENTITATE</Typography>
              </Grid>
              <Grid item xs={12}>
                <Divider sx={{ borderWidth: "1px", borderColor: "black" }} />
              </Grid>
              <Grid item xs={12}>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.7rem"
                >
                  Romania
                </Typography>
              </Grid>
            </Grid>
          </Grid>
          <Grid item xs={1.5}>
            <Image
              quality={100}
              src="/images/stema.png"
              width="100%"
              height="100%"
              objectFit="contain"
            />
          </Grid>
        </Grid>
        <Grid container item xs={12}>
          <Grid item xs={3}>
            <Image
              style={{}}
              src="/images/portrait.jpeg"
              width="100%"
              height="125px"
              objectPosition="left"
              objectFit="contain"
            />
          </Grid>
          <Grid container item xs={9}>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Nume / Surname
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {idCardInfo?.lastName}
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Prenume / Name
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {idCardInfo?.firstName}
              </Typography>
            </Grid>
            <Grid container item xs={12}>
              <Grid item xs={2}>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Sex / Sex
                </Typography>
              </Grid>
              <Grid item xs={5}>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Cetatenie / Nationality
                </Typography>
              </Grid>
              <Grid item xs={5}>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Data Nasterii / Date of Birth
                </Typography>
              </Grid>
              <Grid item xs={2}>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {idCardInfo.sex}
                </Typography>
              </Grid>
              <Grid item xs={5}>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  RO
                </Typography>
              </Grid>
              <Grid item xs={5}>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {getBirthDateFromCnp(idCardInfo?.cnp)}
                </Typography>
              </Grid>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                CNP / PIN
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                283021308123492
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Adresa / Address
              </Typography>
              <Typography fontSize="0.7rem">{idCardInfo?.address}</Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                NR. Document / Document Number
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {idCardInfo?.series} {idCardInfo?.seriesNumber}
              </Typography>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Card>
  );
};
