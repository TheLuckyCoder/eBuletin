import { Grid, Typography } from "@mui/material";
import { Box } from "@mui/system";
import Image from "next/image";
import Wave from "react-wavify";
import { mainColor, secondaryColor, waveColor } from "../colors";
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
      <Grid
        container
        justifyContent="center"
        alignItems="center"
        spacing={2}
        sx={{
          background: mainColor,
          padding: 2,
        }}
      >
        <Grid item xs={12}>
          <Typography
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
        </Grid>
        <Grid item xs={12}>
          <Image
            style={{
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
        </Grid>
      </Grid>
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
        <Box display="flex" gap={4} flexDirection="column">
          <IdCard idCardInfo={idCard.data} />
          <HealthIssuranceCard healthIssuranceInfo={idCard.data} />
        </Box>
      </Box>
    </>
  );
}

export default withAuth(Home);
