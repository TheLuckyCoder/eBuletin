import {
  Box,
  Button,
  Card,
  CardContent,
  Dialog,
  DialogContent,
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
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import LoadingButton from "@mui/lab/LoadingButton";

const formSchema = Yup.object({
  mnemonicPhrase: Yup.string().required("Mnemonic Phrase is required"),
  email: Yup.string().email("Invalid email").required("Email is required"),
  password: Yup.string()
    .required("Password is required")
    .min(8, "Password must be at least 8 characters"),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref("password"), null], "Passwords must match")
    .required("Confirm Password is required")
    .min(8, "Password must be at least 8 characters"),
}).required();

const Login = () => {
  const router = useRouter();
  const {
    error,
    isLoading,
    onGeneralLogin,
    encryptedPrivateKey,
    removeKeys,
    dialog2FactorOpen,
    dialogData,
    setDialog2FactorOpen,
    error2Fa,
    loading2Fa,
    login2Factor,
    setError2Fa,
  } = useAuth();
  const [code, setCode] = React.useState("");

  const handleCodeChange = (event) => {
    setCode(event.target.value);
  };

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

  const onSubmit = async (data) => {
    await onGeneralLogin(data);
  };

  const on2Factor = async () => {
    await login2Factor(parseInt(code));
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
      <Dialog
        open={dialog2FactorOpen}
        onClose={() => {
          setDialog2FactorOpen(false);
          setError2Fa(null);
        }}
        sx={{
          "& .MuiDialog-paper": {
            maxWidth: "600px",
          },
        }}
      >
        <Box display="flex" gap={2} flexDirection="column" padding={2}>
          <Typography variant="h4">
            Completeaza cu codul primit pe email
          </Typography>
          <TextField
            id="outlined-basic"
            label="Cod 2Factor"
            onChange={handleCodeChange}
            variant="outlined"
            type="number"
          />
          <LoadingButton
            loading={loading2Fa}
            variant="contained"
            onClick={on2Factor}
          >
            Login
          </LoadingButton>
          {error2Fa && <Typography color="error">{error2Fa}</Typography>}
        </Box>
      </Dialog>
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
            <LoadingButton
              variant="contained"
              color="primary"
              loading={isLoading}
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              {!encryptedPrivateKey ? "Importa" : "Logheaza-te!"}
            </LoadingButton>
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
              <Link
                component="button"
                underline="hover"
                onClick={() =>
                  router.push("/recovery", null, { shallow: true })
                }
              >
                Recupereaza Cheia
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
