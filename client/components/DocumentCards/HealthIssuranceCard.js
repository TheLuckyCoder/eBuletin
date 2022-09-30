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
    downloadjs(canvas.toDataURL(), "CardDeSanatate.png");
  }, []);

  return (
    <div
      style={{
        boxShadow:
          "0px 4px 10px -2px rgb(0 0 0 / 20%), 0px 7px 10px 1px rgb(0 0 0 / 14%), 0px 2px 16px 1px rgb(0 0 0 / 12%)",
        borderRadius: "20px",
      }}
    >
      <Card
        sx={{
          background: cardColor,
          minHeight: "270px",
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

          <Grid container item xs={12} spacing={0.5} mt="auto">
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
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Numar Document
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {healthIssuranceInfo?.documentNumber}
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography
                fontWeight="light"
                fontStyle="oblique"
                fontSize="0.5rem"
              >
                Cod de asigurat
              </Typography>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                {healthIssuranceInfo?.insuranceCode}
              </Typography>
            </Grid>
            <Grid container item xs={12} justifyContent="space-between">
              <Grid item>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Data Expirarii
                </Typography>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {healthIssuranceInfo?.expiryDate}
                </Typography>
              </Grid>
              {/* <Grid item>
                <Typography
                  fontWeight="light"
                  fontStyle="oblique"
                  fontSize="0.5rem"
                >
                  Cod numeric personal
                </Typography>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  {healthIssuranceInfo?.}
                </Typography>
              </Grid> */}
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
                <IconButton
                  data-html2canvas-ignore="true"
                  onClick={downloadHealthIssurance}
                >
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
