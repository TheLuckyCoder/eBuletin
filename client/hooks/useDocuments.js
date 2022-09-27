import React from "react";
import { getIdCardReq } from "../service/documentService";
import { useKeys } from "./useKeys";
import { handleError, handleSuccess } from "../helpers/documentHelpers";
import { decryptJsonData } from "../helpers/general";

export const useDocuments = () => {
  const { pubKey, privateKey } = useKeys();
  const [idCard, setIdCard] = React.useState({
    data: null,
    loading: true,
    error: null,
  });

  const initIdCard = async () => {
    try {
      const { data } = await getIdCardReq(pubKey);
        handleSuccess(decryptJsonData(data, privateKey), setIdCard);
    } catch (e) {
      console.error(e);
      handleError(e, setIdCard);
    }
  };

  React.useEffect(() => {
    initIdCard();
  }, []);

  return {
    idCard,
  };
};
