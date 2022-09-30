import {
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import React from "react";
import { useForm } from "react-hook-form";
import { ControlledTextField } from "../../components";
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { IDriverLicense, IMedicalCard } from "../../types/transaction";
import { generatePublicKeyFromPrivateKey } from "../../helpers/keysHelper";
import {
  createTransaction,
  getAddressFromPublicKey,
} from "../../helpers/transaction";
import {
  getNounce,
  putSubmitTransaction,
} from "../../service/GovernmentService";

const formSchema = Yup.object({
  blockchainAddress: Yup.string().required(),
  lastName: Yup.string().required(),
  firstName: Yup.string().required(),
  insuranceCode: Yup.string().required(),
  documentNumber: Yup.string().required(),
  expiryDate: Yup.string().required(),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

interface FormMedicalCard extends IMedicalCard {
  blockchainAddress: string;
}

export const MedicalCardForm = () => {
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<FormMedicalCard>(formConfig);

  const handleRegister = async (data: FormMedicalCard) => {
    try {
      const privateKey = window.sessionStorage.getItem("privateKey");
      if (!privateKey) {
        throw new Error("No private key found");
      }
      const information = { ...data } as any;
      delete information.blockchainAddress;
      const medicalCard = information as IMedicalCard;
      // idCard.cnp = parseInt(idCard.cnp );
      // idCard.seriesNumber = parseInt(idCard.seriesNumber);

      const pubKey = generatePublicKeyFromPrivateKey(privateKey);
      const address = await getAddressFromPublicKey(pubKey);

      const nounce = await getNounce(address);

      const transaction = await createTransaction(
        privateKey,
        data.blockchainAddress,
        { medicalCard },
        nounce
      );

      await putSubmitTransaction(transaction);
      window.alert("Medical card created");
      // register(data);
    } catch (e) {
      console.error(e);
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
              Creează card de sănătate
            </Typography>

            <Grid container spacing={1}>
              <Grid item xs={12}>
                <ControlledTextField
                  name="blockchainAddress"
                  label="Adresa pe Blockchain"
                  control={control}
                />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="lastName"
                  label="Nume"
                  control={control}
                />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="firstName"
                  label="Prenume"
                  control={control}
                />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="insuranceCode"
                  label="Cod de asigurat"
                  control={control}
                />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="documentNumber"
                  label="Număr de document"
                  control={control}
                />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="expiryDate"
                  label="Data expirării"
                  control={control}
                />
              </Grid>
            </Grid>

            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Creează
            </Button>
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};
