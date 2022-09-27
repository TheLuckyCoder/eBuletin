import { Card, Divider, Grid, IconButton, Typography } from "@mui/material";
import Image from "next/image";
import React, { useCallback } from "react";
import { cardColor } from "../../colors";
import { getBirthDateFromCnp } from "../../helpers/documentHelpers";
import downloadjs from "downloadjs";
import html2canvas from "html2canvas";
import { Box } from "@mui/system";

export const HealthIssuranceCard = ({ healthIssuranceInfo }) => {
  const downloadHealthIssurance = useCallback(async () => {
    const container = document.getElementById("healthIssuranceCard");
    const root = container.create;
    const canvas = await html2canvas(container, {
      useCORS: true,
    });
    downloadjs(canvas.toDataURL(), "healthIssuranceCard.png");
  }, []);

  return (
    <div
      style={{
        boxShadow: "3px 6px 4px rgba(0, 0, 0, 0.25)",
      }}
    >
      <Card
        sx={{
          background: cardColor,
          minHeight: "220px",
          maxWidth: 400,
          maxHeight: 350,
          padding: "10px",
        }}
        raised
        elevation={0}
        id="healthIssuranceCard"
      >
        <Grid container spacing={1}>
          <Grid
            container
            item
            xs={12}
            justifyContent="space-between"
            alignItems="center"
            spacing={1}
          >
            <Grid item>
              <Typography>
                CARD NATIONAL DE ASIGURARI <br />
                SOCIALE DE SANATATE
              </Typography>
            </Grid>
            <Grid item>
              <Image
                src="/images/LogoCardSanatate.png"
                width="39px"
                height="39px"
                objectFit="contain"
              />
            </Grid>
          </Grid>

          <Grid container item xs={12} height="100%" spacing={0.5}>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Nume
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {healthIssuranceInfo?.lastName}
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Prenume
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {healthIssuranceInfo?.firstName}
              </Typography>
            </Grid>
            <Grid container item xs={12} justifyContent="space-between">
              <Grid item>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Data nasterii
                </Typography>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {getBirthDateFromCnp(healthIssuranceInfo?.cnp)}
                </Typography>
              </Grid>
              <Grid item>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Cod numeric personal
                </Typography>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {healthIssuranceInfo?.cnp}
                </Typography>
              </Grid>
            </Grid>
            <Grid
              container
              item
              xs={12}
              justifyContent="space-between"
              alignItems="center"
            >
              <Grid item>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {healthIssuranceInfo?.cnp}
                </Typography>
              </Grid>
              <Grid item data-html2canvas-ignore="true">
                <IconButton onClick={downloadHealthIssurance}>
                  <div html2canvas-ignore="true">
                    <Image
                      src="/images/share.png"
                      width="20px"
                      height="20px"
                      objectFit="contain"
                      html2canvas-ignore="true"
                    />
                  </div>
                </IconButton>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Card>
    </div>
  );
};
