import { Card, Typography } from "@mui/material";
import { Box } from "@mui/system";
import React from "react";
import { backgroundColor, cardColor } from "../colors";

export const PublicKeyCompoent = ({ publicKey, error }) => {
  const copyToClipboard = async () => {
    await navigator.clipboard.writeText(publicKey);
    window.alert("Adresa a fost copiata in clipboard");
  };

  if (error) {
    return (
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          padding: "20px",
          flexWrap: "wrap",
          rowGap: "20px",
          maxWidth: "1000px",
          margin: "auto",
        }}
      >
        <Typography color="error" variant="body" sx={{ opacity: "0.95" }}>
          KeyError: {error}
        </Typography>
      </Box>
    );
  }

  return (
    <Box mt={3} p={2}>
      <Box
        sx={{
          maxWidth: "600px",
          margin: "auto",
          p: 2,
        }}
      >
        <Typography
          textAlign="center"
          variant="h4"
          noWrap
          color="primary"
          gutterBottom
        >
          Adresa ta pe Blockchain:
        </Typography>
        <Typography
          sx={{ wordWrap: "break-word", cursor: "pointer" }}
          textAlign="center"
          color="primary"
          onClick={copyToClipboard}
        >
          {publicKey}
        </Typography>
      </Box>
    </Box>
  );
};
