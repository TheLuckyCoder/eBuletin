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

export const getAddressFromPublicKey = async (publicKey) => {
  const uint8Array = Buffer.from(publicKey, "hex");
  const shaBuffer = await secp.utils.sha256(uint8Array);
  const last20Bytes = shaBuffer.slice(shaBuffer.length - 20);
  const shaChar = Buffer.from(last20Bytes).toString("hex");
  return "0x" + (shaChar).toUpperCase();
};
