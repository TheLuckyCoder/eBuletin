import React from "react";
import { getIdCardReq } from "../service/documentService";
import { useKeys } from "./useKeys";
import { decrypt, PrivateKey, encrypt } from "eciesjs";

export const useDocuments = () => {
  const { pubKey, privateKey } = useKeys();
  const [idCard, setIdCard] = React.useState({
    data: null,
    loading: true,
    error: null,
  });

  const initIdCard = async () => {
    try {
      const resp = await getIdCardReq(pubKey);
      const encryptedData = resp.data;
      const encr = encrypt(pubKey, "this is a message");
      // transform encrypted data to array buffer

      const encryptedDataArrayBuffer = Buffer.from(encryptedData, "hex");
      const decryptedData = decrypt(privateKey, encryptedDataArrayBuffer);
      setIdCard({
        data: JSON.parse(decryptedData),
        loading: false,
        error: null,
      });
    } catch (e) {
      console.error(e);
      setIdCard({
        data: null,
        loading: false,
        error: e,
      });
    }
  };

  React.useEffect(() => {
    initIdCard();
  }, []);

  return {
    idCard,
  };
};
