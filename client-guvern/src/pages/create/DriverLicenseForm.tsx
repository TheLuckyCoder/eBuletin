import {Box, Button, Card, CardContent, Grid, Typography} from "@mui/material";
import React from "react";
import {useForm} from "react-hook-form";
import {ControlledTextField} from "../../components";
import * as Yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {DriverLicense} from "../../types/transaction";

const formSchema = Yup.object({
  blockchainAddress: Yup.string().required(),
  lastName: Yup.string().required(),
  firstName: Yup.string().required(),
  placeAndDateOfBirth: Yup.string().required(),
  issueDate: Yup.string().required(),
  expirationDate: Yup.string().required(),
  issuedBy: Yup.string().required(),
  licenseNumber: Yup.string().required(),
  validFrom: Yup.string().required(),
  validUntil: Yup.string().required(),
  categories: Yup.string().required(),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

interface FormDriverLicense extends DriverLicense {
  blockchainAddress: string
}

export const DriverLicenseForm = () => {
  const {
    handleSubmit,
    control,
    formState: {errors},
  } = useForm<FormDriverLicense>(formConfig);

  const handleRegister = (data: FormDriverLicense) => {
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
              Creează permis de conducere
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
                <ControlledTextField name="placeAndDateOfBirth" label="Data și locul nașterii" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="issueDate" label="Data eliberării" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="expirationDate" label="Data expirării" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="issuedBy" label="Eliberat de" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="licenseNumber" label="Numărul permisului" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="validFrom" label="Valabilitate" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="validUntil" label="Emisă de" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="categories" label="Coduri" control={control}/>
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
