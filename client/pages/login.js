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
  const router = useRouter()
  const { error, isLoading, login } = useAuth();
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
  console.log(errors);

  const handleLogin = (data) => {
    login(data.username, data.password);
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
              label="Private Key"
              control={control}
              rules={{ required: true }}
            />
            <ControlledTextField
              name="username"
              label="Username"
              autofill="username"
              control={control}
              rules={{ required: true }}
            />
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
              Log in
            </Button>
            <Link
              component="button"
              underline="hover"
              onClick={() => router.asPath("/register")}
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
