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
import { IIdCard } from "../../types/transaction";
import { createTracing } from "trace_events";
import {
  createTransaction,
  generateBlockchainAddress,
  getAddressFromPublicKey,
} from "../../helpers/transaction";
import {
  getNounce,
  putSubmitTransaction,
} from "../../service/GovernmentService";
import { generatePublicKeyFromPrivateKey } from "../../helpers/keysHelper";

const formSchema = Yup.object({
  blockchainAddress: Yup.string().required(),
  cnp: Yup.string().required(),
  lastName: Yup.string().required(),
  firstName: Yup.string().required(),
  birthLocation: Yup.string().required(),
  sex: Yup.string().required(),
  series: Yup.string().required(),
  seriesNumber: Yup.string().required(),
  validity: Yup.string().required(),
  issuedBy: Yup.string().required(),
  address: Yup.string().required(),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

interface FormIdCard extends IIdCard {
  blockchainAddress: string;
}

export const IdCardForm = () => {
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<FormIdCard>(formConfig);

  const handleRegister = async (data: FormIdCard) => {
    console.log(data);
    try {
      const privateKey = window.sessionStorage.getItem("privateKey");
      if (!privateKey) {
        throw new Error("No private key found");
      }
      const information = { ...data } as any;
      delete information.blockchainAddress;
      const idCard = information as IIdCard;
      idCard.validity = `"${idCard.validity}"`;
      // idCard.cnp = parseInt(idCard.cnp );
      // idCard.seriesNumber = parseInt(idCard.seriesNumber);

      const pubKey = generatePublicKeyFromPrivateKey(privateKey);
      const address = await getAddressFromPublicKey(pubKey);

      const nounce = await getNounce(address);

      const transaction = await createTransaction(
        privateKey,
        data.blockchainAddress,
        { idCard },
        nounce
      );

      await putSubmitTransaction(transaction);
      window.alert("Id card created");
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
              Creează card de identitate
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
                <ControlledTextField name="cnp" label="CNP" control={control} />
              </Grid>
              <Grid item xs={12}>
                <ControlledTextField
                  name="address"
                  label="Adresa"
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
              <Grid item xs={10}>
                <ControlledTextField
                  name="birthLocation"
                  label="Loc de naștere"
                  control={control}
                />
              </Grid>
              <Grid item xs={2}>
                <ControlledTextField name="sex" label="Sex" control={control} />
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField
                  name="series"
                  label="Seria"
                  control={control}
                />
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField
                  name="seriesNumber"
                  label="Număr serie"
                  control={control}
                />
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField
                  name="validity"
                  label="Valabilitate"
                  control={control}
                />
              </Grid>
              <Grid item xs={6}>
                <ControlledTextField
                  name="issuedBy"
                  label="Emisă de"
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

// {"hash":"ce8fe33d731639af61d7c49a80471c4fc19db89c6e994b343cfd75bdcea3b6f4","sender":"0x1189986f617f9520df364a7ee89674ea61eb6ab5","receiver":"0x3B098BB268B24C40157D681315E94586A50A446C","data":{"information":{"idCard":{"issuedBy":"SDFsdf","validity":"2022-02-01","seriesNumber":324243,"series":"ASD","sex":"M","birthLocation":"adsasd","firstName":"Esan","lastName":"Tudor","cnp":243324}}},"signature":{"v":"1c","r":"be50a71442b5b516ddb421abcb0948934695466df56ddba1635be995efb529f1","s":"47d2fabf5080ed128b81f60b69328be8ea2eecf58e15fefb42dcbf3123c82cf2"},"nonce":0}
