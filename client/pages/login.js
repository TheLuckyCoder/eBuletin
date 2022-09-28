import {
  Box,
  Button,
  Card,
  CardContent,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import { enc } from "crypto-js";
import { useRouter } from "next/router";
import React from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { deleteKeys } from "../helpers/auth";
import { useAuth } from "../hooks/useAuth";

const Login = () => {
  const router = useRouter();
  const { error, isLoading, login, encryptedPrivateKey, onImport, removeKeys } =
    useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
    reValidateMode: "onSubmit",
  });

  const onSubmit = (data) => {
    console.log("he?");
    if (!!encryptedPrivateKey) {
      login(data.password);
    } else {
      onImport(data);
    }
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
      <Card elevation={12}>
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
              {!encryptedPrivateKey ? "Importa Cont Existent" : " Log in!"}
            </Typography>
            {!encryptedPrivateKey && (
              <>
                <ControlledTextField
                  name="privateKey"
                  label="Cheie Privata"
                  multiline
                  rows={4}
                  control={control}
                  rules={{ required: true }}
                />
              </>
            )}
            <ControlledTextField
              name="password"
              label="Parola"
              control={control}
              autofill="current-password"
              rules={{ required: true }}
              type="password"
              
            />
            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              {!encryptedPrivateKey ? "Importa" : "Logheaza-te!"}
            </Button>
            <Box sx={{ display: "flex", gap: 1, alignItems: "center" }}>
              <Link
                component="button"
                underline="hover"
                onClick={() =>
                  router.push("/register", null, { shallow: true })
                }
              >
                Inregistreaza-te
              </Link>
              {encryptedPrivateKey && (
                <Link component="button" underline="hover" onClick={removeKeys}>
                  Sterge Cheiile Curente
                </Link>
              )}
            </Box>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default Login;
