import {Box, Button, Card, CardContent, Grid, Typography,} from "@mui/material";
import React from "react";
import {useForm} from "react-hook-form";
import {ControlledTextField} from "../../components";
import * as Yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {IIdCard} from "../../types/transaction";

const formSchema = Yup.object({
  blockchainAddress: Yup.string().required(),
  cnp: Yup.number().required(),
  lastName: Yup.string().required(),
  firstName: Yup.string().required(),
  birthLocation: Yup.string().required(),
  sex: Yup.string().required(),
  series: Yup.string().required(),
  seriesNumber: Yup.string().required(),
  validity: Yup.string().required(),
  issuedBy: Yup.string().required(),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

interface FormIdCard extends IIdCard {
  blockchainAddress: string
}

export const IdCardForm = () => {

  const {
    handleSubmit,
    control,
    formState: {errors},
  } = useForm<FormIdCard>(formConfig);

  const handleRegister = (data: FormIdCard) => {
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
              Creează card de identitate
            </Typography>

            <Grid container spacing={1}>

              <Grid item xs={12}>
                <ControlledTextField name="blockchainAddress" label="Adresa pe Blockchain" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="cnp" label="CNP" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="lastName" label="Nume" control={control}/>
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField name="firstName" label="Prenume" control={control}/>
              </Grid>
              <Grid item xs={10}>
                <ControlledTextField name="birthLocation" label="Loc de naștere" control={control}/>
              </Grid>
              <Grid item xs={2}>
                <ControlledTextField name="sex" label="Sex" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="series" label="Seria" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="seriesNumber" label="Număr serie" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="validity" label="Valabilitate" control={control}/>
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField name="issuedBy" label="Emisă de" control={control}/>
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
