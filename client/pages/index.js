import { Grid, Typography } from "@mui/material";
import { Box } from "@mui/system";
import Image from "next/image";
import Wave from "react-wavify";
import { mainColor, secondaryColor, waveColor } from "../colors";
import { DriverLicense } from "../components/DocumentCards/DriverLicense";
import { HealthIssuranceCard } from "../components/DocumentCards/HealthIssuranceCard";
import { IdCard } from "../components/DocumentCards/IdCard";
import { useDocuments } from "../hooks/useDocuments";
import withAuth from "../withAuth";

function Home() {
  const { idCard } = useDocuments();

  if (idCard.loading) {
    return <div>Loading...</div>;
  }

  if (idCard.error) {
    return <div>{idCard.error}</div>;
  }

  return (
    <>
      <Box
        sx={{
          background: mainColor,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "20px",
          flexWrap: "wrap",
          rowGap: "20px",
        }}
      >
        <Box
          sx={{
            flex: "1 1 30%",
            maxWidth: "450px",
            minWidth: " 250px",
          }}
        >
          <Typography
            maxWidth="400px"
            color={secondaryColor}
            variant="h1"
            sx={{ opacity: "0.95" }}
          >
            Salut, <br />
            {idCard.data.firstName}
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
            flex: "1 1 50%",
            minWidth: "301px",
            minHeight: "250px",
            maxWidth: "450px",
            alignSelf: "center",
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
      <Wave
        style={{
          position: "relative",
          left: "0",
          height: "71px",
          bottom: "100px",
          top: "-0.5px",
          background: waveColor,
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
      <Box padding={2}>
        <Box
          display="flex"
          gap={4}
          flexWrap="wrap"
          alignItems="center"
          justifyContent="center"
        >
          <IdCard idCardInfo={idCard.data} />
          <HealthIssuranceCard healthIssuranceInfo={idCard.data} />
          <DriverLicense />
        </Box>
      </Box>
    </>
  );
}

export default withAuth(Home);
