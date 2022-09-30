import { decrypt, PrivateKey, encrypt } from "eciesjs";
import * as secp from "@noble/secp256k1";

export const getErrorMessage = (e) => {
  return e.message || "error";
};

export const encryptJsonData = (publicKey, data) => {
  const encrypted = encrypt(publicKey, JSON.stringify(data));
  return encrypted;
};

export const decryptJsonData = (encryptedData, privateKey) => {
  const encryptedDataArrayBuffer = Buffer.from(encryptedData, "hex");
  const decryptedData = decrypt(privateKey, encryptedDataArrayBuffer);
  return JSON.parse(decryptedData);
};

export const getAddressFromPublicKey = async (pub) => {
  // remove "04"
  let publicKey = pub;
  if (publicKey.substring(0, 2) == "04") {
    publicKey = publicKey.substring(2);
  }
  const textAsBuffer = new TextEncoder().encode(publicKey);
  const shaBuffer = await window.crypto.subtle.digest("SHA-256", textAsBuffer);
  console.log(shaBuffer);
  const hashArray = Array.from(new Uint8Array(shaBuffer));
  const digest = hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
  const last40Char = digest.substring(digest.length - 40);
  console.log(last40Char);
  return "0x" + last40Char;
};
