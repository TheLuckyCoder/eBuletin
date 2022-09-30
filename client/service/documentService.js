import axios from "axios";

const base = "https://server.aaconsl.com/blockchain/citizen/";

const baseLogin = "http://192.168.69.83:11225/";
("https://server.aaconsl.com/blockchain/");

export const registerRequest = async (transaction) => {
  return axios.put(baseLogin + "submit_transaction", transaction);
};

export const loginRequest = async (address, signedAddress) => {
  return axios.post(baseLogin + "login", {
    address,
    signedAddress,
  });
};

export const loginWithCode = async (address, signedAddress, code) => {
  return axios.post(baseLogin + "loginWithCode", {
    address,
    signedAddress,
    code,
  });
};

export const getIdCardReq = async (pubKey) => {
  return axios.get(baseLogin + "citizen/buletin/" + pubKey, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("bearerToken"),
    },
  });
};

export const getMedicalCard = async (pubKey) => {
  return axios.get(baseLogin + "citizen/medical_card/" + pubKey, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("bearerToken"),
    },
  });
};

export const getDriverLicense = async (pubKey) => {
  return axios.get(baseLogin + "citizen/driver_license/" + pubKey, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("bearerToken"),
    },
  });
};
