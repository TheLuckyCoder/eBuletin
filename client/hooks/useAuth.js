import { useRouter } from "next/router";
import React, { useEffect, useState } from "react";
import { AuthContext } from "../authContext";
import { getErrorMessage } from "../helpers/general";
import CryptoJS from "crypto-js";
import { PrivateKey } from "eciesjs";
import { deleteKeys } from "../helpers/auth";

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
    console.log("encrypting private key", privateKey);
    const encryptedPrivateKey = CryptoJS.AES.encrypt(
      JSON.stringify(obj),
      password
    ).toString();
    console.log(
      "equal?: ",
      encryptedPrivateKey === decryptPrivateKey(encryptedPrivateKey, password)
    );
    return encryptedPrivateKey;
  };

  // 0x6f6ecad28dbc2148627e1227b4577a1e26018962567ed96ffbf4f4eed5260762

  const decryptPrivateKey = (encryptedPrivateKey, password) => {
    const bytes = CryptoJS.AES.decrypt(encryptedPrivateKey, password);
    console.log(bytes.toString(CryptoJS.enc.Utf8));
    const decryptedPrivateKey = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
    if (decryptedPrivateKey.loggedIn) {
      console.log("decryptedPrivateKey", decryptedPrivateKey.privateKey);
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

    return { publicKey: pubKeyStr, privateKey: privateKeyStr };
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
      const { privateKey, password } = data;
      console.log("privateKey", privateKey);
      const encryptedPrivateKey = encryptPrivateKey(privateKey, password);
      setEncryptedPrivateKey(encryptedPrivateKey);
      setPrivateKey(privateKey);
      window.localStorage.setItem("encryptedPrivateKey", encryptedPrivateKey);
      window.sessionStorage.setItem("privateKey", privateKey);
      setIsAuthenticated(true);
    } catch (e) {
      console.error(e);
      setError(e.message || "Something went wrong");
    }
    setIsLoading(false);
  };

  const removeKeys = () => {
    deleteKeys();
    setEncryptedPrivateKey(null);
    setPrivateKey(null);
    setIsAuthenticated(false);
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
      if (data.privateKey !== null) {
        window.localStorage.setItem(
          "encryptedPrivateKey",
          encryptPrivateKey(data.privateKey, data.password)
        );
        window.sessionStorage.setItem("privateKey", data.privateKey);
      }
      setIsAuthenticated(true);
      downloadPrivateKey(data.privateKey);
      router.push("/");
    } catch (error) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const logout = () => {
    deleteKeys();
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
    removeKeys,
  };
};
