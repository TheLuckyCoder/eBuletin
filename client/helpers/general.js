import { decrypt, PrivateKey, encrypt } from "eciesjs";

export const getErrorMessage = (e) => {
  return e.message || "error";
};

export const decryptJsonData = (encryptedData, privateKey) => {
  const encryptedDataArrayBuffer = Buffer.from(encryptedData, "hex");
  const decryptedData = decrypt(privateKey, encryptedDataArrayBuffer);
  return JSON.parse(decryptedData);
};
