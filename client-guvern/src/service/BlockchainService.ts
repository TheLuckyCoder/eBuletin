import axios from "axios";
import { IBlock } from "../types/block";
import {serverUrl} from "./general";

export const getBlocksReq = async (): Promise<IBlock[]> => {
  return (await axios.get(`${serverUrl()}/blocks`))?.data;
};
