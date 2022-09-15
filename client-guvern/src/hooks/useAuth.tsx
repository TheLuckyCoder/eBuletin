import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthProvider";
import { deleteAllCookies, isLoggedIn } from "../helpers/authHelper";
import { getErrorMessage } from "../helpers/errors";
import { signIn, signUp } from "../service/AuthService";

export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated } = React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = React.useState<null | string>(null);
  const navigate = useNavigate();

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      setIsAuthenticated(true);
      navigate("/");
    } catch (error: any) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const register = async (data: RegisterInput) => {
    setIsLoading(true);
    try {
      const resp = await signUp(data);
      setIsAuthenticated(true);
      navigate("/");
    } catch (error: any) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const logout = () => {
    setIsAuthenticated(false);
    navigate("/login");
    deleteAllCookies();
  };

  return { logout, isAuthenticated, login, isLoading, error, register };
};
