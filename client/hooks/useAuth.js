import { useRouter } from "next/router";
import React, { useEffect, useState } from "react";
import { AuthContext } from "../authContext";
import { getErrorMessage } from "../helpers/general";
import CryptoJS from "crypto-js";

export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated, setPrivateKey, privateKey } =
    React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = useState(null);
  const router = useRouter();
  const [encryptedPrivateKey, setEncryptedPrivateKey] = useState(" ");

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
    const { publicKey, privateKey } = await window.crypto.subtle.generateKey(
      {
        name: "RSA-OAEP",
        modulusLength: 2048,
        publicExponent: new Uint8Array([1, 0, 1]),
        hash: { name: "SHA-256" },
      },
      true,
      ["encrypt", "decrypt"]
    );

    const exportedPrivateKey = await window.crypto.subtle.exportKey(
      "pkcs8",
      privateKey
    );

    const exportedPublicKey = await window.crypto.subtle.exportKey(
      "spki",
      publicKey
    );

    const pubKeyStr = Buffer.from(exportedPublicKey).toString("base64");
    const privateKeyStr = Buffer.from(exportedPrivateKey).toString("base64");

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

  const login = async (password) => {
    setIsLoading(true);
    setError(false);
    try {
      setIsAuthenticated(true);
      try {
        console.log(decryptPrivateKey);
        const privateKey = decryptPrivateKey(encryptedPrivateKey, password);
        setPrivateKey(privateKey);
        router.push("/");
      } catch (e) {
        setError("Invalid Password");
      }
    } catch (error) {
      setError(getErrorMessage(error));
    }
    setIsLoading(false);
  };

  const register = async (data) => {
    console.log("hello");
    setIsLoading(true);
    try {
      window.localStorage.setItem(
        "privateKey",
        encryptPrivateKey(privateKey, data.password)
      );
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
    const encryptedPrivateKey = window.localStorage.getItem("privateKey");
    setEncryptedPrivateKey(encryptedPrivateKey);
  };

  useEffect(onMount, []);

  return {
    logout,
    isAuthenticated,
    login,
    isLoading,
    encryptedPrivateKey,
    error,
    register,
    generateKeyPair,
  };
};
