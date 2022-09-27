import { ThemeProvider } from "@emotion/react";
import { Box, CssBaseline } from "@mui/material";
import { createTheme, responsiveFontSizes } from "@mui/material/styles";
import { AuthContext, AuthProvider } from "../authContext";
import "./global.css";

let theme = createTheme({
  typography: {
    htmlFontSize: "400px",
    fontFamily:
      "apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica Neue, sans-serif",
  },
  palette: {
    background: {
      default: "#000000",
    },
    primary: {
      main: "#1B2E4C",
    },
  },
});
theme = responsiveFontSizes(theme);
function MyApp({ Component, pageProps }) {
  return (
    <AuthProvider>
      <CssBaseline />
      <Box
        sx={{
          background: "#D9DFE9",
        }}
      >
        <ThemeProvider theme={theme}>
          <Component {...pageProps} />
        </ThemeProvider>
      </Box>
    </AuthProvider>
  );
}

export default MyApp;
