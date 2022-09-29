import {
  Box,
  Button,
  Card,
  CardContent,
  Dialog,
  DialogActions,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { useAuth } from "../hooks/useAuth";
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useRouter } from "next/router";
import { generateBip39Mnemonic } from "../helpers/secretPassphrase";
import { LoadingButton } from "@mui/lab";

const formSchema = Yup.object({
  mnemonicPhrase: Yup.string().required("Mnemonic Phrase is required"),
  email: Yup.string().email("Invalid email").required("Email is required"),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
  reValidateMode: "onSubmit",
};

const Register = () => {
  const router = useRouter();
  const { error, isLoading, register } = useAuth();
  const [dialogOpen, setDialogOpen] = React.useState(false);
  const {
    handleSubmit,
    control,
    formState: { errors },
    setValue,
  } = useForm(formConfig);
  const [mnemonicPhrase, setMnemonicPhrase] = React.useState("");
  const [registerCreatedDialog, setRegisterCreatedDialog] =
    React.useState(false);
  const history = useRouter();

  const handleRegister = async (data) => {
    console.log(data);
    try {
      const privateKey = await register(data);
      setRegisterCreatedDialog(privateKey);
    } catch (error) {
      console.error(error);
    }
  };

  const handleGenerateMnemonicPhrase = () => {
    const mnemonicPhrase = generateBip39Mnemonic();
    setValue("mnemonicPhrase", mnemonicPhrase);
    setMnemonicPhrase(mnemonicPhrase);
    setDialogOpen(true);
  };

  useEffect(() => {}, []);

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
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <Box sx={{ padding: "20px" }}>
          <Typography variant="h5" sx={{ marginBottom: "20px" }}>
            Pentru Recuperarea Contului:
          </Typography>
          <Typography variant="body1" sx={{ marginBottom: "20px" }}>
            Scrieti pe o foaie de hartie aceasta fraza si pastrati-o intr-un loc
            sigur. Aceste cuvinte vor fi folosite pentru a recupera contul in
            caz de pierdere a cheii.
          </Typography>
          <Typography
            variant="body1"
            fontWeight="bold"
            sx={{ marginBottom: "20px" }}
          >
            Fraza:{" "}
            <Typography variant="body1" component="span" color="primary">
              {mnemonicPhrase}
            </Typography>
          </Typography>
        </Box>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={!!registerCreatedDialog}
        onClose={() => setRegisterCreatedDialog(false)}
      >
        <Box sx={{ padding: "20px" }}>
          <Typography variant="h5" sx={{ marginBottom: "20px" }}>
            Contul a fost creat!
          </Typography>
          <Typography variant="body1" sx={{ marginBottom: "20px" }}>
            In maxim doua minute ve-ti putea importa contul nou folosiind:
          </Typography>
          <Typography
            display="inline-block"
            sx={{ wordBreak: "break-all" }}
            ml={2}
          >
            - Cheia: {registerCreatedDialog}
          </Typography>
          <Typography display="inline-block" mt={1} ml={2}>
            - o parola care va fii valida doar pentru acest dispozitiv
          </Typography>
          <Typography display="inline-block" mt={3}>
            va rugam sa nu impartasiti cheia cu nimeni altcineva. Daca cumva o
            pierdeti o puteti regenera folosind cele 24 de cuvinte.
          </Typography>
        </Box>
        <DialogActions>
          <Button
            onClick={() => {
              setRegisterCreatedDialog(false);
              history.push("/login");
            }}
          >
            Ok
          </Button>
        </DialogActions>
      </Dialog>

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
              name="mnemonicPhrase"
              label="Mnemonic Phrase"
              multiline
              rows={4}
              control={control}
              rules={{ required: true }}
            />
            <Button onClick={handleGenerateMnemonicPhrase} variant="outlined">
              Genereaza fraza pentru recuperarea contului
            </Button>

            <ControlledTextField name="email" label="Email" control={control} />
            <LoadingButton
              loading={isLoading}
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Inregistreaza-te!
            </LoadingButton>
            <Box sx={{ display: "flex", gap: 2, flexDirection: "row" }}>
              <Link
                component="button"
                underline="hover"
                onClick={() => router.push("/login")}
              >
                Logheaza-te
              </Link>
              <Link
                component="button"
                underline="hover"
                onClick={() => router.push("/recovery")}
              >
                Recupereaza Cheia
              </Link>
            </Box>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};

export default Register;
