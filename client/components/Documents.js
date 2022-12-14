import { Typography } from "@mui/material";
import { Box } from "@mui/system";
import React from "react";
import { DriverLicense } from "./DocumentCards/DriverLicense";
import { HealthIssuranceCard } from "./DocumentCards/HealthIssuranceCard";
import { IdCard } from "./DocumentCards/IdCard";

export const Documents = ({ idCard, medicalCard, driverLicense }) => {
  if (medicalCard.loading || driverLicense.loading || idCard.loading) {
    return <div>Loading...</div>;
  }

  console.log(medicalCard.data);

  if (!idCard.data && !medicalCard.data && !driverLicense.data) {
    // implement 404 for no document
    return (
      <Typography color="primary" textAlign="center" variant="h5" mt={10}>
        Momentan nu ai nici un document
      </Typography>
    );
  }
  return (
    <Box padding={2}>
      <Box
        display="flex"
        gap={4}
        flexWrap="wrap"
        alignItems="center"
        justifyContent="center"
      >
        {idCard.data && (
          <>
            <IdCard idCardInfo={idCard.data} />
          </>
        )}
        {medicalCard.data && (
          <HealthIssuranceCard healthIssuranceInfo={medicalCard.data} />
        )}
        {driverLicense.data && (
          <DriverLicense driverLicense={driverLicense.data} />
        )}
      </Box>
    </Box>
  );
};
