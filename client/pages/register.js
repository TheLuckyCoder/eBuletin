import {
  Box,
  Button,
  Card,
  CardContent,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import React from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { useAuth } from "../hooks/useAuth";
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useRouter } from "next/router";

const formSchema = Yup.object({
  publicKey: Yup.string()
    .required("Public Key is required")
    .min(20, "Public Key must be at least 20 characters"),
  password: Yup.string()
    .required("Password is required")
    .min(8, "Password must be at least 8 characters"),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref("password"), null], "Passwords must match")
    .required("Confirm Password is required")
    .min(8, "Password must be at least 8 characters"),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
  reValidateMode: "onSubmit",
};

const Register = () => {
  const router = useRouter();
  const { error, isLoading, register, generateKeyPair } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
    setValue,
  } = useForm(formConfig);

  const handleRegister = async (data) => {
    await register(data);
  };

  const handleGenerateKey = async () => {
    const {publicKey, privateKey} = await generateKeyPair(); // private key will be stored in memory for later encryption
    setValue("publicKey", publicKey);
    setValue("privateKey", privateKey);
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
        <form onSubmit={handleSubmit(handleRegister)}>
          <CardContent
            sx={{
              display: "flex",
              gap: 2,
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" textAlign="center">
              Inregistreaza-te
            </Typography>
            <ControlledTextField
              name="publicKey"
              label="Cheie Publica"
              multiline
              rows={4}
              control={control}
              rules={{ required: true }}
            />
            <ControlledTextField
              name="privateKey"
              label="Cheie Privata"
              multiline
              rows={4}
              control={control}
              rules={{ required: true }}
            />
            <Button onClick={handleGenerateKey} variant="outlined">Genereaza Cheiile</Button>
            <ControlledTextField
              name="password"
              label="Parola"
              control={control}
              type="password"
            />
            <ControlledTextField
              name="confirmPassword"
              label="Confirma Parola"
              control={control}
              type="password"
            />
            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Inregistreaza-te!
            </Button>
            <Link
              component="button"
              underline="hover"
              onClick={() => router.push("/login")}
            >
              Logheaza-te
            </Link>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default Register;
