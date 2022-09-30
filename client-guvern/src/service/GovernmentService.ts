import axios from "axios";
import { serverUrl } from "./general";
import { ISignature, ITransaction } from "../types/transaction";

export const putSubmitTransaction = async (
  transaction: ITransaction
): Promise<String> => {
  return (await axios.put(`${serverUrl()}/submit_transaction`, transaction))
    ?.data;
};

export const loginReq = async (address: string, signedAddress: ISignature) => {
  return (await axios.post(`${serverUrl()}/login`, { address, signedAddress }))
    ?.data;
};

export const loginWith2FaReq = async (
  address: string,
  signedAddress: ISignature,
  code: number
) => {
  return (
    await axios.post(`${serverUrl()}/loginWithCode`, {
      address,
      signedAddress,
      code,
    })
  )?.data as string;
};

export const getNounce = async (address: string) => {
  return (
    await axios.get(`${serverUrl()}/government/nonce/${address}`, {
      headers: {
        Authorization: `Bearer ${window.localStorage.getItem("bearerToken")}`,
      },
    })
  ).data as number;
};
