import axios from "axios";

const base = "https://server.aaconsl.com/blockchain/citizen/";

export const getIdCardReq = async (pubKey) => {
  return axios.get(base + "buletin/" + pubKey);
};


