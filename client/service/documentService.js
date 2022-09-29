import axios from "axios";

const base = "https://server.aaconsl.com/blockchain/citizen/";

const baseLogin = "https://server.aaconsl.com/blockchain/";

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
  return axios.get(base + "buletin/" + pubKey);
};
