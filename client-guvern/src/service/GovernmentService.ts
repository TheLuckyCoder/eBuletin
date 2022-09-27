import axios from "axios";
import {serverUrl} from "./general";
import {ITransaction} from "../types/transaction";

export const putSubmitTransaction = async (transaction: ITransaction): Promise<String> => {
    return (await axios.put(`${serverUrl()}/government/submit_transaction`, transaction))?.data;
};
