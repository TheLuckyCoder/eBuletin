import { decrypt, PrivateKey, encrypt } from "eciesjs";
import * as secp from "@noble/secp256k1";

export const getErrorMessage = (e) => {
  return e.message || "error";
};

export const decryptJsonData = (encryptedData, privateKey) => {
  const encryptedDataArrayBuffer = Buffer.from(encryptedData, "hex");
  const decryptedData = decrypt(privateKey, encryptedDataArrayBuffer);
  return JSON.parse(decryptedData);
};

export const getAddressFromPublicKey = async (publicKey) => {
  const uint8Array = Buffer.from(publicKey, "hex");
  const shaBuffer = await secp.utils.sha256(uint8Array);
  const shaChar = Buffer.from(shaBuffer).toString("hex");
  const last20Char = shaChar.slice(shaChar.length - 20);
  return "0x" + last20Char.toString("hex");
};
