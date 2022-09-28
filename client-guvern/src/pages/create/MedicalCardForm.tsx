import {Box, Button, Card, CardContent, Grid, Typography} from "@mui/material";
import React from "react";
import {useForm} from "react-hook-form";
import {ControlledTextField} from "../../components";
import * as Yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {IDriverLicense, IMedicalCard} from "../../types/transaction";

const formSchema = Yup.object({
  blockchainAddress: Yup.string().required(),
  lastName: Yup.string().required(),
  firstName: Yup.string().required(),
  insuranceCode: Yup.number().required(),
  documentNumber: Yup.number().required(),
  expiryDate: Yup.string().required(),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

interface FormMedicalCard extends IMedicalCard {
  blockchainAddress: string
}

export const MedicalCardForm = () => {
  const {
    handleSubmit,
    control,
    formState: {errors},
  } = useForm<FormMedicalCard>(formConfig);

  const handleRegister = (data: FormMedicalCard) => {
    // register(data);
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
                <ControlledTextField name="blockchainAddress" label="Adresa pe Blockchain" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="lastName" label="Nume" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="firstName" label="Prenume" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="insuranceCode" label="Cod de asigurat" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="documentNumber" label="Număr de document" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="expiryDate" label="Data expirării" control={control}/>
              </Grid>
            </Grid>

            <Button
              variant="contained"
              color="primary"
              sx={{maxWidth: "200px"}}
              type="submit"
            >
              Creează
            </Button>
          </CardContent>
        </form>
      </Card>
    </Box>
  )
};
