import { useRouter } from "next/router";
import React, { useEffect, useState } from "react";
import { AuthContext } from "../authContext";
import { getErrorMessage } from "../helpers/general";

export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated } = React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = useState(null);
  const router = useRouter();

  const login = async (username, password) => {
    setIsLoading(true);
    try {
      setIsAuthenticated(true);
      router.asPath("/");
    } catch (error) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const register = async (data) => {
    setIsLoading(true);
    try {
      setIsAuthenticated(true);
      navigate("/");
    } catch (error) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const logout = () => {
    setIsAuthenticated(false);
    navigate("/login");
  };

  return { logout, isAuthenticated, login, isLoading, error, register };
};
