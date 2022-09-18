import axios from "axios";
import { IBlock } from "../types/block";

const API_URL = "https://server.aaconsl.com/blockchain";

export const getBlocksReq = async (): Promise<IBlock[]> => {
  return (await axios.get(`${API_URL}/blocks`))?.data;
};
