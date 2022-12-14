import { CircularProgress } from "@mui/material";
import ResponsiveDrawer from "./components/ResponsiveDrawer";
import { useAuth } from "./hooks/useAuth";
import Login from "./pages/login";

const withAuth = (Component) => {
  const Auth = (props) => {
    // Login data added to props via redux-store (or use react context for example)
    const { isAuthenticated, isLoading, error, initializing } = useAuth();

    if (initializing) {
      return <CircularProgress />;
    }

    // If user is not logged in, return login component
    if (!isAuthenticated) {
      return <Login />;
    }

    // If user is logged in, return original component
    return (
      <ResponsiveDrawer>
        <Component {...props} />
      </ResponsiveDrawer>
    );
  };

  // Copy getInitial props so it will run as well
  if (Component.getInitialProps) {
    Auth.getInitialProps = Component.getInitialProps;
  }

  return Auth;
};

export default withAuth;
