import React from "react";
import {
  getDriverLicense,
  getIdCardReq,
  getMedicalCard,
} from "../service/documentService";
import { useKeys } from "./useKeys";
import { handleError, handleSuccess } from "../helpers/documentHelpers";
import { decryptJsonData } from "../helpers/general";
import { ConstructionOutlined } from "@mui/icons-material";

export const useDocuments = () => {
  const { pubKey, privateKey, loadingKeys, keysError, address } = useKeys();
  const [idCard, setIdCard] = React.useState({
    data: null,
    loading: true,
    error: null,
  });
  const [driverLicense, setDriverLicense] = React.useState({
    data: null,
    loading: true,
    error: null,
  });
  const [medicalCard, setMedicalCard] = React.useState({
    data: null,
    loading: true,
    error: null,
  });

  const initDriverLicense = async () => {
    try {
      const { data } = await getDriverLicense(pubKey);
      handleSuccess(decryptJsonData(data, privateKey), setDriverLicense);
    } catch (e) {
      console.error(e);
      handleError(e, setDriverLicense);
    }
  };

  const initMedicalCard = async () => {
    try {
      const { data } = await getMedicalCard(pubKey);
      handleSuccess(decryptJsonData(data, privateKey), setMedicalCard);
    } catch (e) {
      console.error(e);
      handleError(e, setMedicalCard);
    }
  };

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
    if (!loadingKeys) {
      initIdCard();
      initMedicalCard();
      initDriverLicense();
    }
    if (keysError) {
      setIdCard({ ...idCard, error: keysError });
    }
  }, [loadingKeys, keysError]);

  return {
    idCard,
    pubKey,
    keysError,
    address,
    driverLicense,
    medicalCard,
  };
};
