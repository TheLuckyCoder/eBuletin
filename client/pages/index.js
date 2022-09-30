import { Grid, Typography } from "@mui/material";
import { Box } from "@mui/system";
import Image from "next/image";
import Wave from "react-wavify";
import { mainColor, secondaryColor, waveColor } from "../colors";
import { DriverLicense } from "../components/DocumentCards/DriverLicense";
import { HealthIssuranceCard } from "../components/DocumentCards/HealthIssuranceCard";
import { IdCard } from "../components/DocumentCards/IdCard";
import { Documents } from "../components/Documents";
import { PublicKeyCompoent } from "../components/PublicKeyCompoent";
import { useDocuments } from "../hooks/useDocuments";
import withAuth from "../withAuth";

function Home() {
  const { medicalCard, driverLicense, idCard, pubKey, keysError,  address} = useDocuments();

  if (idCard.loading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Box
        sx={{
          background: mainColor,
          width: "100%",
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "20px",
            flexWrap: "wrap",
            rowGap: "20px",
            maxWidth: "1000px",
            margin: "auto",
          }}
        >
          <Box maxWidth="400px">
            <Typography
              color={secondaryColor}
              variant="h2"
              sx={{ opacity: "0.95" }}
            >
              Salut, <br />
              {idCard.data?.firstName ? `${idCard.data.firstName}` : ""}
            </Typography>
            <Typography
              color={secondaryColor}
              variant="body"
              sx={{ opacity: "0.95" }}
            >
              pe pagina accesta ai acces la toate documentele tale
            </Typography>
          </Box>
          <Image
            style={{
              margin: "auto",
            }}
            objectFit="contain"
            objectPosition="center"
            src="/images/homeIlustration.svg"
            alt="Logo"
            width={400}
            height={170}
            quality={100}
          />
        </Box>
      </Box>
      <Wave
        style={{
          position: "relative",
          left: "0",
          height: "71px",
          bottom: "100px",
          top: "-0.5px",
          background: mainColor,
        }}
        fill="#d9dfe9"
        paused={false}
        options={{
          height: 30,
          amplitude: 20,
          speed: 0.2,
          points: 5,
        }}
      />
      <PublicKeyCompoent error={keysError} publicKey={address} />
      <Documents idCard={idCard}  medicalCard={medicalCard} driverLicense={driverLicense} />
    </>
  );
}

export default withAuth(Home);
