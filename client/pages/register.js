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
  privateKey: Yup.string()
    .required("Private Key is required")
    .min(20, "Private Key must be at least 4 characters"),
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
};

const Register = () => {
  const router = useRouter();
  const { error, isLoading, register } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm(formConfig);

  const handleRegister = (data) => {
    register(data);
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
      <Card>
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
              Register
            </Typography>
            <ControlledTextField
              name="privateKey"
              label="Private Key"
              control={control}
            />
            <ControlledTextField
              name="password"
              label="Password"
              control={control}
              type="password"
            />
            <ControlledTextField
              name="confirmPassword"
              label="Confirm Password"
              control={control}
              type="password"
            />
            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Register
            </Button>
            <Link
              component="button"
              underline="hover"
              onClick={() => router.push("/login")}
            >
              Login
            </Link>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default Register;
