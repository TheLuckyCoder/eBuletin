import { LoadingButton } from "@mui/lab";
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
import { useNavigate } from "react-router-dom";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { useAuth } from "../hooks/useAuth";

export const Login = () => {
  const navigate = useNavigate();
  const { error, isLoading, generalLogin, show2Fa } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<LoginInput>({
    defaultValues: {
      privateKey: "",
      code: undefined,
    },
  });

  const handleLogin = (data: LoginInput) => {
    generalLogin(data.privateKey, data.code);
  };

  return (
    <Box>
      <Card
        sx={{
          maxWidth: "600px",
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          width: "100%",
        }}
      >
        <form onSubmit={handleSubmit(handleLogin)}>
          <CardContent
            sx={{
              display: "flex",
              gap: 2,
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" textAlign="center">
              Log in!
            </Typography>
            <ControlledTextField
              name="privateKey"
              label="Private key"
              autofill="username"
              control={control}
              rules={{ required: true }}
            />
            {show2Fa && (
              <ControlledTextField
                name="code"
                label="2FA Code"
                control={control}
                rules={{ required: true }}
              />
            )}
            <LoadingButton
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
              loading={isLoading}
            >
              Log in
            </LoadingButton>
            <Link
              component="button"
              underline="hover"
              onClick={() => navigate("../register")}
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
