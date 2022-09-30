import { useRouter } from "next/router";
import * as secp from "@noble/secp256k1";
import React, { useEffect, useState } from "react";
import { AuthContext } from "../authContext";
import { getAddressFromPublicKey, getErrorMessage } from "../helpers/general";
import CryptoJS from "crypto-js";
import { PrivateKey } from "eciesjs";
import { deleteKeys } from "../helpers/auth";
import {
  generateEcdsaKeyPair,
  generatePublicKeyFromPrivateKey,
  generateSeed,
  getHexPrivateKey,
  getHexPublicKey,
} from "../helpers/secretPassphrase";
import { createTransaction, signMessage } from "../helpers/transaction";
import {
  loginRequest,
  loginWithCode,
  registerRequest,
} from "../service/documentService";

export const useAuth = () => {
  const { isAuthenticated, setIsAuthenticated, setPrivateKey, privateKey } =
    React.useContext(AuthContext);
  const [isLoading, setIsLoading] = React.useState(false);
  const [error, setError] = useState(null);
  const router = useRouter();
  const [encryptedPrivateKey, setEncryptedPrivateKey] = useState(false);
  const [initializing, setInitializing] = useState(true);
  const [dialog2FactorOpen, setDialog2FactorOpen] = useState(false);
  const [dialogData, setDialogData] = useState(null);
  const [loading2Fa, setLoading2Fa] = useState(false);
  const [error2Fa, setError2Fa] = useState(null);

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
    const decryptedPrivateKey = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
    if (decryptedPrivateKey.loggedIn) {
      console.log("decryptedPrivateKey", decryptedPrivateKey.privateKey);
      return decryptedPrivateKey.privateKey;
    } else {
      throw new Error("Invalid Password");
    }
  };

  const generateKeyPair = async (seed) => {
    const keypair = generateEcdsaKeyPair(seed);
    const privateKey = getHexPrivateKey(keypair);
    const publicKey = getHexPublicKey(keypair);

    return { publicKey: publicKey, privateKey: privateKey };
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
    try {
      const { privateKey, password } = data;
      const encryptedPrivateKey = encryptPrivateKey(privateKey, password);
      return { privateKey, encryptedPrivateKey };
    } catch (e) {
      console.error(e);
      setError(e.message || "Something went wrong");
      throw error;
    }
  };

  const onGeneralLogin = async (data) => {
    setIsLoading(true);
    try {
      let prvKey, encPrv;
      if (!!encryptedPrivateKey) {
        const { privateKey, encryptedPrivateKey } = await login(data.password);
        prvKey = privateKey;
        encPrv = encryptedPrivateKey;
      } else {
        const { privateKey, encryptedPrivateKey } = await onImport(data);
        prvKey = privateKey;
        encPrv = encryptedPrivateKey;
      }
      console.log("privateKey", prvKey);
      const publicKey = await generatePublicKeyFromPrivateKey(prvKey);
      console.log("publicKey", publicKey);
      const address = await getAddressFromPublicKey(publicKey);
      console.log("address", address);
      const signature = await signMessage(prvKey, address);
      await loginRequest(address, signature);
      setDialog2FactorOpen(true);
      setDialogData({ address, signature, encPrv, prvKey });
      setIsLoading(false);
      return { address, signature, encPrv };
    } catch (e) {
      console.error(e);
      setIsLoading(false);
      setError(e.message || "Something went wrong");
      throw error;
    }
  };

  const login2Factor = async (code) => {
    try {
      setLoading2Fa(true);
      setError2Fa(null);
      if (!dialogData.address) {
        throw new Error("Address not found");
      }
      if (!dialogData.signature) {
        throw new Error("Signature not found");
      }
      if (!dialogData.prvKey) {
        throw new Error("Private key not found");
      }
      const { data: bearerToken } = await loginWithCode(
        dialogData.address,
        dialogData.signature,
        code
      );
      window.localStorage.setItem("bearerToken", bearerToken);
      window.sessionStorage.setItem("privateKey", dialogData.prvKey);
      if (dialogData.encPrv) {
        window.localStorage.setItem("encryptedPrivateKey", dialogData.encPrv);
      }
      setDialog2FactorOpen(false);
      setDialogData(null);
      setIsAuthenticated(true);
      setLoading2Fa(false);
      router.push("/");
    } catch (e) {
      setLoading2Fa(false);
      console.error(e);
      setError2Fa(e.message || "Something went wrong");
      throw error;
    }
  };

  const removeKeys = () => {
    deleteKeys();
    setEncryptedPrivateKey(null);
    setPrivateKey(null);
    setIsAuthenticated(false);
  };

  const login = async (password) => {
    setError(false);
    try {
      const privateKey = decryptPrivateKey(encryptedPrivateKey, password);
      setPrivateKey(privateKey);
      window.sessionStorage.setItem("privateKey", privateKey);
      setIsAuthenticated(true);
      return { privateKey, encryptedPrivateKey: null };
    } catch (error) {
      setError("Invalid Password");
      throw error;
    }
  };

  const register = async (data) => {
    setIsLoading(true);
    setError(null);
    try {
      const { email, mnemonicPhrase } = data;
      const seed = generateSeed(mnemonicPhrase);
      const { publicKey, privateKey } = await generateKeyPair(seed);
      const address = await getAddressFromPublicKey(publicKey);
      const signature = await signMessage(privateKey, address);
      console.log("privateKey", privateKey);
      console.log("publicKey", publicKey);
      console.log("address", address);
      console.log("signature", signature);
      const transaction = await createTransaction(privateKey, address, {
        email,
        role: "citizen",
      });
      console.log("transaction", transaction);
      console.log(await registerRequest(transaction));
      downloadPrivateKey(privateKey);
      setIsLoading(false);
      return privateKey;
    } catch (e) {
      console.error(e);
      setError(getErrorMessage(e));
      setIsLoading(false);
      throw e;
    }
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
    onGeneralLogin,
    dialog2FactorOpen,
    dialogData,
    setDialog2FactorOpen,
    login2Factor,
    error2Fa,
    loading2Fa,
    setError2Fa,
  };
};
