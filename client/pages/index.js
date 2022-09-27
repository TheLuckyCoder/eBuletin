import { Grid, Typography } from "@mui/material";
import { Box } from "@mui/system";
import Image from "next/image";
import Wave from "react-wavify";
import { IdCard } from "../components/DocumentCards/IdCard";
import { useDocuments } from "../hooks/useDocuments";
import withAuth from "../withAuth";

function Home() {
  const { idCard } = useDocuments();

  if (idCard.loading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Grid
        container
        justifyContent="center"
        alignItems="center"
        spacing={2}
        sx={{ backgroundColor: "#ABBBE1", padding: 2 }}
      >
        <Grid item xs={12}>
          <Typography
            color="white"
            variant="h1"
            fontWeight="400"
            sx={{ opacity: "0.95" }}
          >
            Salut, <br />
            {idCard.data.firstName}
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
          height: "60px",
          bottom: "0px",
          background: "#ABBBE1",
        }}
        fill="#d9dfe9"
        paused={false}
        options={{
          height: 25,
          amplitude: 20,
          speed: 0.2,
          points: 5,
        }}
      />
      <Box padding={2}>
        <IdCard idCardInfo={idCard.data} />
      </Box>
    </>
  );
}

export default withAuth(Home);
