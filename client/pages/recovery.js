import { Button, Card, CardContent, Link, Typography } from "@mui/material";
import { Box } from "@mui/system";
import download from "downloadjs";
import { useRouter } from "next/router";
import React, { useContext } from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import {
  generateEcdsaKeyPair,
  generateSeed,
  getHexPrivateKey,
} from "../helpers/secretPassphrase";

// recover private key from mnemonic phrase

const recovery = () => {
  const { control, handleSubmit } = useForm();
  const router = useRouter();

  const onSubmit = (data) => {
    let { mnemonicPhrase } = data;
    mnemonicPhrase = mnemonicPhrase.trimStart().trimEnd();
    console.log(mnemonicPhrase);
    const seed = generateSeed(mnemonicPhrase);
    const keyPair = generateEcdsaKeyPair(seed);
    const privateKey = getHexPrivateKey(keyPair);
    download(privateKey, "privateKey.txt", "text/plain");
    router.push("/login");
  };

  return (
    <Box
      sx={{
        maxWidth: "600px",
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        width: "100%",
        boxSizing: "border-box",
        padding: "20px",
      }}
    >
      <Card elevation={10}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent
            sx={{
              display: "flex",
              gap: 2,
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" textAlign="center">
              Recupereaza Cheia
            </Typography>
            <ControlledTextField
              name="mnemonicPhrase"
              label="Fraza Secreta"
              multiline
              rows={4}
              control={control}
              rules={{ required: true }}
            />
            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Recupereaza Cheia
            </Button>
            <Box sx={{ display: "flex", gap: 2, flexDirection: "row" }}>
              <Link
                component="button"
                underline="hover"
                onClick={() => router.push("/register")}
              >
                Creaza Cont
              </Link>
              <Link
                component="button"
                underline="hover"
                onClick={() => router.push("/login")}
              >
                Recupereaza Cheia
              </Link>
            </Box>
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default recovery;
