import {
  Box,
  Button,
  Card,
  CardContent,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import { useRouter } from "next/router";
import React from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { useAuth } from "../hooks/useAuth";

const Login = () => {
  const router = useRouter();
  const { error, isLoading, login, encryptedPrivateKey, onImport } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
  });

  const onSubmit = (data) => {
    if (!encryptedPrivateKey) {
      console.log("import ");
      onImport(data);
    } else {
      login(data.password);
    }
  };
  console.log(encryptedPrivateKey, !encryptedPrivateKey);

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
              {!encryptedPrivateKey ? "Import Account" : " Log in!"}
            </Typography>
            {!encryptedPrivateKey && (
              <ControlledTextField
                name="privateKey"
                label="Private Key"
                multiline
                rows={4}
                control={control}
                rules={{ required: true }}
              />
            )}
            <ControlledTextField
              name="password"
              label="Password"
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
              {!encryptedPrivateKey ? "Import" : "Log in!"}
            </Button>
            <Link
              component="button"
              underline="hover"
              onClick={() => router.push("/register", null, { shallow: true })}
            >
              Register
            </Link>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default Login;
