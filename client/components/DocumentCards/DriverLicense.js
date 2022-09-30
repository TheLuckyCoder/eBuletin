import { Card, Divider, Grid, IconButton, Typography } from "@mui/material";
import Image from "next/image";
import React, { useCallback } from "react";
import { cardColor } from "../../colors";
import downloadjs from "downloadjs";
import html2canvas from "html2canvas";

export const DriverLicense = ({ driverLicense }) => {
  const downloadIdCard = useCallback(async () => {
    const container = document.getElementById("drivareLicense");
    const root = container.create;
    const canvas = await html2canvas(container, {
      useCORS: true,
    });
    downloadjs(canvas.toDataURL(), "permis.png");
  }, []);
  console.log(driverLicense);
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
        id="drivareLicense"
      >
        <Grid container spacing={1.5}>
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
            {/* <Grid item xs={12}>
              <Image
                src="/images/portrait.jpeg"
                width="77px"
                height="100%"
                objectFit="contain"
              />
            </Grid> */}
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
                1. {driverLicense.firstName}
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                2. {driverLicense.lastName}
              </Typography>
            </Grid>
            <Grid item xs={12}>
              <Typography textTransform="uppercase" fontSize="0.7rem">
                3. {driverLicense.placeAndDateOfBirth}
              </Typography>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Grid item>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  4a. {driverLicense.validFrom}
                </Typography>
              </Grid>
              <Grid item>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  4c. {driverLicense.issuedBy}
                </Typography>
              </Grid>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Grid item>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  4b. {driverLicense.validUntil}
                </Typography>
              </Grid>
              <Grid item>
                <Typography textTransform="uppercase" fontSize="0.7rem">
                  4d. {driverLicense.licenseNumber}
                </Typography>
              </Grid>
            </Grid>
          </Grid>
          <Grid container item xs={3}></Grid>
        </Grid>
        <Grid item container xs={12} spacing={0.5} alignItems="center">
          <Grid item>
            <Typography
              display="inline-block"
              textTransform="uppercase"
              fontSize="0.7rem"
            >
              9.
            </Typography>
          </Grid>
          {driverLicense.categories.split(" ").map((category, index) => (
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
          <Grid item marginLeft="auto">
            <IconButton
              data-html2canvas-ignore="true"
              onClick={downloadIdCard}
              sx={{ ml: "auto" }}
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
      </Card>
    </div>
  );
};
