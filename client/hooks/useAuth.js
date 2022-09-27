import { useRouter } from "next/router";
import React, { useEffect, useState } from "react";
import { AuthContext } from "../authContext";
import { getErrorMessage } from "../helpers/general";
import CryptoJS from "crypto-js";
import { PrivateKey } from "eciesjs";


export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated, setPrivateKey, privateKey } =
    React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = useState(null);
  const router = useRouter();
  const [encryptedPrivateKey, setEncryptedPrivateKey] = useState(false);
  const [initializing, setInitializing] = useState(true);

  const encryptPrivateKey = (privateKey, password) => {
    const obj = {
      privateKey,
      loggedIn: true,
    };
    const encryptedPrivateKey = CryptoJS.AES.encrypt(
      JSON.stringify(obj),
      password
    ).toString();

    return encryptedPrivateKey;
  };

  const decryptPrivateKey = (encryptedPrivateKey, password) => {
    const bytes = CryptoJS.AES.decrypt(encryptedPrivateKey, password);
    console.log(bytes.toString(CryptoJS.enc.Utf8));
    const decryptedPrivateKey = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
    if (decryptedPrivateKey.loggedIn) {
      return decryptedPrivateKey.privateKey;
    } else {
      throw new Error("Invalid Password");
    }
  };

  const generateKeyPair = async (password) => {
    const privateKey = new PrivateKey();
    const publicKey = privateKey.publicKey;

    const pubKeyStr = publicKey.toHex();
    const privateKeyStr = privateKey.toHex();

    setPrivateKey(privateKeyStr);
    return pubKeyStr;
  };

  const downloadPrivateKey = (privateKey) => {
    const element = document.createElement("a");
    const file = new Blob([privateKey], { type: "text/plain" });
    element.href = URL.createObjectURL(file);
    element.download = "privateKey.txt";
    document.body.appendChild(element); // Required for this to work in FireFox
    element.click();
  };

  const onImport = async (data) => {
    setIsLoading(true);
    setError(null);
    try {
      const { privateKey, publicKey, password } = data;
      const encryptedPrivateKey = encryptPrivateKey(privateKey, password);
      setEncryptedPrivateKey(encryptedPrivateKey);
      setPrivateKey(privateKey);
      window.localStorage.setItem("publicKey", publicKey);
      window.localStorage.setItem("encryptedPrivateKey", encryptedPrivateKey);
      window.sessionStorage.setItem("privateKey", privateKey);
      setIsAuthenticated(true);
    } catch (e) {
      console.error(e);
      setError(e.message || "Something went wrong");
    }
    setIsLoading(false);
  };

  const login = async (password) => {
    setIsLoading(true);
    setError(false);
    try {
      const privateKey = decryptPrivateKey(encryptedPrivateKey, password);
      setPrivateKey(privateKey);
      window.sessionStorage.setItem("privateKey", privateKey);
      setIsAuthenticated(true);
      router.push("/");
    } catch (error) {
      setError("Invalid Password");
    }
    setIsLoading(false);
  };

  const register = async (data) => {
    setIsLoading(true);
    try {
      if (privateKey !== null) {
        window.localStorage.setItem(
          "encryptedPrivateKey",
          encryptPrivateKey(privateKey, data.password)
        );
      }
      setIsAuthenticated(true);
      downloadPrivateKey(privateKey);
      router.push("/");
    } catch (error) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const logout = () => {
    setIsAuthenticated(false);
  };

  const onMount = () => {
    setInitializing(false);
    const encryptedPrivateKey = window.localStorage.getItem(
      "encryptedPrivateKey"
    );
    setEncryptedPrivateKey(encryptedPrivateKey);
  };

  useEffect(onMount, []);

  return {
    initializing,
    logout,
    isAuthenticated,
    login,
    isLoading,
    encryptedPrivateKey,
    error,
    register,
    generateKeyPair,
    onImport,
  };
};
