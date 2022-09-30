import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthProvider";
import { deleteAllCookies, isLoggedIn } from "../helpers/authHelper";
import { getErrorMessage } from "../helpers/errors";
import { generatePublicKeyFromPrivateKey } from "../helpers/keysHelper";
import { getAddressFromPublicKey, signMessage } from "../helpers/transaction";
import { signIn, signUp } from "../service/AuthService";
import { loginReq, loginWith2FaReq } from "../service/GovernmentService";

export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated } = React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = React.useState<null | string>(null);
  const [show2Fa, setShow2Fa] = React.useState(false);
  const navigate = useNavigate();

  const login = async (privateKey: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const publicKey = generatePublicKeyFromPrivateKey(privateKey);
      console.log("publicKey", publicKey);
      const address = await getAddressFromPublicKey(publicKey);
      console.log(address);
      const signedAddress = await signMessage(privateKey, address);
      await loginReq(address, signedAddress);
      setShow2Fa(true);
    } catch (error: any) {
      console.error(error);
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const loginWith2Fa = async (privateKey: string, code: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const publicKey = generatePublicKeyFromPrivateKey(privateKey);
      const address = await getAddressFromPublicKey(publicKey);
      const signedAddress = await signMessage(privateKey, address);
      const token = await loginWith2FaReq(address, signedAddress, code);
      console.log(token);
      window.localStorage.setItem("bearerToken", token);
      window.sessionStorage.setItem("privateKey", privateKey);
      setIsAuthenticated(true);
      navigate("/");
    } catch (error: any) {
      console.error(error);
      setShow2Fa(false);
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const generalLogin = async (privateKey: string, code: number) => {
    if (!show2Fa) {
      await login(privateKey);
    } else {
      await loginWith2Fa(privateKey, code);
    }
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
    window.sessionStorage.removeItem("privateKey");
    window.localStorage.removeItem("bearerToken");
  };

  return {
    logout,
    isAuthenticated,
    login,
    isLoading,
    error,
    register,
    generalLogin,
    show2Fa,
  };
};
